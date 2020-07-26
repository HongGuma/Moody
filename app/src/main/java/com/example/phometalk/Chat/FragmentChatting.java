package com.example.phometalk.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.phometalk.Activity.IntroActivity;
import com.example.phometalk.Firebase.Image;
import com.example.phometalk.Model.ChatModel;
import com.example.phometalk.Model.ChatRoomModel;
import com.example.phometalk.Model.FeedItems;
import com.example.phometalk.Model.UserModel;
import com.example.phometalk.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.Interpreter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

public class FragmentChatting extends Fragment {
    private static final String TAG = "FragmentChatting";
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database =FirebaseDatabase.getInstance();

    private RecyclerView crRecyclerView;
    private ChatRoomListAdapter crAdapter;
    private ArrayList<ChatRoomModel> cList = new ArrayList<ChatRoomModel>();
    private Map<String,Object> users = new HashMap<>();

    private String uid;
    private String roomID;

    public static FragmentChatting newInstance(){
        return new FragmentChatting();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(); //db초기화
        ChatListDisplay();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_chat_list,container,false);

        TextView myName = (TextView)view.findViewById(R.id.my_name);
        ImageView myImage = (ImageView)view.findViewById(R.id.my_image);
        EditText chatSearch = (EditText)view.findViewById(R.id.chat_room_search);
        myInfo(myName,myImage); //내정보 가져오기

        crRecyclerView = (RecyclerView)view.findViewById(R.id.chat_list_recyclerView);//리사이클러뷰
        crRecyclerView.setHasFixedSize(true);//리사이클러뷰 크기 고정
        //레이아웃 매니저
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(inflater.getContext());
        crRecyclerView.setLayoutManager(mLayoutManager);

        crAdapter = new ChatRoomListAdapter(cList);
        crRecyclerView.setAdapter(crAdapter);

        FloatingActionButton newBtn = (FloatingActionButton)view.findViewById(R.id.chat_new_room);

