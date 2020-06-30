package com.example.phometalk.Setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.phometalk.Activity.LoginActivity;
import com.example.phometalk.Activity.MainActivity;
import com.example.phometalk.Firebase.Image;
import com.example.phometalk.Model.UserModel;
import com.example.phometalk.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class FragmentSetting extends Fragment {
    private static final String TAG = "FragmentSetting";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private String name;
    private String email;

    private String imageUrl;
    int GET_GALLERY_IMAGE = 101;

    public static FragmentSetting newInstance(){
        return new FragmentSetting();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);

        //view 선언
        TextView myName = (TextView)view.findViewById(R.id.user_id);
        TextView myEmail = (TextView)view.findViewById(R.id.user_email);
        ImageView myImage = (ImageView) view.findViewById(R.id.profile_image);
        //DB 유저 정보 불러오기
        UserInfo(myName,myEmail,myImage);

        //버튼 선언
        Button profileBtn = (Button)view.findViewById(R.id.profile_btn);
        final Button block_message_btn = (Button)view.findViewById(R.id.block_message_btn);
        Button logoutBtn = (Button)view.findViewById(R.id.logout_btn);
        Button blocked_userlist_btn = (Button)view.findViewById(R.id.blocked_userlist_btn);
        Button change_password_btn = (Button)view.findViewById(R.id.change_password_btn);
        Button app_info_btn = (Button)view.findViewById(R.id.app_info_btn);


        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ProfilePageActivity.class));
            }
        });

        block_message_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),block_message.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        blocked_userlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), blocked_user.class));
            }
        });

        change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), change_password.class));
            }
        });

        app_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), app_info.class));
            }
        });

        //ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        //actionBar.setTitle("My Information");
        //actionBar.setDisplayHomeAsUpEnabled(false);
        return view;
    }



    public void UserInfo(final TextView myName, final TextView myEmail, final ImageView myImage){
        database.getReference("userInfo").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                myName.setText(user.getName());
                myEmail.setText(user.getEmail());
                if(!user.getProfile().equals(""))
                    Glide.with(getContext()).load(user.getProfile()).apply(new RequestOptions().circleCrop()).into(myImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}