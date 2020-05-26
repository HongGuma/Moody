package com.example.phometalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.Items.UserItems;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private ArrayList<UserItems> uData;


    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView uName;
        public TextView uState;
        public TextView uEmail;
        //ImageView uPhoto;

        ViewHolder(View view){
            super(view);

            uName=view.findViewById(R.id.friend_name);
            uState=view.findViewById(R.id.friend_state);
            //uEmail=view.findViewById(R.id.friend_email);
            //uPhoto=itemView.findViewById(R.id.friend_image);
        }

    }
    //생성자에서 데이터 리스트 객체를 전달받음
    public FriendAdapter(ArrayList<UserItems> list){
        uData=list;
    }

    //아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserItems item = uData.get(position);

        //holder.uPhoto.setId(item.getUserPhoto());
        holder.uName.setText(item.getName());
        holder.uState.setText(item.getState());

    }

    @Override
    public int getItemCount() {
        return uData.size();
    }

}


