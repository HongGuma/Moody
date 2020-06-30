package com.example.phometalk.Feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.Activity.IntroActivity;
import com.example.phometalk.Model.FeedItems;
import com.example.phometalk.R;

import java.util.ArrayList;

public class TagImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    Context context;
    private String tag[]=new String[3];

    public TagImageAdapter(Context context, String []tag) {
        this.context=context;
        this.tag=tag;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Button seltag;
        RecyclerView tag_recyclerview;

        MyViewHolder(View view){
            super(view);
            seltag = view.findViewById(R.id.seltag_btn);
            tag_recyclerview = view.findViewById(R.id.tag_recyclerview);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_feed_row, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.seltag.setText("#" + tag[position]);
        myViewHolder.tag_recyclerview.setHasFixedSize(true);
        myViewHolder.tag_recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeedItems> tagItems= IntroActivity.dbHelper.getTagItems(tag[position]);
        for(int i = 0; i< IntroActivity.publicItems.size(); i++) {
            FeedItems entity = new FeedItems();

            if(tag[position].equals(IntroActivity.publicItems.get(i).getType())) {
                entity.setUrl(IntroActivity.publicItems.get(i).getUrl());
                entity.setTag(IntroActivity.publicItems.get(i).getType());
                tagItems.add(entity);
            }
        }
        final TagAdapter myAdapter = new TagAdapter(context,tagItems);
        myViewHolder.tag_recyclerview.setAdapter(myAdapter);

        myViewHolder.seltag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String tagtext=myViewHolder.seltag.getText().toString();
                tagtext=tagtext.substring(1);

                ArrayList<FeedItems> tagItems= IntroActivity.dbHelper.getTagItems(tagtext);
                for(int i = 0; i< IntroActivity.publicItems.size(); i++) {
                    FeedItems entity = new FeedItems();

                    if(tagtext.equals(IntroActivity.publicItems.get(i).getType())) {
                        entity.setUrl(IntroActivity.publicItems.get(i).getUrl());
                        entity.setTag(IntroActivity.publicItems.get(i).getType());
                        tagItems.add(entity);
                    }
                }
                FragmentFeed.feedRecyclerView.setLayoutManager(new GridLayoutManager(context,2));
                FeedAdapter myAdapter = new FeedAdapter(context,tagItems);

                FragmentFeed.feedRecyclerView.setAdapter(myAdapter);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tag.length;
    }
}
