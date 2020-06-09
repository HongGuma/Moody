package com.example.phometalk.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phometalk.R;
import com.example.phometalk.Setting.add_block_message;
import com.example.phometalk.Setting.block_message;
import com.example.phometalk.Setting.del_block_message;

public class edit_block_message  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_block_message);

        Button add_btn = (Button) findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), add_block_message.class);
                startActivity(intent);
            }
        });

        Button del_btn = (Button) findViewById(R.id.del_btn);
        del_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), del_block_message.class);
                startActivity(intent);
            }
        });

        Button cancel_btn = (Button) findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), block_message.class);
                startActivity(intent);
            }
        });
    }
}

