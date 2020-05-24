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

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private ArrayList<UserItems> uData = null;

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView uName;
        TextView uState;
        ImageView uPhoto;

        ViewHolder(View itemView){
            super(itemView);

            uName=itemView.findViewById(R.id.user_name);
            uState=itemView.findViewById(R.id.user_state);
            uPhoto=itemView.findViewById(R.id.user_photo);
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
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_friend,parent,false);
        FriendAdapter.ViewHolder vh = new FriendAdapter.ViewHolder(view);
        return vh;
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserItems item = uData.get(position);

        holder.uPhoto.setId(item.getUserPhoto());
        holder.uName.setText(item.getUserName());
        holder.uState.setText(item.getUserState());
    }

    @Override
    public int getItemCount() {
        return uData.size();
    }
}
