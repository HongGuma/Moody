package com.example.Moody.Background;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.Moody.Background.Room.AppDatabase;
import com.example.Moody.Background.Room.LikedEntity;
import com.example.Moody.Background.Room.UserEntity;
import com.example.Moody.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDB {
    private static final String TAG = "FirebaseDatabase";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    ArrayList<UserModel> userModels = new ArrayList<UserModel>();

    Context context;
    String uid = currentUser.getUid();
    List<UserEntity> userEntities;

    public FirebaseDB(Context context){
        this.context = context;
    }

    public void DownloadUserData(){
        final ArrayList<String> fid = new ArrayList<String>();
        final ArrayList<String> lid = new ArrayList<String>();

        //친구목록에서 친구 id 가져오기
        database.getReference("friend").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fid.clear();
                //lid.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    fid.add(dataSnapshot1.getKey());

                    //if(dataSnapshot1.getValue().equals(true))
                    //    lid.add(dataSnapshot1.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //유저정보 가져오기
        database.getReference("userInfo").orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    UserModel users = dataSnapshot1.getValue(UserModel.class);
                    for(int i=0;i<lid.size();i++){
                        if(users.getUID().equals(lid.get(i))){
                            userModels.add(users);
                        }
                    }
                }

                SaveDataInRoomDB(userModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });



    }

    public void SaveDataInRoomDB(final ArrayList<UserModel> userList){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(context,AppDatabase.class,"room_db").build();
                //Log.d(TAG, "run: userList="+userList);
                //Log.d(TAG, "run: userModels="+userModels);
                for(int i =0;i<userList.size();i++){
                    UserEntity userEntity = new UserEntity();
                    userEntity.setBirth(userList.get(i).getBirth());
                    userEntity.setConnection(userList.get(i).getConnection());
                    userEntity.setEmail(userList.get(i).getEmail());
                    userEntity.setName(userList.get(i).getName());
                    userEntity.setPassword(userList.get(i).getPassword());
                    userEntity.setRange(userList.get(i).getRange());
                    userEntity.setProfile(userList.get(i).getProfile());
                    userEntity.setUID(userList.get(i).getUID());
                    db.userDAO().insert(userEntity);
                }

                Log.d(TAG, "db="+db.userDAO().debugging());
            }
        });

        thread.start();
    }
}
