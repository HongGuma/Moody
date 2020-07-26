package com.example.Moody.FindPW;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.Moody.Activity.LoginActivity;
import com.example.Moody.R;

public class FindActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        final EditText inputEmail = (EditText)findViewById(R.id.findpw_inputEmail);
        Button backBtn = (Button)findViewById(R.id.findpw_backbtn);
        Button nextBtn = (Button)findViewById(R.id.findpw_nextbtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindActivity.this, LoginActivity.class));
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inEmail = inputEmail.getText().toString();
                startActivity(new Intent(FindActivity.this,FindNewPWActivity.class));
            }
        });
    }
}
