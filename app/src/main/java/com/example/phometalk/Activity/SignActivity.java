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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; //이메일,비밀번호 로그인 모듈 변수
    private FirebaseUser currentUser; //현재 로그인된 유저 정보 담는 변수
    private FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign);

        mAuth = FirebaseAuth.getInstance();

        final EditText signEmail = (EditText)findViewById(R.id.sign_email);
        final EditText signPW = (EditText)findViewById(R.id.sign_pw);
        final EditText signName = (EditText)findViewById(R.id.sign_name);
        Button signBtn = (Button)findViewById(R.id.sign_btn);

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email  = signEmail.getText().toString();
                String password = signPW.getText().toString();
                String name = signName.getText().toString();

                //Toast.makeText(SignActivity.this,email + "/ = 가입버튼 눌리고"+password,Toast.LENGTH_SHORT).show();

                signStart(email,name,password);
            }
        });
    }

    public void signStart(String email,final String name,String password){


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful()){
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        Toast.makeText(SignActivity.this,"비밀번호를 6자 이상 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(SignActivity.this,"이메일형식에 맞지 않습니다.",Toast.LENGTH_SHORT).show();
                    }catch (FirebaseAuthUserCollisionException e){
                        Toast.makeText(SignActivity.this,"가입된 이메일 입니다.",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(SignActivity.this,"다시 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    currentUser = mAuth.getCurrentUser();

                    database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("userInfo").child(currentUser.getUid());

                    HashMap<String, String> users = new HashMap<String, String>();
                    users.put("email",currentUser.getEmail());
                    users.put("name",name);
                    users.put("state","기본상태메세지");

                    myRef.setValue(users);

                    Toast.makeText(SignActivity.this,"가입 완료. 로그인 해주세요. :)",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignActivity.this, LoginActivity.class));

                }
            }
        });
    }
}
