package com.example.phometalk.Sign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.phometalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignPwActivity extends Activity {
    private static final String TAG = "SignActivity";
    private FirebaseAuth mAuth; //이메일,비밀번호 로그인 모듈 변수
    private FirebaseUser currentUser; //현재 로그인된 유저 정보 담는 변수
    private FirebaseDatabase database;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_pw);
        mAuth = FirebaseAuth.getInstance();

        final EditText pw = (EditText)findViewById(R.id.sign_input_pw);
        final EditText pwCheck = (EditText)findViewById(R.id.sign_pw_check);
        final Button nextBtn = (Button)findViewById(R.id.sign_pw_nextBtn);
        final Button backBtn = (Button)findViewById(R.id.sign_pw_backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignPwActivity.this,SignActivity.class));
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String signPW = pw.getText().toString();
                String signPwCheck = pwCheck.getText().toString();

                if(!signPW.equals(signPwCheck))
                    Toast.makeText(SignPwActivity.this,"비밀번호가 맞지 않습니다.",Toast.LENGTH_LONG).show();
                else{
                    if(signPW.length() < 6)
                        Toast.makeText(SignPwActivity.this,"비밀번호는 6자리 이상이어야 합니다.",Toast.LENGTH_LONG).show();
                    else{
                        Intent intentData = getIntent();
                        String email = intentData.getExtras().getString("semail");

                        Intent intent = new Intent(getApplicationContext(),SignAddInfoActivity.class);

                        intent.putExtra("email",email);
                        intent.putExtra("pw",signPW);
                        Log.d(TAG, "onClick: "+email);
                        signUpStart(email,signPW);
                        startActivity(intent);
                    }
                }

            }
        });


    }

    public void signUpStart(String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"회원 생성 성공");
                            currentUser = mAuth.getCurrentUser();
                        }else{
                            Log.d(TAG,"회원 생성 실패");
                            /*try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                Toast.makeText(SignAddInfoActivity.this,"비밀번호를 6자 이상 입력해주세요.",Toast.LENGTH_SHORT).show();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                Toast.makeText(SignAddInfoActivity.this,"이메일형식에 맞지 않습니다.",Toast.LENGTH_SHORT).show();
                            }catch (FirebaseAuthUserCollisionException e){
                                Toast.makeText(SignAddInfoActivity.this,"가입된 이메일 입니다.",Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(SignAddInfoActivity.this,"다시 확인해주세요.",Toast.LENGTH_SHORT).show();
                            }*/
                        }
                    }
                });
    }
}
