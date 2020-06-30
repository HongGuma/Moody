package com.example.phometalk.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.phometalk.Chat.FragmentChatting;
import com.example.phometalk.Feed.FragmentFeed;
import com.example.phometalk.Friend.FragmentFriend;
import com.example.phometalk.Setting.FragmentSetting;
import com.example.phometalk.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText search_btn;
    private MenuItem sitem;


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
            return true;
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
        search_btn=(EditText) findViewById(R.id.friend_search);

        //네비게이션 버튼 선언
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation_bar);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //처음에 보여지는 프래그먼트 설정
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        String fragment = getIntent().getStringExtra("fragment");
        if(fragment == null){
            fragmentTransaction.add(R.id.content,FragmentFriend.newInstance()).commit();
        }else{
            switch (fragment){
                case "friend":
                    sitem = navigationView.getMenu().getItem(0);
                    sitem.setChecked(true);
                    fragmentTransaction.add(R.id.content, FragmentFriend.newInstance()).commit();
                    break;
                case "chat":
                    sitem = navigationView.getMenu().getItem(1);
                    sitem.setChecked(true);
                    fragmentTransaction.add(R.id.content,FragmentChatting.newInstance()).commit();
                    break;
                case "feed":
                    sitem = navigationView.getMenu().getItem(2);
                    sitem.setChecked(true);
                    fragmentTransaction.add(R.id.content,FragmentFeed.newInstance()).commit();
                    break;
                case "setting":

                default:
                    sitem = navigationView.getMenu().getItem(3);
                    sitem.setChecked(true);
                    fragmentTransaction.add(R.id.content,FragmentSetting.newInstance()).commit();
                    break;
            }
        }

    }

    //로그인 상태면 currentUser에 현재 유저 정보가 담김, 아니면 로그인 화면으로 이동
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
    //키보드 내리기
    public boolean onTouchEvent(MotionEvent event) {
        EditText email = (EditText)findViewById(R.id.friend_search);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
        return true;
    }
}

