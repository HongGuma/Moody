package com.example.phometalk.Feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.R;
import com.example.phometalk.Feed.FragmentFeed;
import com.example.phometalk.Model.FeedItems;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        Button tag;
        ToggleButton star;

        MyViewHolder(View view){
            super(view);
            image = view.findViewById(R.id.tag_photo);
            tag = view.findViewById(R.id.tag_btn);
            star=view.findViewById(R.id.star_btn);
        }
    }

    private ArrayList<FeedItems> feedDataArrayList;
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
        myViewHolder.image.setImageBitmap(feedDataArrayList.get(position).getImage());
        myViewHolder.tag.setText("#" + feedDataArrayList.get(position).getTag());
        if (feedDataArrayList.get(position).getStar() == 0) {
            myViewHolder.star.setBackgroundResource(R.drawable.empty_star);
        } else {
            myViewHolder.star.setBackgroundResource(R.drawable.full_star);
        }

        //즐겨찾기 추가
        myViewHolder.star.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(myViewHolder.star.isChecked()){
                    myViewHolder.star.setBackgroundResource(R.drawable.full_star);
                    FragmentFeed.dbHelper.setStar(1, position+1);
                }
                else{
                    myViewHolder.star.setBackgroundResource(R.drawable.empty_star);
                    FragmentFeed.dbHelper.setStar(0, position+1);
                }
            }
        });

        //태그버튼 클릭 시
        myViewHolder.tag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String tagtext=myViewHolder.tag.getText().toString();
                tagtext=tagtext.substring(1);

                ArrayList<FeedItems> tagItems= FragmentFeed.dbHelper.getTagItems(tagtext);
                FeedAdapter myAdapter = new FeedAdapter(tagItems);

                FragmentFeed.feedRecyclerView.setAdapter(myAdapter);
            }
        });

    }

    @Override
    public int getItemCount() {
        return feedDataArrayList.size();
    }
}

