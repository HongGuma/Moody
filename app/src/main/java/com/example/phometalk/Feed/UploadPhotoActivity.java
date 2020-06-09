package com.example.phometalk.Feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phometalk.R;

public class UploadPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_photo_activity);

        ImageButton back_btn = (ImageButton) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FragmentFeed.class);
                startActivity(intent);
            }
        });

        Button upload_btn = (Button) findViewById(R.id.upload_btn);
        upload_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"이미지가 등록되었습니다", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), FragmentFeed.class);
                startActivity(intent);
            }
        });
    }
}

