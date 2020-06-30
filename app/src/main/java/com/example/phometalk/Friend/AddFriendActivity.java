package com.example.phometalk.Friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.phometalk.Activity.MainActivity;
import com.example.phometalk.Model.UserModel;
import com.example.phometalk.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class AddFriendActivity extends Activity {
    private static final String TAG = "AddFriendActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private String fid=null;
    private Boolean check = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        final EditText friendEmail = (EditText)findViewById(R.id.friend_add_input);
        final Button addBtn = (Button)findViewById(R.id.friend_add_btn);
        Button searchBtn = (Button)findViewById(R.id.friend_search_btn);
        Button backBtn = (Button)findViewById(R.id.friend_add_backBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inEmail = friendEmail.getText().toString(); //이메일 입력받기
                UsersInfo(inEmail);
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFriendActivity.this, MainActivity.class);
                intent.putExtra("fragment","friend");
                startActivity(intent);
                finish();
            }
        });
    }

    public void UsersInfo(final String email) {
        //입력받은 이메일로 유저id 가져오기
        database.getReference("userInfo").orderByChild("email/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user = null;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    user = dataSnapshot1.getValue(UserModel.class);
                    if(user.getEmail().equals(email)) {
                        fid = user.getUID();
                        break;
                    }else
                        fid = null;
                }
                if(fid == null) {
                    Toast toast = Toast.makeText(AddFriendActivity.this, "없는 유저입니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, Gravity.CENTER, 0);
                    toast.show();
                }else{
                    FriendCheck();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    //친구목록에서 유저 중복 확인
    public void FriendCheck(){
        database.getReference("friend").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(dataSnapshot1.getKey().equals(fid)){
                        check = true;
                        break;
                    }
                }
                if(check == true){
                    Toast toast = Toast.makeText(AddFriendActivity.this,"이미 친구추가 되어 있어요",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, Gravity.CENTER, 0);
                    toast.show();
                    Intent intent = new Intent(AddFriendActivity.this, MainActivity.class);
                    intent.putExtra("fragment","friend");
                    startActivity(intent);
                    finish();
                }else{
                    HashMap<String,Object> friend = new HashMap<String, Object>();
                    friend.put(fid,true);
                    database.getReference("friend").child(currentUser.getUid()).updateChildren(friend).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast toast = Toast.makeText(AddFriendActivity.this,"친구추가",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL, Gravity.CENTER, 0);
                            toast.show();
                            Intent intent = new Intent(AddFriendActivity.this, MainActivity.class);
                            intent.putExtra("fragment","friend");
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}