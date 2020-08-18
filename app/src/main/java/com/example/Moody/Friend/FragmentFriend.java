package com.example.Moody.Friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.Moody.Chat.ChatActivity;
import com.example.Moody.Model.ChatRoomModel;
import com.example.Moody.Model.UserModel;
//import com.example.phometalk.Model.FriendModel;
import com.example.Moody.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class FragmentFriend extends Fragment {
    private static final String TAG = "FragmentFriend";
    public static FragmentFriend newInstance() {
        return new FragmentFriend();
    }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerView fRecyclerView;
    private FriendAdapter fAdapter;

    private ArrayList<UserModel> uList = new ArrayList<UserModel>();
    private ArrayList<String> fid = new ArrayList<String>();
    private ImageView online_img;

    //제일 먼저 호출
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance(); //db초기화

        FriendListDisplay();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_friend,container,false);

        TextView myName = (TextView) view.findViewById(R.id.my_name);
        ImageView myImage = (ImageView) view.findViewById(R.id.my_image);
        final EditText friendSearch = (EditText)view.findViewById(R.id.chat_room_search);

        final Animation mAnim1 = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.top_border_short_anim);
        mAnim1.setInterpolator(activity.getApplicationContext(), android.R.anim.accelerate_interpolator);
        final Animation mAnim2 = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.main_fade_out_anim);
        mAnim2.setInterpolator(activity.getApplicationContext(), android.R.anim.accelerate_interpolator);

        MyInfo(myName,myImage);

        fRecyclerView = (RecyclerView)view.findViewById(R.id.chat_list_recyclerView);
        fRecyclerView.setHasFixedSize(true);
        fRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        fAdapter = new FriendAdapter(getContext(), uList);
        fRecyclerView.setAdapter(fAdapter);

        //검색
        friendSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        final FloatingActionButton addBtn = (FloatingActionButton)view.findViewById(R.id.friend_add_btn);
        final LinearLayout top1 = (LinearLayout)view.findViewById(R.id.top1);
        final LinearLayout layout1 = (LinearLayout)view.findViewById(R.id.layout1);
        final LinearLayout layout2 = (LinearLayout)view.findViewById(R.id.layout2);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //상단바 애니메이션
                top1.startAnimation(mAnim1);

                //fade-out
                layout1.startAnimation(mAnim2);
                layout2.startAnimation(mAnim2);
                addBtn.startAnimation(mAnim2);

                //딜레이
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(),AddFriendActivity.class));
                        getActivity().overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                    }
                }, 200);
            }
        });

        return view;
    }

    public void MyInfo(final TextView name, final ImageView image){
        database.getReference("userInfo").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel um = dataSnapshot.getValue(UserModel.class);
                name.setText(um.getName());
                if(um.getRange().equals("friend"))
                    image.setBackgroundResource(R.drawable.yj_profile_border);
                if (!um.getProfile().equals(""))
                        Glide.with(getContext()).load(um.getProfile()).apply(new RequestOptions().circleCrop()).into(image);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    //친구 리스트 불러오기
    public void FriendListDisplay(){
        //친구목록에서 친구 id 가져오기
        database.getReference("friend").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    fid.add(dataSnapshot1.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //유저정보 가져오기
        database.getReference("userInfo").orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    UserModel users = dataSnapshot1.getValue(UserModel.class);
                    for(int i=0;i<fid.size();i++){
                        if(users.getUID().equals(fid.get(i))){
                            uList.add(users);
                        }
                    }
                }
                fAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }


    //===============================================================================================================================

    public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
        private static final String TAG = "FriendAdapter";

        private Context context;
        private ArrayList<UserModel> uData; //필터링 안된 리스트(전체 리스트)
        private ArrayList<UserModel> filterList; //필터링 된 리스트(검색창에 입력이 있음)

        private String uid;
        private String roomid = null;
        private Boolean check=false;

        //생성자에서 데이터 리스트 객체를 전달받음
        public FriendAdapter(Context context, ArrayList<UserModel> list){
            super();
            this.context = context;
            this.uData = list;
            this.filterList = list;

        }

        //아이템 뷰를 저장하는 뷰홀더 클래스
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView photo;
            public TextView uName;
            public Button chatBtn;
            public ToggleButton heartBtn;

            ViewHolder(final View view) {
                super(view);

                photo = view.findViewById(R.id.chat_image1);
                uName = view.findViewById(R.id.user_sel_name);
                chatBtn = view.findViewById(R.id.friend_chatBtn);
                heartBtn = view.findViewById(R.id.heartBtn);

                //online_img
                online_img = view.findViewById(R.id.online_image);
                online_img.setVisibility(View.INVISIBLE);

            }

        }

        //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            uid = currentUser.getUid();

            //친한 친구 프로필
            if (filterList.get(position).getRange().equals("all")) {
                if (!filterList.get(position).getProfile().equals(""))
                    Glide.with(getContext()).load(filterList.get(position).getProfile()).apply(new RequestOptions().circleCrop()).into(holder.photo);
            }
            else if (filterList.get(position).getRange().equals("friend")) {
                if (filterList.get(position).getLiked() != null) {
                    for (String key : filterList.get(position).getLiked().keySet()) {
                        if (key.equals(uid)) {
                            holder.photo.setBackgroundResource(R.drawable.yj_profile_border);
                            if (!filterList.get(position).getProfile().equals(""))
                                Glide.with(getContext()).load(filterList.get(position).getProfile()).apply(new RequestOptions().circleCrop()).into(holder.photo);
                        }
                    }
                }
            }


            holder.uName.setText(filterList.get(position).getName());//사용자 이름

            //toggle button 유지
            database.getReference("userInfo").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel um = dataSnapshot.getValue(UserModel.class);
                    String friend = filterList.get(position).getUID();
                    if(um.getLiked() != null) {
                        for (String key : um.getLiked().keySet()) {
                            if(key.equals(friend))
                                holder.heartBtn.setBackgroundResource(R.drawable.yj_full_heart);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });


            holder.chatBtn.setOnClickListener(new View.OnClickListener() { //사용자 버튼 클릭시
                @Override
                public void onClick(View v) {
                    ChatDisplay(filterList.get(position).getUID(),filterList.get(position).getName(),filterList.get(position).getProfile());
                }
            });

            //친구 즐겨찾기 버튼 클릭
            holder.heartBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (holder.heartBtn.isChecked()) {
                        holder.heartBtn.setBackgroundResource(R.drawable.yj_full_heart);

                        String friend = filterList.get(position).getUID();

                        Map<String, Object> likedMap = new HashMap<>();
                        likedMap.put(friend, true);

                        database.getReference("friend").child(currentUser.getUid()).child(friend).removeValue();
                        database.getReference("friend").child(currentUser.getUid()).updateChildren(likedMap);
                        database.getReference("userInfo").child(currentUser.getUid()).child("liked").updateChildren(likedMap);
                    }
                    else{
                        holder.heartBtn.setBackgroundResource(R.drawable.feed_heart);

                        String friend = filterList.get(position).getUID();

                        Map<String, Object> likedMap = new HashMap<>();
                        likedMap.put(friend, false);

                        database.getReference("friend").child(currentUser.getUid()).child(friend).removeValue();
                        database.getReference("friend").child(currentUser.getUid()).updateChildren(likedMap);
                        database.getReference("userInfo").child(currentUser.getUid()).child("liked/"+friend).removeValue();


                    }

                }
            });

            //현재 사용자
            Boolean connection = uData.get(position).getConnection();
            System.out.println(connection);

            if(connection!=null) {
                if (connection == true) {
                    System.out.println("connection");
                    online_img.setVisibility(View.VISIBLE);
                } else
                    online_img.setVisibility(View.INVISIBLE);
            }
        }

        //아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
        @NonNull
        @Override
        public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

            ViewHolder vh = new ViewHolder(view);
            return vh;
        }


        @Override
        public int getItemCount() {
            //Log.d(TAG, "uData size: "+uData.length);
            return filterList.size();
        }

        //채팅방으로 바로 이동
        public void ChatDisplay(final String rec, final String name, final String profile) {

            //현재 로그인한 유저가 속해있는 채팅방 정보 출력
            database.getReference("ChatRoom").orderByChild("users/" +currentUser.getUid()).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ChatRoomModel croom = null;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        croom = dataSnapshot1.getValue(ChatRoomModel.class); // 사용자가 속한 채팅방 정보만 담김
                        if(croom.getUsers().size() == 2){ //1:1 채팅방인 경우만 찾는다.
                            Iterator<String> iter = croom.getUsers().keySet().iterator();
                            //users에서 상대방 id 찾는다.
                            while (iter.hasNext()) {
                                String keys = (String) iter.next();
                                if (keys.equals(rec)) {
                                    roomid = croom.getRoomID();
                                    check = true;
                                    break;
                                } else {
                                    roomid = null;
                                    check = false;
                                }
                            }
                        }else{check = false;}

                        if(check == true)
                            break;
                    }

                    if (check == true) {
                        //Toast.makeText(UserPageActivity.this,"채팅방 존재함",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), ChatActivity.class);

                        intent.putExtra("roomid", roomid);
                        intent.putExtra("receiver", rec);
                        intent.putExtra("recName", name);
                        intent.putExtra("recProfile",profile);
                        intent.putExtra("check","1");

                        startActivity(intent);

                    } else {
                        //Toast.makeText(UserPageActivity.this,"채팅방 없음",Toast.LENGTH_SHORT).show();
                        ChatRoomModel room = new ChatRoomModel();
                        //채팅방 id 생성
                        Map<String, Object> map = new HashMap<String, Object>();
                        final String roomkey = database.getReference().child("ChatRoom").push().getKey();
                        room.setRoomID(roomkey);
                        database.getReference().child("ChatRoom").updateChildren(map);

                        //현재 채팅방에 누가 있는지
                        HashMap<String, Boolean> users = new HashMap<String, Boolean>();
                        users.put(uid, true);
                        users.put(rec, true);
                        room.setUsers(users);

                        //DB에 roomID와 유저 목록 생성
                        Map<String, Object> objectMap = new HashMap<String, Object>();
                        objectMap.put("roomID", roomkey);
                        objectMap.put("users", users);
                        objectMap.put("lastTime", ServerValue.TIMESTAMP);//채팅방 생성 시간

                        //DB에 저장
                        database.getReference().child("ChatRoom").child(roomkey).setValue(objectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("roomid", roomkey);
                                intent.putExtra("receiver", rec);
                                intent.putExtra("recName", name);
                                intent.putExtra("check",1);
                                startActivity(intent);

                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {  }
            });

        }

        //친구 검색하기
        public Filter getFilter(){
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charString = constraint.toString();
                    if(charString.isEmpty()){ //입력받은게 없다면 전부 출력
                        filterList = uData;
                    }else{//있다면
                        ArrayList<UserModel> filtering = new ArrayList<>();
                        for(UserModel item: uData){
                            //사용자 이름으로 필터링
                            if(item.getName().toLowerCase().contains(charString.toLowerCase()))
                                filtering.add(item); //전체 데이터 중에서 입력받은 데이터만 추가
                        }
                        filterList = filtering; //검색창에서 입력받은 아이템만 출력한다.
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filterList;
                    return filterResults;
                }

                //필터링된걸로 리사이클러뷰 업데이트
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filterList = (ArrayList<UserModel>)results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

}