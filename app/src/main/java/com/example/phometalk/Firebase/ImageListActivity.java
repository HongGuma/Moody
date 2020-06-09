package com.example.phometalk.Firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.Feed.FragmentFeed;
import com.example.phometalk.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImageListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private ProgressDialog mProgressDialog;
    private ImageAdapter adapter;
    private ArrayList<Image> list = new ArrayList<Image>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagelist);

        ImageButton back_btn = (ImageButton) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FragmentFeed.class);
                startActivity(intent);
            }
        });

        mProgressDialog= new ProgressDialog(this);
        recyclerView = findViewById(R.id.mng_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        adapter = new ImageAdapter(this,list);
        recyclerView.setAdapter(adapter);
        getImageList();
        findViewById(R.id.mng_upload_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageListActivity.this,UpLoadImageToFirebase.class));
            }
        });
    }

    private void getImageList() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Image");
        mProgressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProgressDialog.dismiss();
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Image image = snapshot.getValue(Image.class);
                    list.add(image);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });
    }

}
