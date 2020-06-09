package com.example.phometalk.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.Model.ChatModel;
import com.example.phometalk.Model.ChatRoomModel;
import com.example.phometalk.Model.UserModel;
import com.example.phometalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

        crRecyclerView = (RecyclerView)view.findViewById(R.id.chat_list_recyclerView);

        crRecyclerView.setHasFixedSize(true);
        crRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        crAdapter = new ChatRoomListAdapter(cList);
        crRecyclerView.setAdapter(crAdapter);

        Button newBtn = (Button)view.findViewById(R.id.chat_new_room);

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),UserSelectActivity.class);
                v.getContext().startActivity(intent);
            }
        });



        //ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        //actionBar.setTitle("채팅");
        //actionBar.setDisplayHomeAsUpEnabled(false);

        return view;
    }

    public void ChatListDisplay() {
        uid = currentUser.getUid();
        //현재 로그인한 유저가 속해있는 채팅방 정보 출력
        database.getReference().child("ChatRoom").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cList.clear();
                ChatRoomModel room;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    room = dataSnapshot1.getValue(ChatRoomModel.class);
                    cList.add(room);
                }
                crAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });

    }

    //=======================================================================================================================

    class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder>{
        private static final String TAG = "ChatRoomListAdapter";

        private ArrayList<ChatRoomModel> chatRoomList;
        private ArrayList<String> recID = new ArrayList<String>(); //상대방 id
        private ArrayList<String> recName = new ArrayList<String>();
        private ArrayList<String> rID = new ArrayList<String>(); //채팅방 id


        public ChatRoomListAdapter(ArrayList<ChatRoomModel> list){
            this.chatRoomList = list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView userImage;
            TextView username;
            TextView lastMsg;
            TextView time;
            ConstraintLayout itemLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                userImage = (ImageView)itemView.findViewById(R.id.chat_image);
                username = (TextView)itemView.findViewById(R.id.chat_user);
                lastMsg = (TextView)itemView.findViewById(R.id.chat_lastMsg);
                time = (TextView)itemView.findViewById(R.id.chat_time);
                itemLayout = (ConstraintLayout)itemView.findViewById(R.id.chat_item_layout);
            }
        }

        @NonNull
        @Override
        public ChatRoomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatlist,parent,false);

            ViewHolder vh = new ViewHolder(view);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final ChatRoomListAdapter.ViewHolder holder, final int position) {
            rID.add(chatRoomList.get(position).getRoomID());
            String rec = null;
            //채팅방에 있는 유저 체크
            for(String user: chatRoomList.get(position).getUsers().keySet()){
                if(!user.equals(uid)) {
                    rec = user;
                    recID.add(rec);
                }
            }

            //유저 정보 꺼내서 출력
            database.getReference().child("userInfo").child(rec).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    holder.username.setText(userModel.getName());
                    recName.add(userModel.getName());

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {  }
            });

            //메세지 정보 꺼내서 출력
           database.getReference().child("Message").child(rID.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        ChatModel cm = dataSnapshot1.getValue(ChatModel.class);
                        chatRoomList.get(position).setLastMsg(cm.getMsg());
                        chatRoomList.get(position).setLastTime(cm.getTimestamp());
                    }
                    holder.lastMsg.setText(chatRoomList.get(position).getLastMsg());
                    holder.time.setText(chatRoomList.get(position).getLastTime());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {  }
            });

            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),ChatActivity.class);
                    intent.putExtra("recName",recName.get(position));
                    intent.putExtra("receiver",recID.get(position));
                    intent.putExtra("roomid",rID.get(position));

                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return chatRoomList.size();
        }


    }
}
