package com.example.Moody.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Moody.R;

public class add_block_message  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_block_message);

        ImageButton back_btn = (ImageButton) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), block_message.class);
                startActivity(intent);
            }
        });

        Button ok_btn = (Button) findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"차단메시지가 추가되었습니다", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), block_message.class);
                startActivity(intent);
            }
        });
    }
}

