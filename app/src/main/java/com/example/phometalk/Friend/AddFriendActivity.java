package com.example.phometalk.Friend;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.phometalk.R;

public class AddFriendActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        EditText friendEmail = (EditText)findViewById(R.id.friend_add_input);
        Button addBtn = (Button)findViewById(R.id.friend_add_btn);
        Button backBtn = (Button)findViewById(R.id.friend_add_backBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(AddFriendActivity.this, FragmentFriend.class));
            }
        });
    }
}
