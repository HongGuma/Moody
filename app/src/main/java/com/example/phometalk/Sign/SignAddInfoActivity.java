package com.example.phometalk.Sign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.phometalk.Activity.LoginActivity;
import com.example.phometalk.Chat.ChatActivity;
import com.example.phometalk.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignAddInfoActivity extends Activity {
    private static final String TAG = "SignActivity";
    private FirebaseAuth mAuth; //이메일,비밀번호 로그인 모듈 변수
    private FirebaseUser currentUser; //현재 로그인된 유저 정보 담는 변수
    private FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_info);
        mAuth = FirebaseAuth.getInstance(); //이메일 비밀번호 로그인 모듈 변수
        currentUser = mAuth.getCurrentUser();

        final EditText userName = (EditText)findViewById(R.id.sign_input_name);
        final EditText userBirth = (EditText)findViewById(R.id.sign_input_birth);
        Button backBtn = (Button)findViewById(R.id.sign_info_backBtn);
        Button okBtn = (Button)findViewById(R.id.sign_info_btn);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignAddInfoActivity.this,SignPwActivity.class));
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentData = getIntent();

                final String name = userName.getText().toString();
                final String birth = userBirth.getText().toString();
                final String email = intentData.getExtras().getString("email");
                final String password = intentData.getExtras().getString("pw");



                database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("userInfo").child(currentUser.getUid());

                HashMap<String, String> users = new HashMap<String, String>();
                users.put("uID",currentUser.getUid());
                users.put("email",email);
                users.put("password",password);
                users.put("name",name);
                users.put("birth",birth);

                myRef.setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    //database에 값 전달이 성공되면
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignAddInfoActivity.this,"가입 완료. 로그인 해주세요",Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(SignAddInfoActivity.this, LoginActivity.class);
                        //intent.putExtra("userName",name);
                        startActivity(new Intent(SignAddInfoActivity.this, LoginActivity.class));

                        finish();
                    }
                });



            }
        });
    }



}
