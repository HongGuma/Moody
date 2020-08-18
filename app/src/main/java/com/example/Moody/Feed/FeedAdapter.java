package com.example.Moody.Feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Moody.Activity.IntroActivity;
import com.example.Moody.Activity.LoginActivity;
import com.example.Moody.Model.FeedItems;
import com.example.Moody.R;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        Button tag;
        ToggleButton star;
        MyViewHolder(View view){
            super(view);
            image = view.findViewById(R.id.upload_image);
            tag = view.findViewById(R.id.tag_btn);
            star=view.findViewById(R.id.star_btn);
        }
    }
    Context context;
    private ArrayList<FeedItems> feedDataArrayList;
    public FeedAdapter(Context context, ArrayList<FeedItems> feedDataArrayList){
        this.context=context;
        this.feedDataArrayList = feedDataArrayList;
    }
    public FeedAdapter(ArrayList<FeedItems> feedDataArrayList){
        this.feedDataArrayList = feedDataArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        //이미지 출력
        if(position<3) {
            if (feedDataArrayList.get(position).getUrl() == null) {
                myViewHolder.image.setImageBitmap(feedDataArrayList.get(position).getImage());
            } else {
                Glide.with(context).load(feedDataArrayList.get(position).getUrl()).into(myViewHolder.image);
            }

            myViewHolder.tag.setText("#" + feedDataArrayList.get(position).getTag());
            if (feedDataArrayList.get(position).getStar() == 1) {
                myViewHolder.star.setBackgroundResource(R.drawable.feed_full_heart);
            } else if (!LoginActivity.dbHelper.searchItem(feedDataArrayList.get(position).getUrl())) {
                myViewHolder.star.setBackgroundResource(R.drawable.feed_full_heart);
            } else {
                myViewHolder.star.setBackgroundResource(R.drawable.feed_heart);
            }
        }
        else{
            myViewHolder.itemView.setVisibility(View.GONE);
        }
        //즐겨찾기 추가 및 해제
        myViewHolder.star.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(myViewHolder.star.isChecked()){
                    myViewHolder.star.setBackgroundResource(R.drawable.feed_full_heart);
                    if(feedDataArrayList.get(position).getUrl()==null) {
                        LoginActivity.dbHelper.setStar(1, feedDataArrayList.get(position).getId());
                    }
                    else{
                        LoginActivity.dbHelper.pblInsert(feedDataArrayList.get(position).getUrl(), feedDataArrayList.get(position).getTag());
                    }
                }
                else{
                    myViewHolder.star.setBackgroundResource(R.drawable.feed_heart);
                    if(feedDataArrayList.get(position).getUrl()==null) {
                        LoginActivity.dbHelper.setStar(0,feedDataArrayList.get(position).getId());
                    }
                    else
                        LoginActivity.dbHelper.pblDelete(feedDataArrayList.get(position).getUrl());
                }
            }
        });

        //태그버튼 클릭 시
        myViewHolder.tag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String tagtext=myViewHolder.tag.getText().toString();
                tagtext=tagtext.substring(1);
                int chk=0;
                ArrayList<FeedItems> tagItems= LoginActivity.dbHelper.getTagItems(tagtext);
                for(int i=0;i<feedDataArrayList.size();i++){
                    if(feedDataArrayList.get(i).getUrl()!=null)
                        chk=1;
                }
                if(chk==1) {
                    for (int i = 0; i < LoginActivity.publicItems.size(); i++) {
                        FeedItems entity = new FeedItems();

                        if (tagtext.equals(LoginActivity.publicItems.get(i).getType())) {
                            entity.setUrl(LoginActivity.publicItems.get(i).getUrl());
                            entity.setTag(LoginActivity.publicItems.get(i).getType());
                            tagItems.add(entity);
                        }
                    }
                }
                FeedAdapter myAdapter = new FeedAdapter(context,tagItems);
                FragmentFeed.feedRecyclerView.setLayoutManager(new GridLayoutManager(context,2));
                FragmentFeed.feedRecyclerView.setAdapter(myAdapter);
            }
        });

    }

    @Override
    public int getItemCount() {
        return feedDataArrayList.size();
    }
}

