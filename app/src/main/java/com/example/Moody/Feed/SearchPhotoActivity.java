package com.example.Moody.Feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Moody.Activity.MainActivity;
import com.example.Moody.R;

public class SearchPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_photo);

        ImageButton back_btn = (ImageButton) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPhotoActivity.this, MainActivity.class);
                intent.putExtra("fragment","feed");
                startActivity(intent);
            }
        });
    }
}

