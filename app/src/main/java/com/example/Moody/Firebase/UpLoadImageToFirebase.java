package com.example.Moody.Firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Moody.Activity.MainActivity;
import com.example.Moody.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class UpLoadImageToFirebase extends AppCompatActivity {
    private ProgressDialog dialog;
    private StorageReference mStoreReference;
    String emotion="null";
    Uri selectedImageUri;
    private String imageUrl;
    private final int GET_GALLERY_IMAGE = 200;
    private ArrayList<String> urls=new ArrayList<>();
    private ArrayList<String> tags=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_photo_activity);
        dialog = new ProgressDialog(this);
        TextView upload_lbl = (TextView)findViewById(R.id.upload_lbl);
        upload_lbl.setText("Upload image for adminstrator");

        //뒤로가기 버튼
        Button back_btn = (Button) findViewById(R.id.upload_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpLoadImageToFirebase.this, MainActivity.class);
                intent.putExtra("fragment","feed");
                startActivity(intent);
            }
        });

        findViewById(R.id.upload_sel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });
        findViewById(R.id.upload_okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpLoadImageToFirebase.this, MainActivity.class);
                intent.putExtra("fragment","feed");
                startActivity(intent);
                if (TextUtils.isEmpty(imageUrl)){
                    return;
                }
                sendImage();
            }
        });

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


        return emotion;
    }

    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(UpLoadImageToFirebase.this, modelPath));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageview = (ImageView) findViewById(R.id.upload_image);
        imageview.setBackground(null);
        TextView tag_field = (TextView) findViewById(R.id.tag_field);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GET_GALLERY_IMAGE:
                    //selectedImageUri = data.getData();
                    ClipData clipData=data.getClipData();
                    urls.clear();
                    tags.clear();

                    ArrayList<String> uris=new ArrayList<>();
                    Log.i("clipdata", String.valueOf(clipData.getItemCount()));

                    //이미지 하나만 선택했을 경우
                    if (clipData.getItemCount() == 1) {
                        String dataStr = String.valueOf(clipData.getItemAt(0).getUri());
                        Log.i("1. clipdata choice", String.valueOf(clipData.getItemAt(0).getUri()));
                        Log.i("1. single choice", clipData.getItemAt(0).getUri().getPath());
                        selectedImageUri=data.getData();
                        imageview.setImageURI(selectedImageUri);
                        try {
                            emotion = getEmotion();
                            tag_field.setText("#"+emotion);
                            tags.add(emotion);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        uris.add(dataStr);
                    }
                    //여러장 선택한 경우
                    else if (clipData.getItemCount() > 1 && clipData.getItemCount() < 100) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Log.i("2. single choice", String.valueOf(clipData.getItemAt(i).getUri()));
                            uris.add(String.valueOf(clipData.getItemAt(i).getUri()));
                            selectedImageUri=clipData.getItemAt(i).getUri();
                            imageview.setImageURI(selectedImageUri);
                            try {
                                emotion = getEmotion();
                                tag_field.append(" #"+emotion);
                                tags.add(emotion);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    upload(uris);
                    break;

                default:
                    break;
            }
        }

    }

    // 이미지 업로드
    private void upload(final ArrayList<String> uris) {
        dialog.show();
        for(int i=0;i<uris.size();i++){
            final int position=i;
            mStoreReference = FirebaseStorage.getInstance().getReference();
            final StorageReference riversRef = mStoreReference.child("image/" + System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = riversRef.putFile(Uri.parse(uris.get(i)));
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageUrl = downloadUri.toString();
                        urls.add(imageUrl);
                        //Glide.with(UpLoadImageToFirebase.this).load(imageUrl).into(upload_image);
                        if (position==uris.size()-1){
                            dialog.dismiss();
                        }
                    }
                }
            });
        }
    }
    private void sendImage() {
        for(int i=0;i<urls.size();i++) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("url", urls.get(i));
            hashMap.put("type", tags.get(i));
            databaseReference.child("Image").push().setValue(hashMap);
        }
        finish();
    }
}
