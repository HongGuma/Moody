package com.example.phometalk.Firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phometalk.R;
import com.example.phometalk.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Context context;
    ArrayList<Image> imageArrayList;
    public ImageAdapter(Context context, ArrayList<Image> videos){
        this.context =context;
        this.imageArrayList =videos;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Image item = imageArrayList.get(position);
        holder.tag.setText("#"+item.getType());
        Glide.with(context).load(item.getUrl()).into(holder.photo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        holder.star.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(holder.star.isChecked()){
                    holder.star.setBackgroundResource(R.drawable.full_star);
                }
                else{
                    holder.star.setBackgroundResource(R.drawable.empty_star);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageArrayList.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder{
        ImageView photo;
        Button tag;
        ToggleButton star;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.tag_btn);
            photo = itemView.findViewById(R.id.tag_photo);
            star=itemView.findViewById(R.id.star_btn);
        }
    }
    interface OnItemClickListener{
        void onItemClick(int position);
    }
    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
