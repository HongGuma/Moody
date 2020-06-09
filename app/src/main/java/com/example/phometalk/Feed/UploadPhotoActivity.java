package com.example.phometalk.Feed;
import com.example.phometalk.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadPhotoActivity  extends AppCompatActivity {
    private final int GET_GALLERY_IMAGE = 200;
    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //아이디별 db파일 호출
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String file=mAuth.getUid()+".db";
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), file, null, 1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_photo_activity);

        //뒤로가기 버튼
        ImageButton back_btn = (ImageButton) findViewById(R.id.upload_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(UploadPhotoActivity.this, FragmentFeed.class));
            }
        });

        //사진 선택
        Button sel_btn = (Button)findViewById(R.id.upload_sel_btn);
        sel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        //이미지 등록
        Button upload_btn = (Button) findViewById(R.id.upload_okBtn);
        final EditText tag_field=(EditText)findViewById(R.id.tag_field);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = tag_field.getText().toString();
                try {
                    byte[]image=getByteArray();
                    dbHelper.insert(image, tag);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),"이미지가 등록되었습니다", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), FragmentFeed.class);
                startActivity(intent);
                dbHelper.close();
            }
        });
    }
    //이미지 바이트 단위로 변환
    public byte[]getByteArray() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImageUri);
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[]data= stream.toByteArray();
        return data;
    }
    //갤러리 이미지 출력
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageview = (ImageView) findViewById(R.id.upload_image);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
        }
    }

}

