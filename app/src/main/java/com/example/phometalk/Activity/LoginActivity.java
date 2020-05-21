package com.example.phometalk.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phometalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; //이메일 비밀번호 로그인 모듈 변수
    private FirebaseUser currentUser; //현재 로그인 된 유저 정보를 담을 변수

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        final EditText loginEmail = (EditText)findViewById(R.id.login_email);
        final EditText loginPW = (EditText)findViewById(R.id.login_pw);
        Button loginBtn = (Button)findViewById(R.id.login_loginBtn);
        Button signBtn = (Button)findViewById(R.id.login_signBtn);

        //로그인 버튼 눌렀을때
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPW.getText().toString();
                //로그인 성공
                loginStart(email,password);
            }
        });

        //회원 가입 버튼 눌렀을때
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"회원가입 버튼 클릭",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,SignActivity.class));
                finish();
            }
        });


    }

    public void loginStart(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Toast.makeText(LoginActivity.this,"mAuth.onComplete 함수",Toast.LENGTH_SHORT).show();
                if(!task.isSuccessful()){
                    try{
                        throw task.getException();
                    }catch (FirebaseApiNotAvailableException e){
                        Toast.makeText(LoginActivity.this,"등록되지 않은 이메일 입니다.", Toast.LENGTH_SHORT).show();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(LoginActivity.this,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }catch (FirebaseNetworkException e){
                        Toast.makeText(LoginActivity.this,"Firebase NetworkException",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(LoginActivity.this,"Excetion",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    currentUser = mAuth.getCurrentUser();
                    //Toast.makeText(LoginActivity.this,"로그인"+"/"+currentUser.getEmail()+"/"+currentUser.getUid(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this,"로그인 완료",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }
            }
        });
    }
    /*
    //자동 로그인
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }*/
}
