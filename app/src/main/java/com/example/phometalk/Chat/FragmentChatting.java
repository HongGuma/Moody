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
import java.util.TimeZone;

public class FragmentChatting extends Fragment {
    private static final String TAG = "FragmentChatting";
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database =FirebaseDatabase.getInstance();

    private RecyclerView crRecyclerView;
    private ChatRoomListAdapter crAdapter;
    private ArrayList<ChatRoomModel> cList = new ArrayList<ChatRoomModel>();

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
                ChatRoomModel room;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    room = dataSnapshot1.getValue(ChatRoomModel.class);
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

        private ArrayList<String> recID = new ArrayList<String>(); //상대방 id
        private ArrayList<String> recName = new ArrayList<String>();//상대방 이름
        private ArrayList<String> recProfile = new ArrayList<String>();//상대방 프로필
        private ArrayList<String> rID = new ArrayList<String>(); //채팅방 id

        SimpleDateFormat writeTimeFormat = new SimpleDateFormat("a hh:mm");


        public ChatRoomListAdapter(ArrayList<ChatRoomModel> list) {
            this.chatRoomList = list;
            this.filterList = list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView userImage;
            TextView username;
            TextView lastMsg;
            TextView time;
            ConstraintLayout itemLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                userImage = (ImageView) itemView.findViewById(R.id.chat_image);
                username = (TextView) itemView.findViewById(R.id.chat_user);
                lastMsg = (TextView) itemView.findViewById(R.id.chat_lastMsg);
                time = (TextView) itemView.findViewById(R.id.chat_time);
                itemLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_item_layout);
            }
        }

        @NonNull
        @Override
        public ChatRoomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatlist, parent, false);

            ViewHolder vh = new ViewHolder(view);

            return vh;
        }

        @Override
        public int getItemViewType(int position) {

            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(@NonNull final ChatRoomListAdapter.ViewHolder holder, final int position) {
            rID.add(filterList.get(position).getRoomID());//
            String rec = null;

            //채팅방에 있는 유저 체크
            for (String user : filterList.get(position).getUsers().keySet()) {
                if (!user.equals(uid)) {
                    rec = user;
                    recID.add(rec);//
                }
            }

            //상대방 정보
            database.getReference("userInfo").child(rec).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel um = dataSnapshot.getValue(UserModel.class);
                    if (!um.getProfile().equals(""))
                        Glide.with(getContext()).load(um.getProfile()).apply(new RequestOptions().circleCrop()).into(holder.userImage);
                    holder.username.setText(um.getName());
                    recName.add(um.getName());//
                    recProfile.add(um.getProfile());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            //채팅방 정보 꺼내서 출력
            database.getReference("ChatRoom").child(filterList.get(position).getRoomID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ChatRoomModel crm = dataSnapshot.getValue(ChatRoomModel.class);
                    holder.lastMsg.setText(crm.getLastMsg()); //마지막 메시지

                    //시간 포멧
                    long unixTime = (long) crm.getLastTime();
                    Date date = new Date(unixTime);
                    writeTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                    String time = writeTimeFormat.format(date);
                    //마지막 시간출력
                    holder.time.setText(time);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
                    intent.putExtra("recName", filterList.get(position).getRoomName());
                    intent.putExtra("receiver", recID.get(position));
                    intent.putExtra("roomid", rID.get(position));
                    intent.putExtra("recProfile", recProfile.get(position));
                    intent.putExtra("check", "1");

                    startActivity(intent);
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

    }

}