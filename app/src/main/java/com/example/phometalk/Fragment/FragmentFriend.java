package com.example.phometalk.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.FriendAdapter;
import com.example.phometalk.Items.UserItems;
import com.example.phometalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class FragmentFriend extends Fragment {

    public static FragmentFriend newInstance() {
        return new FragmentFriend();
    }

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;

    private ImageView userPhoto;
    private TextView userName;
    private TextView userState;

    RecyclerView fRecyclerView = null;
    FriendAdapter fAdapter = null;
    ArrayList<UserItems> uList = new ArrayList<UserItems>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(); */

        addItem(,"김철수","테스트중");
        addItem(,"이영희","테스트중");


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_friend,container,false);

        fRecyclerView = (RecyclerView)view.findViewById(R.id.friend_recyclerView);
        fAdapter = new FriendAdapter(uList);
        fRecyclerView.setAdapter(fAdapter);

        return view;
    }

    public void addItem(String icon, String name, String state){
        UserItems uitem = new UserItems();

        uitem.setUserPhoto(icon);
        uitem.setUserName(name);
        uitem.setUserState(state);
    }
}
