package com.example.Moody.Activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Moody.FindPW.FindActivity;
import com.example.Moody.R;
import com.example.Moody.Sign.SignActivity;
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
        setContentView(R.layout.login); //login 레이아웃 연결

        mAuth = FirebaseAuth.getInstance();

        final EditText loginEmail = (EditText)findViewById(R.id.login_email);
        final EditText loginPW = (EditText)findViewById(R.id.login_pw);
        Button loginBtn = (Button)findViewById(R.id.login_loginBtn);
        Button signBtn = (Button)findViewById(R.id.login_signBtn);
        Button findBtn = (Button)findViewById(R.id.login_findBtn);

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
                //Toast.makeText(LoginActivity.this,"회원가입 버튼 클릭",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, SignActivity.class));

            }
        });

        //찾기 버튼 눌렀을때
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this,"비밀번호 찾기 버튼 클릭",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, FindActivity.class));
            }
        });


    }

    public void loginStart(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Toast.makeText(LoginActivity.this,"mAuth.onComplete 함수",Toast.LENGTH_SHORT).show();
                if(!task.isSuccessful()){ //예외처리
                    try{
                        throw task.getException();
                    }catch (FirebaseApiNotAvailableException e){
                        Toast toast = Toast.makeText(LoginActivity.this,"등록되지 않은 이메일 입니다.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL,Gravity.CENTER,0);
                        toast.show();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        Toast toast = Toast.makeText(LoginActivity.this,"이메일/비밀번호가 틀렸습니다.",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL,Gravity.CENTER,0);
                        toast.show();
                    }catch (FirebaseNetworkException e){
                        Toast toast = Toast.makeText(LoginActivity.this,"Firebase NetworkException",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL,Gravity.CENTER,0);
                        toast.show();
                    }catch (Exception e){
                        Toast toast = Toast.makeText(LoginActivity.this,"다시확인해주세요.",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL,Gravity.CENTER,0);
                        toast.show();
                    }
                }else{
                    currentUser = mAuth.getCurrentUser(); //성공시
                    //Toast.makeText(LoginActivity.this,"로그인 완료",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("email",email);
                    startActivity(intent);

                    finish();
                }
            }
        });
    }


    //자동 로그인

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }




    //키보드 내리기
    public boolean onTouchEvent(MotionEvent event) {
        EditText email = (EditText)findViewById(R.id.login_email);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
        return true;
    }


}