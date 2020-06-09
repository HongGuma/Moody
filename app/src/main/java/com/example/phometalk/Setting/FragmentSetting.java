package com.example.phometalk.Setting;

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

public class FragmentSetting extends Fragment {
    private static final String TAG = "FragmentSetting";
    public static FragmentSetting newInstance(){
        return new FragmentSetting();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);
        Log.d(TAG, "onCreateView");
        final Button block_message_btn = (Button)view.findViewById(R.id.block_message_btn);
        block_message_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),block_message.class));
            }
        });

        Button blocked_userlist_btn = (Button)view.findViewById(R.id.blocked_userlist_btn);
        blocked_userlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), blocked_user.class));
            }
        });

        Button change_password_btn = (Button)view.findViewById(R.id.change_password_btn);
        change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), change_password.class));
            }
        });

        Button app_info_btn = (Button)view.findViewById(R.id.app_info_btn);
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
}
