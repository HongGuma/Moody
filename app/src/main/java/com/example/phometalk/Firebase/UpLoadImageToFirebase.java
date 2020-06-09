package com.example.phometalk.Firebase;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.phometalk.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class UpLoadImageToFirebase extends AppCompatActivity {
    private ProgressDialog dialog;
    private StorageReference mStoreReference;
    private EditText etTagField;
    private String imageUrl;
    private ImageView upload_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_photo_activity);
        dialog = new ProgressDialog(this);
        etTagField = findViewById(R.id.tag_field);
        upload_image = findViewById(R.id.upload_image);
//        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        findViewById(R.id.upload_sel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });
        findViewById(R.id.upload_okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = etTagField.getText().toString();
                if (TextUtils.isEmpty(tag)){
                    return;
                }
                if (TextUtils.isEmpty(imageUrl)){
                    return;
                }
                sendImage();
            }
        });

    }

    private void selectImage() {
        PermissionUtils.checkPermission(this, PermissionUtils.PERMISSION_SD, new PermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                takePhoto();
            }

            @Override
            public void onPermissionDenied(String... permission) {
                showNoticeDialog();
            }

            @Override
            public void alwaysDenied(String... permission) {
                PermissionUtils.requestPermission(UpLoadImageToFirebase.this, PermissionUtils.PERMISSION_SD, 100);
            }


        });
    }
    private void showNoticeDialog() {

        new AlertDialog.Builder(UpLoadImageToFirebase.this)
                .setTitle(PermissionUtils.TITLE)
                .setMessage("Permission  allow")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PermissionUtils.requestPermission(UpLoadImageToFirebase.this, PermissionUtils.PERMISSION_SD, 100);

                    }
                }).setNegativeButton("cancel", null)
                .show();
    }
    private void takePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 100);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 100:
                    final Uri selectedUri = data.getData();
                    upload(selectedUri);
                    break;

                default:
                    break;
            }
        }

    }
    private void upload(Uri selectedUri) {
        dialog.show();
        mStoreReference=  FirebaseStorage.getInstance().getReference();
        final StorageReference riversRef = mStoreReference.child("image/"+System.currentTimeMillis()+".jpg");
        UploadTask uploadTask = riversRef.putFile(selectedUri);
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
                    Glide.with(UpLoadImageToFirebase.this).load(imageUrl).into(upload_image);
                }
            }
        });

    }
    private void sendImage() {
        String type = etTagField.getText().toString();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("url", imageUrl);
        hashMap.put("type",type);
        databaseReference.child("Image").push().setValue(hashMap);
        finish();
    }
}

