package com.example.Moody.Feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Moody.Activity.IntroActivity;
import com.example.Moody.Activity.LoginActivity;
import com.example.Moody.Firebase.ImageAdapter;
import com.example.Moody.Firebase.UpLoadImageToFirebase;
import com.example.Moody.Model.FeedItems;
import com.example.Moody.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FragmentFeed extends Fragment {
    private static final String TAG = "FragmentFeed";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static RecyclerView feedRecyclerView;
    public static RecyclerView pageRecyclerView;
    public static ImageAdapter adapter;
    int tab=1;
    int mode=0;
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
        pageRecyclerView=(RecyclerView)view.findViewById(R.id.page_recyclerview);
        pageRecyclerView.setHasFixedSize(true);
        pageRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeedItems> feedItems = new ArrayList<FeedItems>();
        for(int i = 0; i< LoginActivity.publicItems.size(); i++){
            FeedItems entity=new FeedItems();
            int length=LoginActivity.publicItems.size();
            entity.setUrl(LoginActivity.publicItems.get(length-i-1).getUrl());
            entity.setTag(LoginActivity.publicItems.get(length-i-1).getType());
            feedItems.add(entity);
        }
        FeedAdapter myAdapter = new FeedAdapter(inflater.getContext(),feedItems);
        feedRecyclerView.setAdapter(myAdapter);
        PageAdapter pAdapter=new PageAdapter(inflater.getContext(),feedItems);
        pageRecyclerView.setAdapter(pAdapter);


        final TextView public_btn = (TextView)view.findViewById(R.id.feed_public_btn);
        final TextView private_btn = (TextView) view.findViewById(R.id.feed_private_btn);
        final TextView mark_btn = (TextView) view.findViewById(R.id.feed_mark_btn);
        final TextView tag_btn = (TextView) view.findViewById(R.id.feed_tag_btn);
        final LinearLayout layout1 = (LinearLayout) view.findViewById(R.id.layout1);
        final LinearLayout layout2 = (LinearLayout) view.findViewById(R.id.layout2);
        final LinearLayout layout3 = (LinearLayout) view.findViewById(R.id.layout3);
        final LinearLayout layout4 = (LinearLayout) view.findViewById(R.id.layout4);

        //출력 순서
        final Spinner spinner=(Spinner)view.findViewById(R.id.feed_sortmode_spinner);
        ArrayAdapter SpinAdapter = ArrayAdapter.createFromResource(inflater.getContext(), R.array.selmode, R.layout.spinner_design);
        //adapter.setDropDownViewResource(R.layout.customer_spinner);
        spinner.setAdapter(SpinAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //공용이미지 정렬
                ArrayList<FeedItems> pList=new ArrayList<FeedItems>();
                if(parent.getItemAtPosition(position).equals("Newest")&&tab==1){
                    mode=1;
                    adapter=new ImageAdapter(inflater.getContext(),LoginActivity.publicItems,1);
                    for(int i=LoginActivity.publicItems.size()-1;i>=0;i--) {
                        FeedItems entity = new FeedItems();
                        entity.setUrl(LoginActivity.publicItems.get(i).getUrl());
                        entity.setTag(LoginActivity.publicItems.get(i).getType());
                        pList.add(entity);
                    }
                    feedRecyclerView.setAdapter(adapter);
                    PageAdapter pAdapter=new PageAdapter(inflater.getContext(), pList);
                    pageRecyclerView.setAdapter(pAdapter);
                }
                else if(parent.getItemAtPosition(position).equals("Oldest")&&tab==1){
                    mode=2;
                    adapter=new ImageAdapter(inflater.getContext(),LoginActivity.publicItems,2);
                    for(int i=0;i<LoginActivity.publicItems.size();i++) {
                        FeedItems entity = new FeedItems();
                        entity.setUrl(LoginActivity.publicItems.get(i).getUrl());
                        entity.setTag(LoginActivity.publicItems.get(i).getType());
                        pList.add(entity);
                    }
                    feedRecyclerView.setAdapter(adapter);
                    PageAdapter pAdapter=new PageAdapter(inflater.getContext(), pList);
                    pageRecyclerView.setAdapter(pAdapter);
                }
                //개인 정렬
                else if(parent.getItemAtPosition(position).equals("Newest")&&tab==2){
                    mode =1;
                    ArrayList<FeedItems> descItems = LoginActivity.dbHelper.getItems(1);
                    FeedAdapter myAdapter = new FeedAdapter(descItems);
                    PageAdapter pAdapter = new PageAdapter(inflater.getContext(),descItems);
                    feedRecyclerView.setAdapter(myAdapter);
                    pageRecyclerView.setAdapter(pAdapter);

                }
                else if(parent.getItemAtPosition(position).equals("Oldest")&&tab==2){
                    mode =2;
                    ArrayList<FeedItems> feedItems = LoginActivity.dbHelper.getItems(2);
                    FeedAdapter myAdapter = new FeedAdapter(feedItems);
                    feedRecyclerView.setAdapter(myAdapter);
                    PageAdapter pAdapter = new PageAdapter(inflater.getContext(), feedItems);
                    pageRecyclerView.setAdapter(pAdapter);
                }
                //즐겨찾기 정렬
                else if(parent.getItemAtPosition(position).equals("Newest")&&tab==4){
                    mode =1;
                    ArrayList<FeedItems> descItems = LoginActivity.dbHelper.getStarItems(1);
                    descItems.addAll(LoginActivity.dbHelper.getMarkItems(1));
                    FeedAdapter myAdapter = new FeedAdapter(inflater.getContext(),descItems);
                    feedRecyclerView.setAdapter(myAdapter);
                    PageAdapter pAdapter = new PageAdapter(inflater.getContext(), descItems);
                    pageRecyclerView.setAdapter(pAdapter);

                }
                else if(parent.getItemAtPosition(position).equals("Oldest")&&tab==4){
                    mode =2;
                    ArrayList<FeedItems> feedItems = LoginActivity.dbHelper.getStarItems(2);
                    feedItems.addAll(LoginActivity.dbHelper.getMarkItems(2));
                    FeedAdapter myAdapter = new FeedAdapter(inflater.getContext(),feedItems);
                    feedRecyclerView.setAdapter(myAdapter);
                    PageAdapter pAdapter = new PageAdapter(inflater.getContext(), feedItems);
                    pageRecyclerView.setAdapter(pAdapter);
                }
                else{
                    mode=3;
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
                /*public_btn.setTextColor(Color.parseColor("#EFEDF0"));
                mark_btn.setTextColor(Color.parseColor("#707070"));
                private_btn.setTextColor(Color.parseColor("#707070"));
                tag_btn.setTextColor(Color.parseColor("#707070"));*/
                layout1.setBackgroundResource(R.drawable.yj_feed_click_btn);
                layout2.setBackgroundColor(Color.parseColor("#00ff0000"));
                layout3.setBackgroundColor(Color.parseColor("#00ff0000"));
                layout4.setBackgroundColor(Color.parseColor("#00ff0000"));

                ArrayList<FeedItems> pList=new ArrayList<FeedItems>();
                if(mode==2) {
                    adapter = new ImageAdapter(inflater.getContext(), LoginActivity.publicItems, 2);
                    for(int i=0;i<LoginActivity.publicItems.size();i++) {
                        FeedItems entity = new FeedItems();
                        entity.setUrl(LoginActivity.publicItems.get(i).getUrl());
                        entity.setTag(LoginActivity.publicItems.get(i).getType());
                        pList.add(entity);
                    }
                }
                else{
                    adapter = new ImageAdapter(inflater.getContext(), LoginActivity.publicItems, 1);
                    for(int i=LoginActivity.publicItems.size()-1;i>=0;i--) {
                        FeedItems entity = new FeedItems();
                        entity.setUrl(LoginActivity.publicItems.get(i).getUrl());
                        entity.setTag(LoginActivity.publicItems.get(i).getType());
                        pList.add(entity);
                    }
                }
                feedRecyclerView.setAdapter(adapter);
                feedRecyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),2));
                PageAdapter pAdapter=new PageAdapter(inflater.getContext(), pList);
                pageRecyclerView.setAdapter(pAdapter);
                pageRecyclerView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);

            }
        });

        //개별 이미지
        private_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tab=2;
                /*private_btn.setTextColor(Color.parseColor("#EFEDF0"));
                mark_btn.setTextColor(Color.parseColor("#707070"));
                public_btn.setTextColor(Color.parseColor("#707070"));
                tag_btn.setTextColor(Color.parseColor("#707070"));*/
                layout2.setBackgroundResource(R.drawable.yj_feed_click_btn);
                layout1.setBackgroundColor(Color.parseColor("#00ff0000"));
                layout3.setBackgroundColor(Color.parseColor("#00ff0000"));
                layout4.setBackgroundColor(Color.parseColor("#00ff0000"));

                ArrayList<FeedItems> privateItems=new ArrayList<>();

                if(mode==2) {
                    privateItems = LoginActivity.dbHelper.getItems(2);
                }
                else{
                    privateItems = LoginActivity.dbHelper.getItems(1);
                }
                FeedAdapter myAdapter = new FeedAdapter(privateItems);
                feedRecyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),2));
                feedRecyclerView.setAdapter(myAdapter);
                PageAdapter pAdapter=new PageAdapter(inflater.getContext(), privateItems);
                pageRecyclerView.setAdapter(pAdapter);
                pageRecyclerView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
            }
        });

        //즐겨찾기
        mark_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tab=4;
                /*mark_btn.setTextColor(Color.parseColor("#EFEDF0"));
                public_btn.setTextColor(Color.parseColor("#707070"));
                private_btn.setTextColor(Color.parseColor("#707070"));
                tag_btn.setTextColor(Color.parseColor("#707070"));*/
                layout4.setBackgroundResource(R.drawable.yj_feed_click_btn);
                layout2.setBackgroundColor(Color.parseColor("#00ff0000"));
                layout3.setBackgroundColor(Color.parseColor("#00ff0000"));
                layout1.setBackgroundColor(Color.parseColor("#00ff0000"));


                ArrayList<FeedItems> starItems = new ArrayList<>();
                if(mode==1) {
                    starItems = LoginActivity.dbHelper.getStarItems(1);
                    starItems.addAll(LoginActivity.dbHelper.getMarkItems(1));
                }
                else{
                    starItems = LoginActivity.dbHelper.getStarItems(2);
                    starItems.addAll(LoginActivity.dbHelper.getMarkItems(2));
                }

                FeedAdapter myAdapter = new FeedAdapter(inflater.getContext(),starItems);
                feedRecyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(),2));
                feedRecyclerView.setAdapter(myAdapter);
                PageAdapter pAdapter=new PageAdapter(inflater.getContext(), starItems);
                pageRecyclerView.setAdapter(pAdapter);
                pageRecyclerView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
            }
        });

        //태그 이미지
        tag_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                tab=3;
                /*tag_btn.setTextColor(Color.parseColor("#EFEDF0"));
                mark_btn.setTextColor(Color.parseColor("#707070"));
                private_btn.setTextColor(Color.parseColor("#707070"));
                public_btn.setTextColor(Color.parseColor("#707070"));*/
                layout3.setBackgroundResource(R.drawable.yj_feed_click_btn);
                layout2.setBackgroundColor(Color.parseColor("#00ff0000"));
                layout1.setBackgroundColor(Color.parseColor("#00ff0000"));
                layout4.setBackgroundColor(Color.parseColor("#00ff0000"));

                //ArrayList<FeedItems> tagItems = IntroActivity.dbHelper.getItems(2);
                String tag[]={"happy","sad","angry","surprise","fear","disgust"};
                feedRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
                TagImageAdapter myAdapter = new TagImageAdapter(inflater.getContext(),tag);
                feedRecyclerView.setAdapter(myAdapter);
                pageRecyclerView.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.GONE);
            }
        });



        return view;

    }
}