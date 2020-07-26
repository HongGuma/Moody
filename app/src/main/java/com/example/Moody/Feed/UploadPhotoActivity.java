package com.example.Moody.Feed;
import com.example.Moody.Activity.IntroActivity;
import com.example.Moody.Activity.MainActivity;
import com.example.Moody.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class UploadPhotoActivity  extends AppCompatActivity {
    private final int GET_GALLERY_IMAGE = 200;
    String emotion="null";
    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //아이디별 db파일 호출

        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_photo_activity);

        //뒤로가기 버튼
        Button back_btn = (Button) findViewById(R.id.upload_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadPhotoActivity.this, MainActivity.class);
                intent.putExtra("fragment","feed");
                startActivity(intent);
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
        //final EditText tag_field=(EditText)findViewById(R.id.tag_field);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String tag = tag_field.getText().toString();
                try {
                    byte[]image=getByteArray();
                    IntroActivity.dbHelper.insert(image, emotion);
                    System.out.println("URI:"+selectedImageUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),"이미지가 등록되었습니다", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UploadPhotoActivity.this, MainActivity.class);
                intent.putExtra("fragment","feed");
                startActivity(intent);
                IntroActivity.dbHelper.close();
            }
        });
    }
    //이미지 바이트 단위로 변환
    public byte[]getByteArray() throws IOException {
        Bitmap byteBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImageUri);
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        byteBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[]data= stream.toByteArray();
        return data;
    }

    public String getEmotion() throws IOException {
        Bitmap floatBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

        float[][][][] bytes_img = new float[1][64][64][3];

        int k = 0;
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                int pixel = floatBitmap.getPixel(x, y);      // ARGB : ff4e2a2a

                bytes_img[0][y][x][0] = ((pixel >> 16) & 0xff) / (float) 255;
                bytes_img[0][y][x][1] = ((pixel >> 8) & 0xff) / (float) 255;
                bytes_img[0][y][x][2] = ((pixel >> 0) & 0xff) / (float) 255;
            }
        }

        Interpreter tf_lite = getTfliteInterpreter("image_model.tflite");

        float[][] output = new float[1][3];
        tf_lite.run(bytes_img, output);

        Log.d("predict", Arrays.toString(output[0]));

        int maxIdx = 0;
        float maxProb = output[0][0];
        for (int i = 1; i < 3; i++) {
            if (output[0][i] > maxProb) {
                maxProb = output[0][i];
                maxIdx = i;
            }
        }
        System.out.println(maxIdx);
        String emotion = null;
        if (maxIdx == 0)
            emotion = "angry";
        else if (maxIdx == 1)
            emotion = "happy";
        else if (maxIdx == 2)
            emotion = "sad";

        System.out.println(emotion);
        TextView tag_field = (TextView) findViewById(R.id.tag_field);
        tag_field.setText("#"+emotion);
        return emotion;
    }

    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(UploadPhotoActivity.this, modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //모델을 읽어오는 함수
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //갤러리 이미지 출력
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageview = (ImageView) findViewById(R.id.upload_image);
        imageview.setBackground(null);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            try {
                emotion = getEmotion();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}

