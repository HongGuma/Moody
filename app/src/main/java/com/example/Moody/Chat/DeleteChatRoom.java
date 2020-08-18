package com.example.Moody.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.Moody.Activity.MainActivity;
import com.example.Moody.Model.ChatRoomModel;
import com.example.Moody.Model.UserModel;
import com.example.Moody.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

public class DeleteChatRoom extends Activity {
    private static final String TAG = "DeleteChatRoom";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerView dRecyclerView;
    private DeleteAdapter DeleteAdapter;

    private ArrayList<ChatRoomModel> chatRoomModels = new ArrayList<ChatRoomModel>();
    private ArrayList<String> roomID = new ArrayList<String>();

    private String uid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_chat_delete);

        ImageView myImageView = (ImageView)findViewById(R.id.my_image);
        TextView myNameView = (TextView)findViewById(R.id.my_name);
        Button backBtn = (Button)findViewById(R.id.chat_add_backBtn);
        LinearLayout okBtn = (LinearLayout)findViewById(R.id.sel_ok_btn);

        myInfo(myNameView,myImageView);
        ChatRoomListDisplay();

        dRecyclerView = (RecyclerView)findViewById(R.id.sel_recyclerview);
        dRecyclerView.setHasFixedSize(true); //리사이클러뷰 크기 고정
        dRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DeleteAdapter = new DeleteAdapter(chatRoomModels);
        dRecyclerView.setAdapter(DeleteAdapter);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Select();

                for(int i=0;i<roomID.size();i++){
                    database.getReference("ChatRoom").child(roomID.get(i)).removeValue(); //채팅방 정보 지우기
                    database.getReference("Message").child(roomID.get(i)).removeValue();//채팅내역 지우기
                }



                /*
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fragment", "chat");
                startActivity(intent);

                 */
                //finish();

            }
        });
    }

    //채팅방 선택
    public void Select(){

        for(int i = 0;i<chatRoomModels.size();i++){
            Boolean check = chatRoomModels.get(i).getCheck();
            if(check != null){
                roomID.add(chatRoomModels.get(i).getRoomID());
            }
        }

    }

    //사용자 정보
    public void myInfo(final TextView name, final ImageView image){
        database.getReference("userInfo").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel um = dataSnapshot.getValue(UserModel.class);
                name.setText(um.getName()); //채팅방 상단에 사용자 정보
                if (um.getRange().equals("all")) {
                    if (!um.getProfile().equals(""))
                        Glide.with(image.getContext()).load(um.getProfile()).apply(new RequestOptions().circleCrop()).into(image);
                }
                else if (um.getRange().equals("friend")) {
                    image.setBackgroundResource(R.drawable.yj_profile_border);
                    if (!um.getProfile().equals(""))
                        Glide.with(image.getContext()).load(um.getProfile()).apply(new RequestOptions().circleCrop()).into(image);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    //채팅방 정보
    public void ChatRoomListDisplay() {
        uid = currentUser.getUid();
        //현재 로그인한 유저가 속해있는 채팅방 정보 출력
        database.getReference("ChatRoom").orderByChild("lastTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatRoomModels.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatRoomModel room = dataSnapshot1.getValue(ChatRoomModel.class);
                    Iterator<String> iter = room.getUsers().keySet().iterator();
                    //users에서 상대방 id 찾는다.
                    while (iter.hasNext()) {
                        String keys = (String) iter.next();
                        if(keys.equals(uid)){
                            chatRoomModels.add(0,room);
                        }
                    }
                }
                DeleteAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //=============================================================================================================================================================================

    public class DeleteAdapter extends RecyclerView.Adapter<DeleteAdapter.ViewHolder>{
        private static final String TAG = "DeleteAdapter";
        private ArrayList<ChatRoomModel> chatRoomModel = new ArrayList<>();
        private ArrayList<String> user = new ArrayList<>(); //상대방 id
        private ArrayList<String> roomID = new ArrayList<String>(); //채팅방 id
        private ArrayList<String> profiles = new ArrayList<>();// 프로필
        private Map<Integer,String> names = new HashMap<>();//상대방 이름
        private Map<Integer,String> recID = new HashMap<>();//상대방 id

        SimpleDateFormat writeTimeFormat = new SimpleDateFormat("a hh:mm");

        public DeleteAdapter(ArrayList<ChatRoomModel> list){
            this.chatRoomModel = list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView selImage;
            public TextView selName;
            public CheckBox selBox;

            ViewHolder(View view) {
                super(view);
                selImage = (ImageView)view.findViewById(R.id.chat_image1);
                selName = (TextView)view.findViewById(R.id.user_sel_name);
                selBox = (CheckBox)view.findViewById(R.id.user_sel_checkbox);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(chatRoomModels.get(position).getUsers().size()>2){ //그룹채팅
                return 2;
            }
            return 1; //아니면 1:1 채팅
        }

        @NonNull
        @Override
        public DeleteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == 1){
                //1:1 채팅
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_chat_delete, parent, false);
            }else{
                //단체 채팅
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_group_chat, parent, false);
            }

            DeleteAdapter.ViewHolder vh = new DeleteAdapter.ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            roomID.add(chatRoomModel.get(position).getRoomID()); //채팅방 id

            user.clear();
            for(String id: chatRoomModel.get(position).getUsers().keySet()){
                if(!id.equals(currentUser.getUid()))
                    user.add(id);
            }

            for(int i=0; i<chatRoomModel.get(position).getUsers().size()-1; i++){
                database.getReference("userInfo").child(user.get(i)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel um = dataSnapshot.getValue(UserModel.class);
                        holder.selName.setText(um.getName());
                        Log.d(TAG, "onDataChange: position="+um.getName());
                        names.put(position,um.getName());
                        recID.put(position,um.getUID());
                        if(chatRoomModel.get(position).getUsers().size()==2){ //개인채팅방
                            if (um.getRange().equals("all")) {
                                if (!um.getProfile().equals(""))
                                    Glide.with(holder.selImage.getContext()).load(um.getProfile()).apply(new RequestOptions().circleCrop()).into(holder.selImage);
                            }
                            else if (um.getRange().equals("friend")) {
                                if (um.getLiked() != null) {
                                    for (String key : um.getLiked().keySet()) {
                                        if (key.equals(uid)) {
                                            holder.selImage.setBackgroundResource(R.drawable.yj_profile_border);
                                            if (!um.getProfile().equals(""))
                                                Glide.with(holder.selImage.getContext()).load(um.getProfile()).apply(new RequestOptions().circleCrop()).into(holder.selImage);
                                        }
                                    }
                                }
                            }
                        }else{ //단체 채팅방
                            /*
                            if (um.getRange().equals("all")) {
                                if (!um.getProfile().equals(""))
                                    Glide.with(holder.selImage.getContext()).load(um.getProfile()).apply(new RequestOptions().circleCrop()).into(holder.selImage);
                            }
                            else if (um.getRange().equals("friend")) {
                                if (um.getLiked() != null) {
                                    for (String key : um.getLiked().keySet()) {
                                        if (key.equals(uid)) {
                                            holder.selImage.setBackgroundResource(R.drawable.yj_profile_border);
                                            if (!um.getProfile().equals(""))
                                                Glide.with(holder.selImage.getContext()).load(um.getProfile()).apply(new RequestOptions().circleCrop()).into(holder.selImage);
                                        }
                                    }
                                }
                            }

                             */
                            /*if(!um.getProfile().equals("")) {
                                Glide.with(holder.userImage1.getContext())
                                        .load(um.getProfile())
                                        .apply(new RequestOptions().circleCrop())
                                        .error(R.drawable.friend_profile)
                                        .into(holder.userImage1);
                            }*/
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){}
                });

            }

            database.getReference("ChatRoom").child(chatRoomModel.get(position).getRoomID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ChatRoomModel crm = dataSnapshot.getValue(ChatRoomModel.class);
                        //시간 포맷
                        long unixTime = (long) crm.getLastTime();
                        Date date = new Date(unixTime);
                        writeTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                        String time = writeTimeFormat.format(date);
                        //채팅방 이름(단톡방만)
                        if (!crm.getRoomName().equals("")) {
                            holder.selName.setText(crm.getRoomName());
                            names.remove(position);
                            names.put(position, crm.getRoomName());
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            holder.selBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox)v;
                    chatRoomModel.get(position).setCheck(cb.isChecked());
                    //Toast.makeText(v.getContext(),"선택됨 : "+userList.get(position).getUID()+", "+cb.isChecked(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatRoomModel.size();
        }
    }
}
