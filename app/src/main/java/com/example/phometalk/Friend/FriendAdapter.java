package com.example.phometalk.Friend;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.Model.ChatRoomModel;
import com.example.phometalk.Model.UserModel;
import com.example.phometalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private static final String TAG = "FriendAdapter";

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private ArrayList<UserModel> uData;
    private ChatRoomModel room = new ChatRoomModel();
    private String uid;
    private String rec;
    private String roomid;

    //생성자에서 데이터 리스트 객체를 전달받음
    public FriendAdapter(ArrayList<UserModel> list){
        uData=list;
    }

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView photo;
        public TextView uName;
        public Button chatBtn;
        public Button blockBtn;
        //ImageView uPhoto;

        ViewHolder(final View view){
            super(view);

            photo=view.findViewById(R.id.friend_image);
            uName=view.findViewById(R.id.friend_name);
            chatBtn = view.findViewById(R.id.friend_chatBtn);

        }

    }

    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        holder.uName.setText(uData.get(position).getName());

        holder.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),UserPageActivity.class);
                intent.putExtra("receiver",uData.get(position).getUID());

                v.getContext().startActivity(intent);
            }
        });


    }

    //아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }



    @Override
    public int getItemCount() {
        //Log.d(TAG, "uData size: "+uData.length);
        return uData.size();
    }

}


