package com.example.Moody.Feed;
import com.example.Moody.Activity.IntroActivity;
import com.example.Moody.Activity.LoginActivity;
import com.example.Moody.Activity.MainActivity;
import com.example.Moody.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
        ImageView back_btn = (ImageView) findViewById(R.id.upload_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadPhotoActivity.this, MainActivity.class);
                intent.putExtra("fragment","feed");
                startActivity(intent);
            }
        });

        //사진 선택
        LinearLayout sel_btn = (LinearLayout)findViewById(R.id.upload_sel_btn);
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
                    LoginActivity.dbHelper.insert(image, emotion);
                    System.out.println("URI:"+selectedImageUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),"이미지가 등록되었습니다", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UploadPhotoActivity.this, MainActivity.class);
                intent.putExtra("fragment","feed");
                startActivity(intent);
                LoginActivity.dbHelper.close();
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
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap resized = null;

        while (height > 64) {
            resized = Bitmap.createScaledBitmap(bitmap, (width * 118) / height, 64, true);
            height = resized.getHeight();
            width = resized.getWidth();

        }


        /*byte[][][] pixel = new byte[64][64][3];
        int count =0;
        for(int i=0; i<64; i++)
            for(int j=0; j<64; j++)
                for(int k=0; k<3; k++) {
                    pixel[i][j][k] = bytes[count];
                    count++;
                }*/

        /*ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
        bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

        byte[] array = buffer.array();*/
        float[][][][] bytes_img = new float[1][64][64][3];

        int k = 0;
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                int pixel = resized.getPixel(x, y);      // ARGB : ff4e2a2a

                bytes_img[0][y][x][0] = (Color.red(pixel)) / (float) 255;
                bytes_img[0][y][x][1] = (Color.green(pixel)) / (float) 255;
                bytes_img[0][y][x][2] = (Color.blue(pixel)) / (float) 255;
            }
        }
        for(int a=0; a<64; a++) {
            for (int i = 60; i <64; i++)
                for (int j = 0; j < 3; j++)
                    System.out.println(bytes_img[0][a][i][j]);
            System.out.println("A");
        }
        Interpreter tf_lite = getTfliteInterpreter("image2_model.tflite");

        float[][] output = new float[1][6];
        tf_lite.run(bytes_img, output);

        Log.d("predict", Arrays.toString(output[0]));

        int maxIdx = 0;
        float maxProb = output[0][0];
        for (int i = 1; i < 6; i++) {
            if (output[0][i] > maxProb) {
                maxProb = output[0][i];
                maxIdx = i;
            }
            System.out.println(output[0][i]);
        }
        System.out.println(maxIdx);
        String emotion = null;
        if (maxIdx == 0)
            emotion = "angry";
        else if (maxIdx == 1)
            emotion = "happy";
        else if (maxIdx == 2)
            emotion = "sad";
        else if (maxIdx == 3)
            emotion = "disgust";
        else if (maxIdx == 4)
            emotion = "fear";
        else if (maxIdx == 5)
            emotion = "surprise";

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

