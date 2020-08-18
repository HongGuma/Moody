package com.example.Moody.Chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Moody.Model.UserModel;
import com.example.Moody.R;
import com.example.Moody.Model.FeedItems;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TabImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<FeedItems> tagDataArrayList;
    Context context;
    private String imageUrl;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();//현재 로그인 정보
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String uName;

    public TabImageAdapter(Context context,ArrayList<FeedItems> tagDataArrayList){
        this.context=context;
        this.tagDataArrayList = tagDataArrayList;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        MyViewHolder(View view){
            super(view);
            image = view.findViewById(R.id.upload_image);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auto_image, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        if(tagDataArrayList.get(position).getUrl()==null) {
            myViewHolder.image.setImageBitmap(tagDataArrayList.get(position).getImage());
        }
        else{
            Glide.with(context).load(tagDataArrayList.get(position).getUrl()).into(myViewHolder.image);
        }

        myViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                String datetime = dateFormat.format(Calendar.getInstance().getTime());

                if(tagDataArrayList.get(position).getUrl()!=null) {
                    sendImage(tagDataArrayList.get(position).getUrl());
                }else{
                    String imagePath = "Chat/"+ ChatActivity.roomid+"/"+datetime; //사진파일 경로 및 이름
                    UploadFiles(tagDataArrayList.get(position).getImage(),imagePath);
                }
                if (!(AutoChatActivity.sText.equals(""))) {
                    DatabaseReference ref = database.getReference("Message").child(ChatActivity.roomid);
                    SimpleDateFormat writeTimeFormat = new SimpleDateFormat("a hh:mm");
                    final String writeTime = writeTimeFormat.format(Calendar.getInstance().getTime());
                    HashMap<String, Object> member = new HashMap<String, Object>();
                    member.put("uID", currentUser.getUid()); //보낸사람 id
                    member.put("userName", ChatActivity.uName); //보낸 사람 이름
                    member.put("msg", ChatActivity.sText);
                    member.put("timestamp", ServerValue.TIMESTAMP);
                    member.put("msgType", "0");
                    ref.push().setValue(member);

                    HashMap<String, Object> last = new HashMap<String, Object>();
                    last.put("lastMsg",ChatActivity.sText);
                    last.put("lastTime",ServerValue.TIMESTAMP);
                    database.getReference("ChatRoom").child(ChatActivity.roomid).updateChildren(last);
                }
                ((AutoChatActivity)context).finish();
            }
        });
    }
    public void UploadFiles(Bitmap bitmap, final String path) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        final StorageReference storageRef = storage.getReference();
        SimpleDateFormat writeTimeFormat = new SimpleDateFormat("a hh:mm");
        final String writeTime = writeTimeFormat.format(Calendar.getInstance().getTime());
        final StorageReference riversRef = storageRef.child(path);
        UploadTask uploadTask = riversRef.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    //DB에 저장
                    DatabaseReference ref = database.getReference("Message").child(ChatActivity.roomid);
                    Map<String,Object> read = new HashMap<>();
                    read.put(currentUser.getUid(),true);
                    HashMap<String, Object> member = new HashMap<String, Object>();
                    member.put("uID", currentUser.getUid()); //보낸사람 id
                    member.put("userName", uName); //보낸 사람 이름
                    member.put("msg", imageUrl); //url
                    member.put("timestamp",ServerValue.TIMESTAMP); //작성 시간
                    member.put("msgType","1"); //메세지 타입
                    member.put("readUsers",read);
                    ref.push().setValue(member); //DB에 저장

                    HashMap<String, Object> chatroom = new HashMap<String, Object>();
                    chatroom.put("lastMsg","사진"); //사진일때
                    chatroom.put("lastTime",ServerValue.TIMESTAMP); //마지막 시간
                    database.getReference("ChatRoom").child(ChatActivity.roomid).updateChildren(chatroom);
                    //Glide.with(UpLoadImageToFirebase.this).load(imageUrl).into(upload_image);
                }
            }
        });
    }

    public void sendImage(String url){
        DatabaseReference ref = database.getReference("Message").child(ChatActivity.roomid);
        SimpleDateFormat writeTimeFormat = new SimpleDateFormat("a hh:mm");
        //final String writeTime = writeTimeFormat.format(Calendar.getInstance().getTime());
        HashMap<String, Object> member = new HashMap<String, Object>();
        member.put("uID", currentUser.getUid()); //보낸사람 id
        member.put("userName", ChatActivity.uName); //보낸 사람 이름
        member.put("msg", url); //url
        member.put("timestamp", ServerValue.TIMESTAMP); //작성 시간
        member.put("msgType","1"); //메세지 타입
        ref.push().setValue(member); //DB에 저장
    }
    @Override
    public int getItemCount() {
        return tagDataArrayList.size();
    }

    //사용자 정보 불러오기
    public void UserInfo(){
        database.getReference("userInfo").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel um = dataSnapshot.getValue(UserModel.class);
                uName = um.getName();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}