package com.example.phometalk.Feed;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.example.phometalk.Activity.IntroActivity;
import com.example.phometalk.Chat.ChatActivity;
import com.example.phometalk.Model.ChatModel;
import com.example.phometalk.R;
import com.example.phometalk.Model.FeedItems;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<FeedItems> feedDataArrayList;
    Context context;
    private String imageUrl;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();//현재 로그인 정보
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public TagAdapter(Context context,ArrayList<FeedItems> feedDataArrayList){
        this.context=context;
        this.feedDataArrayList = feedDataArrayList;

    }

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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_image, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        String tagtext=myViewHolder.tag.getText().toString();
        tagtext=tagtext.substring(1);

        for(int i=0;i<IntroActivity.publicItems.size();i++) {
            FeedItems entity = new FeedItems();
            if(tagtext.equals(IntroActivity.publicItems.get(i).getType())) {
                entity.setUrl(IntroActivity.publicItems.get(i).getUrl());
                entity.setTag(IntroActivity.publicItems.get(i).getType());
                feedDataArrayList.add(entity);
            }
        }
        if(feedDataArrayList.get(position).getUrl()==null) {
            myViewHolder.image.setImageBitmap(feedDataArrayList.get(position).getImage());
        }
        else{
            Glide.with(context).load(feedDataArrayList.get(position).getUrl()).into(myViewHolder.image);
        }
        myViewHolder.tag.setText("#" + feedDataArrayList.get(position).getTag());
        if (feedDataArrayList.get(position).getStar() == 1) {
            myViewHolder.star.setBackgroundResource(R.drawable.feed_full_heart);
        }
        else if(!IntroActivity.dbHelper.searchItem(feedDataArrayList.get(position).getUrl())){
            myViewHolder.star.setBackgroundResource(R.drawable.feed_full_heart);
        }
        else {
            myViewHolder.star.setBackgroundResource(R.drawable.feed_heart);
        }

        myViewHolder.star.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(myViewHolder.star.isChecked()){
                    myViewHolder.star.setBackgroundResource(R.drawable.feed_full_heart);
                    if(feedDataArrayList.get(position).getUrl()==null) {
                        IntroActivity.dbHelper.setStar(1, feedDataArrayList.get(position).getId());
                    }
                    else{
                        IntroActivity.dbHelper.pblInsert(feedDataArrayList.get(position).getUrl(), feedDataArrayList.get(position).getTag());
                    }
                }
                else{
                    myViewHolder.star.setBackgroundResource(R.drawable.feed_heart);
                    if(feedDataArrayList.get(position).getUrl()==null) {
                        IntroActivity.dbHelper.setStar(0,feedDataArrayList.get(position).getId());
                    }
                    else
                        IntroActivity.dbHelper.pblDelete(feedDataArrayList.get(position).getUrl());
                }
            }
        });

        myViewHolder.tag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String tagtext=myViewHolder.tag.getText().toString();
                tagtext=tagtext.substring(1);

                ArrayList<FeedItems> tagItems= IntroActivity.dbHelper.getTagItems(tagtext);

                for(int i=0;i<IntroActivity.publicItems.size();i++) {
                    FeedItems entity = new FeedItems();

                    if(tagtext.equals(IntroActivity.publicItems.get(i).getType())) {
                        entity.setUrl(IntroActivity.publicItems.get(i).getUrl());
                        entity.setTag(IntroActivity.publicItems.get(i).getType());
                        tagItems.add(entity);
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
