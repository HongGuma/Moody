package com.example.phometalk.Feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.example.phometalk.Activity.MainActivity;
import com.example.phometalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class FragmentFeed extends Fragment {
    private static final String TAG = "FragmentFeed";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;


    public static FragmentFeed newInstance(){
        return new FragmentFeed();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_feed,container,false);

        Button uploadBtn = (Button)view.findViewById(R.id.feed_upload_btn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),UploadPhotoActivity.class));
            }
        });

        Button searchBtn = (Button)view.findViewById(R.id.feed_search_btn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SearchPhotoActivity.class));
            }
        });

        Button publicBtn = (Button)view.findViewById(R.id.feed_public_btn);
        Button privateBtn = (Button)view.findViewById(R.id.feed_private_btn);
        Button tagBtn = (Button)view.findViewById(R.id.feed_tag_btn);
        Button markBtn = (Button)view.findViewById(R.id.feed_mark_btn);


        //ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        //actionBar.setTitle("피드");
        //actionBar.setDisplayHomeAsUpEnabled(false);


        return view;

    }
}
