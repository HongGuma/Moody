package com.example.phometalk.Friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.phometalk.Chat.ChatActivity;
import com.example.phometalk.Model.ChatRoomModel;
import com.example.phometalk.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserPageActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String uid;
    private ChatRoomModel room = new ChatRoomModel();
    private String roomid;
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();

        final String rec = getIntent().getStringExtra("receiver");
        roomid = getIntent().getStringExtra("roomid");

        TextView uName = (TextView)findViewById(R.id.user_page_name);
        Button chatBtn = (Button)findViewById(R.id.user_page_btn);



        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final Intent intent = new Intent(UserPageActivity.this, ChatActivity.class);

                intent.putExtra("receiver",rec);
                intent.putExtra("roomid",roomid);

                startActivity(intent);
                finish();

            }
        });

    }

}
