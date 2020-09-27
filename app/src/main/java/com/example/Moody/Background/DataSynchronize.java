package com.example.Moody.Background;

import androidx.annotation.NonNull;

import com.example.Moody.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DataSynchronize {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    ArrayList<String> fid = new ArrayList<String>();
    ArrayList<String> lid = new ArrayList<String>();

    String uid = currentUser.getUid();
    int like = 0;
    DBHelper dbHelper;

    public void LoadData(){

        //내정보 동기화
        database.getReference("userInfo").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel um = snapshot.getValue(UserModel.class);
                dbHelper.insertMyInfo(um);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        database.getReference("friend").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fid.clear();
                lid.clear();

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    fid.add(dataSnapshot1.getKey());//친구 id

                    if(dataSnapshot1.getValue().equals(true))
                        lid.add(dataSnapshot1.getKey());//좋아요 친구 id
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
                            like = 1;
                        }
                    }
                    for(int i=0;i<fid.size();i++){
                        if(users.getUID().equals(fid.get(i))) {
                            dbHelper.insertFriend(users,like);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });




    }
}
