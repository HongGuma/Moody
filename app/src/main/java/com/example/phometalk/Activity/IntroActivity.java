package com.example.phometalk.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phometalk.R;

public class IntroActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable run = new Runnable() {
        @Override
        public void run() {
            //다음화면으로 넘어가기 handler
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish(); // activity화면 제거
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro); //intro.xml과 연결

    }

    @Override
    protected void onResume() {
        super.onResume(); //handler에 예약 걸기
        handler.postDelayed(run,2000); //2초뒤에 Runnable() 객체 실행
    }

    @Override
    protected void onPause() {
        super.onPause(); //화면을 벗어나면, handler에 예약한 작업 취소
        handler.removeCallbacks(run); //예약취소
    }
}
