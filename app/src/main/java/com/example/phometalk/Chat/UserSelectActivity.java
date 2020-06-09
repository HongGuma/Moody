package com.example.phometalk.Chat;


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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phometalk.Model.ChatRoomModel;
import com.example.phometalk.Model.UserModel;
import com.example.phometalk.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSelectActivity extends Activity {
    private static final String TAG = "UserSelectActivity";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database;

    private RecyclerView uRecyclerView;
    private UserSelectAdapter uAdapter;

    private ArrayList<UserModel> userModels = new ArrayList<UserModel>();
    private ChatRoomModel room = new ChatRoomModel();

    private String uid;
    private String rec;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_select);

        UsersInfo();

        database = FirebaseDatabase.getInstance();
        uid = currentUser.getUid();

        uRecyclerView = (RecyclerView)findViewById(R.id.sel_recyclerview);
        uRecyclerView.setHasFixedSize(true); //리사이클러뷰 크기 고정
        uRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //리사이클러뷰 어뎁터
        uAdapter = new UserSelectAdapter(userModels);
        uRecyclerView.setAdapter(uAdapter);

        Button okBtn = (Button)findViewById(R.id.sel_ok_btn);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Select();

                //채팅방 id 생성
                Map<String, Object> map = new HashMap<String,Object>();
                final String roomkey = database.getReference().child("ChatRoom").push().getKey();
                room.setRoomID(roomkey);
                database.getReference().child("ChatRoom").updateChildren(map);

                //현재 채팅방에 누가 있는지
                HashMap<String,Boolean> users = new HashMap<String,Boolean>();
                users.put(uid,true);
                users.put(rec,true);
                room.setUsers(users);

                //DB에 roomID와 유저 목록 생성
                Map<String,Object> objectMap = new HashMap<String, Object>();
                objectMap.put("roomID",roomkey);
                objectMap.put("users",users);

                //DB에 저장
                database.getReference().child("ChatRoom").child(roomkey).setValue(objectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(UserSelectActivity.this, "채팅방 생성",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                        intent.putExtra("roomid",roomkey);
                        startActivity(intent);

                        finish();
                    }

                });

            }
        });

    }

    public void Select(){

        for(int i = 0;i<userModels.size();i++){
            Boolean check = userModels.get(i).getCheck();
            if(check != null){
                rec = userModels.get(i).getUID();
            }
        }

    }



    public void UsersInfo() {
        database = FirebaseDatabase.getInstance();
        database.getReference().child("userInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel u;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    u = dataSnapshot1.getValue(UserModel.class);
                    if(u.getUID().equals(currentUser.getUid()))
                        continue;
                    userModels.add(u);
                }
                uAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //==============================================================================================================//

    public class UserSelectAdapter extends RecyclerView.Adapter<UserSelectAdapter.ViewHolder>{
        private static final String TAG = "UserSelectAdapter";

        private ArrayList<UserModel> userList; //전체 유저 리스트
        private ArrayList<UserModel> userSel; //선택한 유저 리스트

        public UserSelectAdapter(ArrayList<UserModel> list){
            this.userList = list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView selImage;
            public TextView selName;
            public CheckBox selBox;

            ViewHolder(View view) {
                super(view);
                selImage = (ImageView)view.findViewById(R.id.user_sel_image);
                selName = (TextView)view.findViewById(R.id.user_sel_name);
                selBox = (CheckBox)view.findViewById(R.id.user_sel_checkbox);
            }
        }

        @NonNull
        @Override
        public UserSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_select,parent,false);

            UserSelectAdapter.ViewHolder vh = new UserSelectAdapter.ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull UserSelectAdapter.ViewHolder holder, final int position) {
                        holder.selName.setText(userList.get(position).getName());

            holder.selBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox)v;

                    userModels.get(position).setCheck(cb.isChecked());

                    //Toast.makeText(v.getContext(),"선택됨 : "+userList.get(position).getUID()+", "+cb.isChecked(),Toast.LENGTH_SHORT).show();

                }
            });


        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
    }

}
