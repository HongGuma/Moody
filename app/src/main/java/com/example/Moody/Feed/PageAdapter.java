package com.example.Moody.Feed;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Moody.Model.FeedItems;
import com.example.Moody.R;

import java.util.ArrayList;

public class PageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<FeedItems> feedDataArrayList;
    Context context;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Button page;
        MyViewHolder(View view){
            super(view);
            page=view.findViewById(R.id.page_btn);
        }
    }
    public PageAdapter(Context context, ArrayList<FeedItems> feedDataArrayList){
        this.context=context;
        this.feedDataArrayList = feedDataArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page_number, parent, false);
        return new MyViewHolder(v);
    }

    boolean click=true;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        if((position+1)%3!=1){
            myViewHolder.page.setVisibility(View.GONE);
        }
        else {
            int num=(position+1)/3+1;
            myViewHolder.page.setText(Integer.toString(num));
        }
        //페이지 번호 클릭
        myViewHolder.page.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int pagenum=Integer.parseInt(myViewHolder.page.getText().toString());
                ArrayList<FeedItems> pageItems=new ArrayList<FeedItems>();
                //myViewHolder.page.setTextColor(Color.parseColor("#EFEDF0"));
                for(int i=(pagenum-1)*3;i<pagenum*3;i++) {
                    FeedItems entity = new FeedItems();
                    if(i<feedDataArrayList.size()) {
                        if (feedDataArrayList.get(i).getUrl() == null) {
                            entity.setId(feedDataArrayList.get(i).getId());
                            entity.setImage(feedDataArrayList.get(i).getImage());
                            entity.setTag(feedDataArrayList.get(i).getTag());
                            entity.setStar(feedDataArrayList.get(i).getStar());
                            pageItems.add(entity);
                        } else {
                            entity.setUrl(feedDataArrayList.get(i).getUrl());
                            entity.setTag(feedDataArrayList.get(i).getTag());
                            pageItems.add(entity);
                        }
                    }
                }
//                if (click) {
//                    myViewHolder.page.setTextColor(Color.parseColor("#EFEDF0"));
//                    click = false;
//                } else {
//                    myViewHolder.page.setTextColor(Color.parseColor("#000000"));
//                    click = true;
//                }
//                notifyDataSetChanged();

                FeedAdapter myAdapter = new FeedAdapter(context,pageItems);
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