        //새 채팅방 생성 버튼
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),UserSelectActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        //검색
        chatSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        //ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        //actionBar.setTitle("채팅");
        //actionBar.setDisplayHomeAsUpEnabled(false);

        return view;
    }

    public void myInfo(final TextView name, final ImageView image){
        database.getReference("userInfo").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel um = dataSnapshot.getValue(UserModel.class);
                name.setText(um.getName()); //채팅방 상단에 사용자 정보
                Glide.with(getContext()).load(um.getProfile()).apply(new RequestOptions().circleCrop()).into(image);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    public void ChatListDisplay() {
        uid = currentUser.getUid();
        //현재 로그인한 유저가 속해있는 채팅방 정보 출력
        database.getReference("ChatRoom").orderByChild("lastTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatRoomModel room = dataSnapshot1.getValue(ChatRoomModel.class);
                    Iterator<String> iter = room.getUsers().keySet().iterator();
                    //users에서 상대방 id 찾는다.
                    while (iter.hasNext()) {
                        String keys = (String) iter.next();
                        if(keys.equals(uid)){
                            cList.add(0,room);
                        }
                    }
                }
                crAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });

    }



    //=======================================================================================================================

    class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder> {
        private static final String TAG = "ChatRoomListAdapter";

        private ArrayList<ChatRoomModel> chatRoomList; //전체 데이터
        private ArrayList<ChatRoomModel> filterList; //검색된 데이터

        private ArrayList<String> user = new ArrayList<>(); //상대방 id
        private ArrayList<String> roomID = new ArrayList<String>(); //채팅방 id
        private ArrayList<String> profiles = new ArrayList<>();// 프로필
        private Map<Integer,String> names = new HashMap<>();//상대방 이름
        private Map<Integer,String> recID = new HashMap<>();//상대방 id

        SimpleDateFormat writeTimeFormat = new SimpleDateFormat("a hh:mm");


        public ChatRoomListAdapter(ArrayList<ChatRoomModel> list) {
            this.chatRoomList = list;
            this.filterList = list;

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView userImage;
            ImageView userImage1;
            ImageView userImage2;
            ImageView userImage3;
            ImageView userImage4;
            TextView roomName;
            TextView lastMsg;
            TextView time;
            LinearLayout itemLayout;
            TextView msgCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                userImage = (ImageView) itemView.findViewById(R.id.chat_image);
                userImage1 = (ImageView) itemView.findViewById(R.id.chat_image1);
                userImage2 = (ImageView) itemView.findViewById(R.id.chat_image2);
                userImage3 = (ImageView) itemView.findViewById(R.id.chat_image3);
                userImage4 = (ImageView) itemView.findViewById(R.id.chat_image4);
                roomName = (TextView) itemView.findViewById(R.id.chat_room_name);
                lastMsg = (TextView) itemView.findViewById(R.id.chat_lastMsg);
                time = (TextView) itemView.findViewById(R.id.chat_time);
                itemLayout = (LinearLayout) itemView.findViewById(R.id.chatList_layout);
                msgCount = (TextView) itemView.findViewById(R.id.msg_count);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(filterList.get(position).getUsers().size()>2){ //그룹채팅
                return 2;
            }
            return 1; //아니면 1:1 채팅
        }

        @NonNull
        @Override
        public ChatRoomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == 1){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatlist, parent, false);
            }else{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_chatlist, parent, false);
            }

            ViewHolder vh = new ViewHolder(view);
            return vh;
        }



        @Override
        public void onBindViewHolder(@NonNull final ChatRoomListAdapter.ViewHolder holder, final int position) {
            roomID.add(filterList.get(position).getRoomID());


            user.clear();
            for(String id: filterList.get(position).getUsers().keySet()){
                if(!id.equals(currentUser.getUid()))
                    user.add(id);
            }


            for(int i=0; i<filterList.get(position).getUsers().size()-1; i++){
                database.getReference("userInfo").child(user.get(i)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel um = dataSnapshot.getValue(UserModel.class);
                        holder.roomName.setText(um.getName());
                        names.put(position,um.getName());
                        recID.put(position,um.getName());
                        if(filterList.get(position).getUsers().size()==2){ //개인채팅방
                            //if(!um.getProfile().equals("")) {
                                Glide.with(holder.userImage.getContext())
                                        .load(um.getProfile())
                                        .apply(new RequestOptions().circleCrop())
                                        .error(R.drawable.friend_profile)
                                        .into(holder.userImage);
                            //}
                        }else{ //단체 채팅방
                            if(!um.getProfile().equals("")) {
                                profiles.add(position,um.getProfile());
                                Glide.with(holder.userImage1.getContext())
                                        .load(um.getProfile())
                                        .apply(new RequestOptions().circleCrop())
                                        .error(R.drawable.friend_profile)
                                        .into(holder.userImage1);
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){}
                });

            }

            database.getReference("ChatRoom").child(filterList.get(position).getRoomID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ChatRoomModel crm = dataSnapshot.getValue(ChatRoomModel.class);
                    //시간 포맷
                    long unixTime = (long) crm.getLastTime();
                    Date date = new Date(unixTime);
                    writeTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                    String time = writeTimeFormat.format(date);
                    //시간 출력
                    holder.time.setText(time);
                    //마지막 메시지
                    holder.lastMsg.setText(crm.getLastMsg());
                    //채팅방 이름(단톡방만)
                    if(!crm.getRoomName().equals("")){
                        holder.roomName.setText(crm.getRoomName());
                        names.remove(position);
                        names.put(position,crm.getRoomName());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            MessageCount(position,holder.msgCount,filterList.get(position).getRoomID());

            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("roomid", roomID.get(position));
                    if(filterList.get(position).getUsers().size()>2) {
                        //단체 채팅방
                        intent.putExtra("name",names.get(position)); //채팅방 이름 전달
                        intent.putExtra("check", "2");
                        startActivity(intent);
                    }else {
                        //개인 채팅방
                        intent.putExtra("name",names.get(position)); //이름 전달
                        intent.putExtra("receiver",recID.get(position)); //id 전달
                        Log.d(TAG, "onClick: recID="+recID.get(position));
                        intent.putExtra("check", "1");
                        startActivity(intent);
                    }


                }
            });




        }

        @Override
        public int getItemCount() {
            return filterList.size();
        }


        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charString = constraint.toString();
                    if (charString.isEmpty()) { //입력받은게 없다면 전부 출력
                        filterList = chatRoomList;
                    } else {//있다면
                        ArrayList<ChatRoomModel> filtering = new ArrayList<>();
                        for (ChatRoomModel item : chatRoomList) {
                            //채팅방 이름으로 필터링
                            if (item.getRoomName().toLowerCase().contains(charString.toLowerCase()))
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
                    filterList = (ArrayList<ChatRoomModel>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        public void MessageCount(int position, final TextView msgCount, String rID){
            database.getReference("Message").child(rID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = 0;
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        ChatModel cm = dataSnapshot1.getValue(ChatModel.class);
                        for(String user:cm.getReadUsers().keySet()){
                            if(!user.equals(currentUser.getUid())){
                                count++;
                            }else{
                                count--;
                            }

                        }

                    }
                    if(count >0){
                        msgCount.setVisibility(View.VISIBLE);
                        msgCount.setText(String.valueOf(count));
                    }else{
                        msgCount.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

    }

}