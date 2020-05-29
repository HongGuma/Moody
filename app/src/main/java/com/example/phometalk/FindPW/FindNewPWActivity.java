package com.example.phometalk.FindPW;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.phometalk.Activity.LoginActivity;
import com.example.phometalk.R;

public class FindNewPWActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpw_newpw);

        final EditText newPW = (EditText)findViewById(R.id.findpw_newPw);
        final EditText newPwCheck = (EditText)findViewById(R.id.findpw_pwCheck);
        Button backBtn = (Button)findViewById(R.id.findpw_new_backBtn);
        Button okBtn = (Button)findViewById(R.id.findpw_new_nextBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindNewPWActivity.this,FindActivity.class));
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw = newPW.getText().toString();
                String pwChk = newPwCheck.getText().toString();

                startActivity(new Intent(FindNewPWActivity.this, LoginActivity.class));
            }
        });
    }
}
