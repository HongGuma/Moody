package com.example.phometalk.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phometalk.R;
import com.example.phometalk.Setting.block_message;

public class del_block_message  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.del_block_message);

        ImageButton back_btn = (ImageButton) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), block_message.class);
                startActivity(intent);
            }
        });

        Button del_btn = (Button) findViewById(R.id.del_btn);
        del_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"차단메시지가 삭제되었습니다", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), block_message.class);
                startActivity(intent);
            }
        });
    }
}

