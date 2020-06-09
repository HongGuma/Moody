package com.example.phometalk.Sign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phometalk.Activity.LoginActivity;
import com.example.phometalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignActivity extends AppCompatActivity {
    private static final String TAG = "SignActivity";
    private FirebaseAuth mAuth; //이메일,비밀번호 로그인 모듈 변수
    private FirebaseUser currentUser; //현재 로그인된 유저 정보 담는 변수
    private FirebaseDatabase database;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        mAuth = FirebaseAuth.getInstance();

        final EditText signEmail = (EditText)findViewById(R.id.sign_input_email);
        final Button nextBtn = (Button)findViewById(R.id.sign_nextBtn);
        final Button backBtn = (Button)findViewById(R.id.sign_backBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email  = signEmail.getText().toString();

                //email값 전달
                Intent intent = new Intent(getApplicationContext(),SignPwActivity.class);
                intent.putExtra("semail",email);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignActivity.this,LoginActivity.class));
            }
        });

    }


}
