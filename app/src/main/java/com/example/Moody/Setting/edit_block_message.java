package com.example.Moody.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Moody.R;

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

