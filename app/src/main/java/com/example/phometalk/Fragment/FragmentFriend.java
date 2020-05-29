package com.example.phometalk.Fragment;

import android.app.Activity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.Activity.AddFriendActivity;
import com.example.phometalk.FriendAdapter;
import com.example.phometalk.Items.UserItems;
import com.example.phometalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;


public class FragmentFriend extends Fragment {
    private static final String TAG = "FragmentFriend";

    public static FragmentFriend newInstance() {
        return new FragmentFriend();
    }

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;

   // private ImageView userPhoto;
    private TextView userName;
    private TextView userState;

    private RecyclerView fRecyclerView;
    private FriendAdapter fAdapter;
    private ArrayList<UserItems> uList = new ArrayList<UserItems>();

    //제일 먼저 호출
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(); //db초기화
        Log.d(TAG,"onCreate0");

        FriendListDisplay();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_friend,container,false);

        Button addBtn = (Button)view.findViewById(R.id.friend_add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AddFriendActivity.class));
            }
        });
        
        fRecyclerView = (RecyclerView)view.findViewById(R.id.friend_recyclerView);

        fRecyclerView.setHasFixedSize(true);
        fRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        //String[] uList = {"test","1","2","3"};
        fAdapter = new FriendAdapter(uList);
        fRecyclerView.setAdapter(fAdapter);

        return view;
    }



    public void FriendListDisplay(){

        DatabaseReference ref = database.getReference("userInfo");

        database.getReference("userInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,"onDataChange:"+dataSnapshot.getValue().toString());
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Log.d(TAG,"dataSnapshot1:"+dataSnapshot1.getValue().toString());

                    UserItems userItems = dataSnapshot1.getValue(UserItems.class);
                    Log.d(TAG,"name:"+userItems.getName()+", email:"+userItems.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
