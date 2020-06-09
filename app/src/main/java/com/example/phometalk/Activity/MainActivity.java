package com.example.phometalk.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.phometalk.Chat.FragmentChatting;
import com.example.phometalk.Feed.FragmentFeed;
import com.example.phometalk.Friend.FragmentFriend;
import com.example.phometalk.Setting.FragmentSetting;
import com.example.phometalk.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_friend:
                    replaceFragment(FragmentFriend.newInstance());
                    return true;
                case R.id.menu_chatting:
                    replaceFragment(FragmentChatting.newInstance());
                    return true;
                case R.id.menu_feed:
                    replaceFragment(FragmentFeed.newInstance());
                    return true;
                case R.id.menu_setting:
                    replaceFragment(FragmentSetting.newInstance());
                    return true;

            }
            return false;
        }
    };
    //프래그먼트 바꾸는 함수
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content,fragment).commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation_bar);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content,FragmentFeed.newInstance()).commit();
    }

    //로그인 상태면 currentUser에 현재 유저 정보가 담김, 아니면 로그인 화면으로 이동
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }
    }


}


