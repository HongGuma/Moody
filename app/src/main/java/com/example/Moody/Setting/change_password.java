package com.example.Moody.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Moody.Activity.MainActivity;
import com.example.Moody.R;

public class change_password  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        ImageButton back_btn = (ImageButton) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button ok_btn = (Button) findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"비밀번호가 변경되었어요", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fragment", "setting");
                startActivity(intent);
                finish();
            }
        });
    }
}

