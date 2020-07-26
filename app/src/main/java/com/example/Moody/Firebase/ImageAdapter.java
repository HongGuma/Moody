package com.example.Moody.Firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Moody.Activity.IntroActivity;
import com.example.Moody.Feed.FragmentFeed;
import com.example.Moody.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Context context;
    ArrayList<Image> imageArrayList;
    int mode=1;
    public ImageAdapter(Context context, ArrayList<Image> videos, int mode){
        this.context =context;
        this.imageArrayList =videos;
        this.mode=mode;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int size;
        if(mode==1)
            size=imageArrayList.size()-position-1;
        else
            size=position;
        final Image item = imageArrayList.get(size);
        holder.tag.setText("#"+item.getType());
        Glide.with(context).load(item.getUrl()).into(holder.photo);

        if (IntroActivity.dbHelper.searchItem(item.getUrl())) {
            holder.star.setBackgroundResource(R.drawable.feed_heart);
        } else {
            holder.star.setBackgroundResource(R.drawable.feed_full_heart);
        }
        holder.star.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(holder.star.isChecked()){
                    holder.star.setBackgroundResource(R.drawable.feed_full_heart);
                    IntroActivity.dbHelper.pblInsert(item.getUrl(),item.getType());
                }
                else{
                    holder.star.setBackgroundResource(R.drawable.feed_heart);
                    IntroActivity.dbHelper.pblDelete(item.getUrl());
                }
            }
        });

        holder.tag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String tagtext=holder.tag.getText().toString();
                tagtext=tagtext.substring(1);
                ArrayList<Image> tagItems=new ArrayList<>();
                for(int i=0;i<imageArrayList.size();i++) {
                    Image entity = new Image();

                    if(tagtext.equals(imageArrayList.get(i).getType())) {
                        entity.setUrl(imageArrayList.get(i).getUrl());
                        entity.setType(imageArrayList.get(i).getType());
                        tagItems.add(entity);
                    }
                }

                ImageAdapter myAdapter = new ImageAdapter(context,tagItems,1);
                FragmentFeed.feedRecyclerView.setLayoutManager(new GridLayoutManager(context,2));
                FragmentFeed.feedRecyclerView.setAdapter(myAdapter);
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
}