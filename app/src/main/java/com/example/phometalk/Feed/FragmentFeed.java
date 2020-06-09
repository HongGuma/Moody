package com.example.phometalk.Feed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.Feed.FeedAdapter;
import com.example.phometalk.Feed.TagAdapter;
import com.example.phometalk.Chat.FragmentChatting;
import com.example.phometalk.Friend.FriendAdapter;
import com.example.phometalk.Model.FeedItems;
import com.example.phometalk.R;
import com.example.phometalk.Firebase.Image;
import com.example.phometalk.Firebase.ImageAdapter;
import com.example.phometalk.Firebase.ImageListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentFeed extends Fragment {
    private static final String TAG = "FragmentFeed";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;

    public static DBHelper dbHelper = null;
    public static RecyclerView feedRecyclerView;
    private ProgressDialog mProgressDialog;
    private ImageAdapter adapter;
    private ArrayList<Image> publicItems = new ArrayList<Image>();
    private DatabaseReference databaseReference;

    public static FragmentFeed newInstance() {
        return new FragmentFeed();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mAuth = FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        //아이디별 db파일 호출
        mAuth = FirebaseAuth.getInstance();
        String file=mAuth.getUid()+".db";
        dbHelper = new DBHelper(activity.getApplicationContext(), file, null, 1);
        super.onCreate(savedInstanceState);

        feedRecyclerView = (RecyclerView)view.findViewById(R.id.feed_recyclerview);
        feedRecyclerView.setHasFixedSize(true);
        feedRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        ArrayList<FeedItems> feedItems = dbHelper.getItems(2);
        FeedAdapter myAdapter = new FeedAdapter(feedItems);
        feedRecyclerView.setAdapter(myAdapter);

        final Button public_btn = (Button)view.findViewById(R.id.feed_public_btn);
        final Button private_btn = (Button) view.findViewById(R.id.feed_private_btn);
        final Button mark_btn = (Button) view.findViewById(R.id.feed_mark_btn);
        final Button tag_btn = (Button) view.findViewById(R.id.feed_tag_btn);

        //출력 순서
        final int[] mode = {0};
        Spinner spinner=(Spinner)view.findViewById(R.id.feed_sortmode_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ColorDrawable mark_color= (ColorDrawable)mark_btn.getBackground();
                ColorDrawable private_color= (ColorDrawable)private_btn.getBackground();

                if(parent.getItemAtPosition(position).equals("최신순")&&(int)mark_color.getColor()== Color.WHITE){
                    mode[0] =1;
                    final ArrayList<FeedItems> descItems = dbHelper.getStarItems(1);
                    final FeedAdapter myAdapter = new FeedAdapter(descItems);
                    feedRecyclerView.setAdapter(myAdapter);

                }
                else if(parent.getItemAtPosition(position).equals("등록순")&&(int)mark_color.getColor()==Color.WHITE){
                    mode[0] =2;
                    final ArrayList<FeedItems> feedItems = dbHelper.getStarItems(2);
                    final FeedAdapter myAdapter = new FeedAdapter(feedItems);
                    feedRecyclerView.setAdapter(myAdapter);
                }
                else if(parent.getItemAtPosition(position).equals("최신순")&&(int)private_color.getColor()==Color.WHITE){
                    mode[0] =1;
                    final ArrayList<FeedItems> descItems = dbHelper.getItems(1);
                    final FeedAdapter myAdapter = new FeedAdapter(descItems);
                    feedRecyclerView.setAdapter(myAdapter);

                }
                else if(parent.getItemAtPosition(position).equals("등록순")&&(int)private_color.getColor()==Color.WHITE){
                    mode[0] =2;
                    final ArrayList<FeedItems> feedItems = dbHelper.getItems(2);
                    final FeedAdapter myAdapter = new FeedAdapter(feedItems);
                    feedRecyclerView.setAdapter(myAdapter);
                }
                else{
                    mode[0]=3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //이미지 등록
        Button uploadBtn = (Button)view.findViewById(R.id.feed_upload_btn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),UploadPhotoActivity.class));
            }
        });

        //이미지 검색
        Button searchBtn = (Button)view.findViewById(R.id.feed_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SearchPhotoActivity.class));
            }
        });

        Button firebase_btn = (Button)view.findViewById(R.id.firebase_btn);
        firebase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ImageListActivity.class));
            }
        });

        mProgressDialog= new ProgressDialog(getActivity());
        adapter = new ImageAdapter(getActivity(), publicItems);
        //공용 이미지
        public_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                public_btn.setBackgroundColor(Color.WHITE);
                private_btn.setBackgroundColor(Color.rgb(213,213,213));
                mark_btn.setBackgroundColor(Color.rgb(213,213,213));
                tag_btn.setBackgroundColor(Color.rgb(213,213,213));
//                if(mode[0]==1) {
//                    privateItems = dbHelper.getItems(1);
//                }
//                else{
//                    privateItems = dbHelper.getItems(2);
//                }

                feedRecyclerView.setAdapter(adapter);
                getImageList();

            }
        });

        //개별 이미지
        private_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ArrayList<FeedItems> privateItems=new ArrayList<>();
                private_btn.setBackgroundColor(Color.WHITE);
                mark_btn.setBackgroundColor(Color.rgb(213,213,213));
                tag_btn.setBackgroundColor(Color.rgb(213,213,213));
                if(mode[0]==1) {
                    privateItems = dbHelper.getItems(1);
                }
                else{
                    privateItems = dbHelper.getItems(2);
                }
                FeedAdapter myAdapter = new FeedAdapter(privateItems);

                feedRecyclerView.setAdapter(myAdapter);
            }
        });

        //즐겨찾기
        mark_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mark_btn.setBackgroundColor(Color.WHITE);
                private_btn.setBackgroundColor(Color.rgb(213,213,213));
                tag_btn.setBackgroundColor(Color.rgb(213,213,213));
                ArrayList<FeedItems> starItems = new ArrayList<>();
                if(mode[0]==1) {
                    starItems = dbHelper.getStarItems(1);
                }
                else{
                    starItems = dbHelper.getStarItems(2);
                }
                FeedAdapter myAdapter = new FeedAdapter(starItems);

                feedRecyclerView.setAdapter(myAdapter);
            }
        });

        //태그 이미지
        tag_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                tag_btn.setBackgroundColor(Color.WHITE);
                private_btn.setBackgroundColor(Color.rgb(213,213,213));
                mark_btn.setBackgroundColor(Color.rgb(213,213,213));
                ArrayList<FeedItems> tagItems = dbHelper.getItems(2);
                TagAdapter myAdapter = new TagAdapter(tagItems);

                feedRecyclerView.setAdapter(myAdapter);
            }
        });



        return view;

    }
    private void getImageList() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Image");
        mProgressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProgressDialog.dismiss();
                publicItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Image image = snapshot.getValue(Image.class);
                    publicItems.add(image);

                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

    }

}

