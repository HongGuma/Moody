package com.example.Moody.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Moody.Firebase.Image;
import com.example.Moody.Feed.DBHelper;
import com.example.Moody.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class IntroActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static HashMap<Integer, String> word_set;
    public static ArrayList<Image> publicItems = new ArrayList<Image>();
    public static DBHelper dbHelper=null;
    Handler handler = new Handler();
    Runnable run = new Runnable() {
        @Override
        public void run() {
            //다음화면으로 넘어가기 handler
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            //finish(); // activity화면 제거
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro); //intro.xml과 연결
        //아이디별 db파일 호출
        mAuth = FirebaseAuth.getInstance();
        String file=mAuth.getUid()+".db";
        dbHelper = new DBHelper(IntroActivity.this, file, null, 1);
        word_set = Word();
        getImageList();
    }
    private void getImageList() {
        final ProgressDialog mProgressDialog = new ProgressDialog(IntroActivity.this);
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Image");
        //mProgressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProgressDialog.dismiss();
                publicItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Image image = snapshot.getValue(Image.class);
                    publicItems.add(image);

                }
                //FragmentFeed.adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume(); //handler에 예약 걸기
        handler.postDelayed(run,1000); //1초뒤에 Runnable() 객체 실행
    }

    @Override
    protected void onPause() {
        super.onPause(); //화면을 벗어나면, handler에 예약한 작업 취소
        handler.removeCallbacks(run); //예약취소
    }

    //뒤로가기 버튼 누를시
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private HashMap<Integer, String> Word() {

        HashMap<Integer, String> word_set = new HashMap<Integer, String>();

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.wordset);
            InputStreamReader reader = new InputStreamReader(inputStream);
            // 입력 버퍼 생성
            BufferedReader bufReader = new BufferedReader(reader);
            String line = "";

            int i=0;
            while ((line = bufReader.readLine()) != null) {
                System.out.println(line);

                String[] word = line.split(":");
                word_set.put(Integer.parseInt(word[1]), word[0]);
                i++;
            }
            bufReader.close();
        } catch (IOException e) {
            System.out.println(e);
        }

        return word_set;
    }

    public static <K, V> K getKey(HashMap<K, V> map, V value) {

        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }
}
