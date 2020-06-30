package com.example.phometalk.Feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.Activity.IntroActivity;
import com.example.phometalk.Firebase.UpLoadImageToFirebase;
import com.example.phometalk.Model.FeedItems;
import com.example.phometalk.R;
import com.example.phometalk.Firebase.ImageAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FragmentFeed extends Fragment {
    private static final String TAG = "FragmentFeed";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;

    public static RecyclerView feedRecyclerView;
    public static ImageAdapter adapter;
    int tab=0;
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
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        super.onCreate(savedInstanceState);

        feedRecyclerView = (RecyclerView)view.findViewById(R.id.feed_recyclerview);
        feedRecyclerView.setHasFixedSize(true);
        feedRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        ArrayList<FeedItems> feedItems = IntroActivity.dbHelper.getItems(1);
        for(int i=0;i< IntroActivity.publicItems.size();i++){
            FeedItems entity=new FeedItems();
            int length=IntroActivity.publicItems.size();
            entity.setUrl(IntroActivity.publicItems.get(length-i-1).getUrl());
            entity.setTag(IntroActivity.publicItems.get(length-i-1).getType());
            feedItems.add(entity);
        }
        FeedAdapter myAdapter = new FeedAdapter(inflater.getContext(),feedItems);
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
                //공용이미지 정렬
                if(parent.getItemAtPosition(position).equals("최신순")&&tab==1){
                    mode[0]=1;
                    adapter=new ImageAdapter(inflater.getContext(),IntroActivity.publicItems,1);
                    feedRecyclerView.setAdapter(adapter);
                }
                else if(parent.getItemAtPosition(position).equals("등록순")&&tab==1){
                    mode[0]=2;
                    adapter=new ImageAdapter(inflater.getContext(),IntroActivity.publicItems,2);
                    feedRecyclerView.setAdapter(adapter);
                }
                //개인 정렬
                else if(parent.getItemAtPosition(position).equals("최신순")&&tab==2){
                    mode[0] =1;
                    final ArrayList<FeedItems> descItems = IntroActivity.dbHelper.getItems(1);
                    final FeedAdapter myAdapter = new FeedAdapter(descItems);
                    feedRecyclerView.setAdapter(myAdapter);

                }
                else if(parent.getItemAtPosition(position).equals("등록순")&&tab==2){
                    mode[0] =2;
                    final ArrayList<FeedItems> feedItems = IntroActivity.dbHelper.getItems(2);
                    final FeedAdapter myAdapter = new FeedAdapter(feedItems);
                    feedRecyclerView.setAdapter(myAdapter);
                }
                //즐겨찾기 정렬
                if(parent.getItemAtPosition(position).equals("최신순")&&tab==4){
                    mode[0] =1;
                    final ArrayList<FeedItems> descItems = IntroActivity.dbHelper.getStarItems(1);
                    descItems.addAll(IntroActivity.dbHelper.getMarkItems(1));
                    final FeedAdapter myAdapter = new FeedAdapter(inflater.getContext(),descItems);
                    feedRecyclerView.setAdapter(myAdapter);

                }
                else if(parent.getItemAtPosition(position).equals("등록순")&&tab==4){
                    mode[0] =2;
                    final ArrayList<FeedItems> feedItems = IntroActivity.dbHelper.getStarItems(2);
                    feedItems.addAll(IntroActivity.dbHelper.getMarkItems(2));
                    final FeedAdapter myAdapter = new FeedAdapter(inflater.getContext(),feedItems);
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
        FloatingActionButton uploadBtn = (FloatingActionButton) view.findViewById(R.id.feed_upload_btn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getUid().equals("e67K1BVGsxT7b8qq40uvI8CkCGA2")) {
                    startActivity(new Intent(inflater.getContext(), UpLoadImageToFirebase.class));
                }
                else {
                    startActivity(new Intent(inflater.getContext(), UploadPhotoActivity.class));
                }
            }
        });

        //이미지 검색
        Button searchBtn = (Button)view.findViewById(R.id.feed_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(inflater.getContext(),SearchPhotoActivity.class));
            }
        });

        //공용 이미지
        public_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tab=1;
                public_btn.setTextColor(Color.parseColor("#EFEDF0"));
                mark_btn.setTextColor(Color.parseColor("#707070"));
                private_btn.setTextColor(Color.parseColor("#707070"));
                tag_btn.setTextColor(Color.parseColor("#707070"));

                if(mode[0]==2) {
                    adapter = new ImageAdapter(inflater.getContext(), IntroActivity.publicItems, 2);
                }
                else{
                    adapter = new ImageAdapter(inflater.getContext(), IntroActivity.publicItems, 1);
                }
                feedRecyclerView.setAdapter(adapter);
                feedRecyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),2));


            }
        });

        //개별 이미지
        private_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tab=2;
                private_btn.setTextColor(Color.parseColor("#EFEDF0"));
                mark_btn.setTextColor(Color.parseColor("#707070"));
                public_btn.setTextColor(Color.parseColor("#707070"));
                tag_btn.setTextColor(Color.parseColor("#707070"));
                ArrayList<FeedItems> privateItems=new ArrayList<>();

                if(mode[0]==2) {
                    privateItems = IntroActivity.dbHelper.getItems(2);
                }
                else{
                    privateItems = IntroActivity.dbHelper.getItems(1);
                }
                FeedAdapter myAdapter = new FeedAdapter(privateItems);
                feedRecyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),2));
                feedRecyclerView.setAdapter(myAdapter);
            }
        });

        //즐겨찾기
        mark_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tab=4;
                mark_btn.setTextColor(Color.parseColor("#EFEDF0"));
                public_btn.setTextColor(Color.parseColor("#707070"));
                private_btn.setTextColor(Color.parseColor("#707070"));
                tag_btn.setTextColor(Color.parseColor("#707070"));

                ArrayList<FeedItems> starItems = new ArrayList<>();
                if(mode[0]==1) {
                    starItems = IntroActivity.dbHelper.getStarItems(1);
                    starItems.addAll(IntroActivity.dbHelper.getMarkItems(1));
                }
                else{
                    starItems = IntroActivity.dbHelper.getStarItems(2);
                    starItems.addAll(IntroActivity.dbHelper.getMarkItems(2));
                }

                FeedAdapter myAdapter = new FeedAdapter(inflater.getContext(),starItems);
                feedRecyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),2));
                feedRecyclerView.setAdapter(myAdapter);
            }
        });

        //태그 이미지
        tag_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                tab=3;
                tag_btn.setTextColor(Color.parseColor("#EFEDF0"));
                mark_btn.setTextColor(Color.parseColor("#707070"));
                private_btn.setTextColor(Color.parseColor("#707070"));
                public_btn.setTextColor(Color.parseColor("#707070"));
                //ArrayList<FeedItems> tagItems = IntroActivity.dbHelper.getItems(2);
                String tag[]={"happy","sad","angry"};
                feedRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
                TagImageAdapter myAdapter = new TagImageAdapter(inflater.getContext(),tag);
                feedRecyclerView.setAdapter(myAdapter);
            }
        });



        return view;

    }
}