package com.example.Moody.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.Moody.Model.ChatModel;
import com.example.Moody.Model.ChatRoomModel;
import com.example.Moody.Model.UserModel;
import com.example.Moody.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.ViewHolder> {
    private static final String TAG = "PersonalAdapter";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private ArrayList<ChatModel> chatModel = new ArrayList<>();
    private String roomID;
    private String recID;

    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    String wDate = dateFormat1.format(Calendar.getInstance().getTime());

    SimpleDateFormat writeTimeFormat = new SimpleDateFormat("a hh:mm");

    //생성자에서 데이터 리스트 객체를 전달받음
    public PersonalAdapter(String receiver,String roomid,ArrayList<ChatModel> list){
        this.recID=receiver;
        this.roomID=roomid;
        this.chatModel = list;


    }

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView userImage;
        public TextView userName;
        public TextView textView;
        public TextView timestamp;
        public ImageView sendPhoto;
        public TextView timeLien;
        public TextView readNum;

        ViewHolder(View view){
            super(view);
            userImage = (ImageView)view.findViewById(R.id.user_image);
            userName = (TextView)view.findViewById(R.id.user_name);
            textView = (TextView)view.findViewById(R.id.tvChat);
            timestamp = (TextView)view.findViewById(R.id.timestamp);
            sendPhoto = (ImageView)view.findViewById(R.id.ivChat);
            timeLien = (TextView)view.findViewById(R.id.chat_time_line);
            readNum = (TextView)view.findViewById(R.id.read_number);

        }

    }

    //상대방이 보낸 메세지인지 구분
    @Override
    public int getItemViewType(int position) {
        if(chatModel.get(position).getUID().equals(currentUser.getUid())){
            switch (chatModel.get(position).getMsgType()){
                case "0": return 1; //내가 보낸 텍스트
                case "1": return 2; //내가 보낸 사진
                default: return 1; //예외는 그냥 텍스트
            }
        }else {
            switch (chatModel.get(position).getMsgType()){
                case "0": return 3; //상대방이 보낸 텍스트
                case "1": return 4; //상대방이 보낸 사진
                default: return 3; // 예외는 텍스트로
            }
        }
    }

    //아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public PersonalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatbubble_right,parent,false);
        }else if(viewType ==2){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_right,parent,false);
        }else if(viewType == 4){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_left,parent,false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatbubble_left,parent,false);
        }

        PersonalAdapter.ViewHolder vh = new PersonalAdapter.ViewHolder(view);

        return vh;

    }


    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull final PersonalAdapter.ViewHolder holder, final int position) {
        //시간 포맷
        long unixTime = (long) chatModel.get(position).getTimestamp();
        Date date = new Date(unixTime);
        writeTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String time = writeTimeFormat.format(date);
        //시간 출력
        holder.timestamp.setText(time);

        if(chatModel.get(position).getMsgType().equals("0")){ //메시지 타입이 0이면 텍스트
            holder.textView.setText(chatModel.get(position).getMsg());
        }else{//아니면 이미지뷰
            Glide.with(holder.sendPhoto.getContext()).load(chatModel.get(position).getMsg()).into(holder.sendPhoto);
        }

        ReaderCounter(position,holder.readNum); //읽음표시


        //내 uid가 아니면 다른 뷰가 오기 때문에
        if(!chatModel.get(position).getUID().equals(currentUser.getUid())){
            holder.userName.setText(chatModel.get(position).getUserName());// 상대방 이름
            database.getReference("userInfo").child(chatModel.get(position).getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel um = dataSnapshot.getValue(UserModel.class);
                    if(!um.getProfile().equals("")){//프로필 사진이 있으면
                        Glide.with(holder.userImage.getContext())
                                .load(um.getProfile())
                                .apply(new RequestOptions().circleCrop())
                                .error(R.drawable.user)
                                .into(holder.userImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }



    }

    //개수만큼 아이템 생성
    @Override
    public int getItemCount() {
        return chatModel.size();
    }

    public void ReaderCounter(final int position, final TextView readNumber){

        database.getReference("ChatRoom").child(roomID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatRoomModel cm = dataSnapshot.getValue(ChatRoomModel.class);
                int count = cm.getUsers().size()-chatModel.get(position).getReadUsers().size();
                //Log.d(TAG, "onDataChange: count="+count);
                if(count>0){
                    readNumber.setVisibility(View.VISIBLE);
                    readNumber.setText(String.valueOf(count));
                }else{
                    readNumber.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }

}
