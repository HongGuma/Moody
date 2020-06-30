package com.example.phometalk.Chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.phometalk.Model.ChatModel;
import com.example.phometalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private static final String TAG = "GroupAdapter";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private ArrayList<String> recID;

    SimpleDateFormat writeTimeFormat = new SimpleDateFormat("a hh:mm");

    //생성자에서 데이터 리스트 객체를 전달받음
    public GroupAdapter(ArrayList<String> recIDs){
        this.recID=recIDs;

    }

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView userImage;
        public TextView userName;
        public TextView textView;
        public TextView timestamp;
        public ImageView sendPhoto;

        ViewHolder(View view){
            super(view);
            userImage = (ImageView)view.findViewById(R.id.user_image);
            userName = (TextView)view.findViewById(R.id.user_name);
            textView = (TextView)view.findViewById(R.id.tvChat);
            timestamp = (TextView)view.findViewById(R.id.timestamp);
            sendPhoto = (ImageView)view.findViewById(R.id.ivChat);

        }

    }

    //상대방이 보낸 메세지인지 구분
    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        return 1;

    }

    //아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatbubble_right,parent,false);

        GroupAdapter.ViewHolder vh = new GroupAdapter.ViewHolder(view);
/*
        cRecyclerView.scrollToPosition(cAdapter.getItemCount()-1);
        String a = Integer.toString(cAdapter.getItemCount());
        Log.d(TAG, "onClick"+a);
*/

        return vh;

    }


    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {


    }

    //개수만큼 아이템 생성
    @Override
    public int getItemCount() {
        return recID.size();
    }

}
