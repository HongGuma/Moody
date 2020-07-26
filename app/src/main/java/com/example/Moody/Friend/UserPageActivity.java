package com.example.Moody.Friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.Moody.Chat.ChatActivity;
import com.example.Moody.Model.ChatRoomModel;
import com.example.Moody.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserPageActivity extends Activity {
    private static final String TAG = "UserPageActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String uid = mAuth.getCurrentUser().getUid();
    private String roomid = null;
    private Boolean check=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        //친구목록에서 받아온 상대방 id,name
        final String recID = getIntent().getStringExtra("receiver");
        final String recName = getIntent().getStringExtra("recName");

        //텍스트뷰
        TextView uName = (TextView)findViewById(R.id.user_page_name);
        uName.setText(recName);

        //채팅하기 버튼
        Button chatBtn = (Button)findViewById(R.id.user_page_btn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ChatListDisplay(recID,recName); //채팅방 목록에서 해당 유저와 있는 방 찾는 함수
            }
        });

    }

    public void ChatListDisplay(final String rec, final String name) {

        //현재 로그인한 유저가 속해있는 채팅방 정보 출력
        database.getReference().child("ChatRoom").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatRoomModel croom = null;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    croom = dataSnapshot1.getValue(ChatRoomModel.class); // 사용자가 속한 채팅방 정보만 담김
                    Iterator<String> iter = croom.getUsers().keySet().iterator();
                    //users에서 상대방 id 찾는다.
                    while (iter.hasNext()) {
                        String keys = (String) iter.next();
                        if(keys.equals(rec)){
                            roomid = croom.getRoomID();
                            check = true;
                            break;
                        }else {
                            roomid = null;
                            check = false;
                        }
                    }
                    if(check == true)
                        break;
                }

                if (check == true) {
                    //Toast.makeText(UserPageActivity.this,"채팅방 존재함",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserPageActivity.this, ChatActivity.class);
                    intent.putExtra("roomid", roomid);
                    intent.putExtra("receiver", rec);
                    intent.putExtra("recName", name);

                    startActivity(intent);
                    finish();

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

                    //DB에 저장
                    database.getReference().child("ChatRoom").child(roomkey).setValue(objectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("roomid", roomkey);
                            startActivity(intent);

                            finish();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });

    }

}
