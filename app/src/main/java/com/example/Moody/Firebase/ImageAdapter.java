package com.example.Moody.Firebase;

import android.content.Context;
import android.content.Intent;
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
import com.example.Moody.Activity.LoginActivity;
import com.example.Moody.Feed.DetailPopupActivity;
import com.example.Moody.Feed.FeedAdapter;
import com.example.Moody.Feed.FragmentFeed;
import com.example.Moody.Feed.PageAdapter;
import com.example.Moody.Model.FeedItems;
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
        final int size;
        try {
            if (mode == 1)
                size = imageArrayList.size() - position - 1;
            else
                size = position;
            final Image item = imageArrayList.get(size);

            if (position < 8) {
                holder.tag.setText("#" + item.getType());
                Glide.with(context).load(item.getUrl()).into(holder.photo);

                if (LoginActivity.dbHelper.searchItem(item.getUrl())) {
                    holder.star.setBackgroundResource(R.drawable.feed_heart);
                } else {
                    holder.star.setBackgroundResource(R.drawable.feed_full_heart);
                }
            } else {
                holder.photo.setVisibility(View.GONE);
                holder.star.setVisibility(View.GONE);
                holder.tag.setVisibility(View.GONE);
                holder.itemView.setVisibility(View.GONE);
            }
            holder.star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.star.isChecked()) {
                        holder.star.setBackgroundResource(R.drawable.feed_full_heart);
                        LoginActivity.dbHelper.pblInsert(item.getUrl(), item.getType(), item.getResult());
                    } else {
                        holder.star.setBackgroundResource(R.drawable.feed_heart);
                        LoginActivity.dbHelper.pblDelete(item.getUrl());
                    }
                }
            });

            holder.tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tagtext = holder.tag.getText().toString();
                    tagtext = tagtext.substring(1);
                    ArrayList<FeedItems> pageItems = new ArrayList<>();
                    if (mode == 1) {
                        for (int i = imageArrayList.size() - 1; i >= 0; i--) {
                            FeedItems entity = new FeedItems();
                            if (tagtext.equals(imageArrayList.get(i).getType())) {
                                entity.setUrl(imageArrayList.get(i).getUrl());
                                entity.setTag(imageArrayList.get(i).getType());
                                entity.setResult(imageArrayList.get(i).getResult());
                                pageItems.add(entity);
                            }
                        }
                    } else {
                        for (int i = 0; i < imageArrayList.size(); i++) {
                            FeedItems entity = new FeedItems();
                            if (tagtext.equals(imageArrayList.get(i).getType())) {
                                entity.setUrl(imageArrayList.get(i).getUrl());
                                entity.setTag(imageArrayList.get(i).getType());
                                entity.setResult(imageArrayList.get(i).getResult());
                                pageItems.add(entity);
                            }
                        }
                    }
                    FeedAdapter fAdapter = new FeedAdapter(context, pageItems);
                    PageAdapter pAdapter = new PageAdapter(context, pageItems);
                    FragmentFeed.feedRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                    FragmentFeed.feedRecyclerView.setAdapter(fAdapter);
                    FragmentFeed.pageRecyclerView.setAdapter(pAdapter);
                }
            });
            //이미지 클릭 시
            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int size;
                    if (mode == 1)
                        size = imageArrayList.size() - position - 1;
                    else
                        size = position;
                    Intent intent = new Intent(context, DetailPopupActivity.class);
                    intent.putExtra("res", imageArrayList.get(size).getResult());
                    intent.putExtra("url", imageArrayList.get(size).getUrl());
                    context.startActivity(intent);


                }
            });
        } catch (IndexOutOfBoundsException e){
            System.out.println(e);
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    class  ViewHolder extends RecyclerView.ViewHolder{
        ImageView photo;
        Button tag;
        ToggleButton star;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.tag_btn);
            photo = itemView.findViewById(R.id.upload_image);
            star=itemView.findViewById(R.id.star_btn);
        }
    }
}