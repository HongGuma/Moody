package com.example.phometalk.Chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phometalk.Activity.IntroActivity;
import com.example.phometalk.Activity.MainActivity;
import com.example.phometalk.Model.ChatModel;
import com.example.phometalk.Model.FeedItems;

import com.example.phometalk.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class AutoChatActivity extends Activity {
    private static final String TAG = "AutoChatActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    //private ListView cListView
    public static RecyclerView cRecyclerView;
    public static RecyclerView tRecyclerView;
    private ChatAdapter cAdapter;

    private ArrayList<ChatModel> chatModels = new ArrayList<ChatModel>();

    public static ArrayList<String> uInfo = new ArrayList<String>();
    private String receiver;
    private String uid;
    private String roomid;
    private String receiverName;
    private String receiverProfile;
    private String imageUrl;
    private int GET_GALLERY_IMAGE=200;
    public static String sText;

    //작성 시간
    SimpleDateFormat writeTimeFormat = new SimpleDateFormat("a hh:mm");
    //String writeTime = writeTimeFormat.format(Calendar.getInstance().getTime());
    //사진 파일 이름
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
    String datetime = dateFormat.format(Calendar.getInstance().getTime());


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_chatting);
        mAuth = FirebaseAuth.getInstance();//현재 로그인 정보
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();//현재 사용자 id
        receiver = getIntent().getStringExtra("receiver"); //상대방 id
        receiverName = getIntent().getStringExtra("recName"); //상대방 이름
        receiverProfile = getIntent().getStringExtra("recProfile");//상대방 프로필
        roomid=getIntent().getStringExtra("roomid");
        mAuth = FirebaseAuth.getInstance();

        UsersInfo(); //유저 정보 가져오기


        TextView recUser = (TextView) findViewById(R.id.chatRoom_users);//채팅방 상단에 유저 이름
        recUser.setText(receiverName);

        final EditText sendText = (EditText) findViewById(R.id.chatRoom_text);  //메세지 입력창

        cRecyclerView = (RecyclerView) findViewById(R.id.chatRoom_recyclerView); //리사이클러뷰
        cRecyclerView.setHasFixedSize(true); //리사이클러뷰 크기 고정

        cAdapter = new ChatAdapter(chatModels);//리사이클러뷰 어뎁터
        cRecyclerView.setAdapter(cAdapter);

        //cRecyclerView.scrollToPosition(cAdapter.getItemCount());

        //버튼 선언
        Button backBtn = (Button) findViewById(R.id.chatRoom_backBtn);
        Button calendarBtn = (Button) findViewById(R.id.chatRoom_calendarBtn);
        Button sendBtn = (Button) findViewById(R.id.chatRoom_sendBtn);
        Button galleryBtn = (Button) findViewById(R.id.chatRoom_galleryBtn);
        final Button autoBtn = (Button) findViewById(R.id.chatRoom_autoBtn);

        sText=ChatActivity.sText;
        sendText.setText(sText);

        tRecyclerView=(RecyclerView)findViewById(R.id.tag_recyclerview);
        //cRecyclerView.scrollToPosition(cAdapter.getItemCount()-1);
        String emotion=ChatActivity.emotion;
        ArrayList<FeedItems> tagItems = new ArrayList<>();
        for (int i = 0; i < IntroActivity.publicItems.size(); i++) {
            FeedItems entity = new FeedItems();
            if (emotion.equals(IntroActivity.publicItems.get(i).getType())) {
                entity.setUrl(IntroActivity.publicItems.get(i).getUrl());
                entity.setTag(IntroActivity.publicItems.get(i).getType());
                tagItems.add(entity);
            }
        }
        tagItems.addAll(IntroActivity.dbHelper.getTagItems(emotion));
        TabAdapter tAdapter = new TabAdapter(AutoChatActivity.this, tagItems);
        tRecyclerView.setHasFixedSize(true);
        tRecyclerView.setLayoutManager(new LinearLayoutManager(AutoChatActivity.this));
        tRecyclerView.setAdapter(tAdapter);

        //뒤로가기
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AutoChatActivity.this, MainActivity.class);
                intent.putExtra("fragment","chat");
                startActivity(intent);

            }
        });

        //send버튼 클릭시
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sText = sendText.getText().toString();
                if (!(sText.equals(""))) {
                    DatabaseReference ref = database.getReference("Message").child(roomid);
                    HashMap<String, Object> member = new HashMap<String, Object>();
                    member.put("uID", currentUser.getUid()); //보낸사람 id
                    member.put("userName", uInfo.get(2)); //보낸 사람 이름
                    member.put("msg", sText);
                    member.put("timestamp", ServerValue.TIMESTAMP);
                    member.put("msgType", "0");
                    cRecyclerView.scrollToPosition(cAdapter.getItemCount() - 1);
                    sendText.setText(null);
                    ref.push().setValue(member);

                    HashMap<String, Object> chatroom = new HashMap<String, Object>();
                    chatroom.put("lastMsg",sText);
                    chatroom.put("lastTime", ServerValue.TIMESTAMP);
                    database.getReference("ChatRoom").child(roomid).updateChildren(chatroom);
                }
                finish();
            }
        });

        //갤러리 버튼
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        //이미지추천 버튼
        autoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData(); //이미지 경로 원본
            String imagePath = "Chat/"+roomid+"/"+datetime; //사진파일 경로 및 이름
            UploadFiles(selectedImageUri,imagePath); //사진 업로드

        }

    }

    //파이어베이스에 사진 업로드
    public void UploadFiles(Uri uri, final String path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();

        final StorageReference riversRef = storageRef.child(path);
        UploadTask uploadTask = riversRef.putFile(uri);
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
                    DatabaseReference ref = database.getReference("Message").child(roomid);
                    HashMap<String, Object> member = new HashMap<String, Object>();
                    member.put("uID", currentUser.getUid()); //보낸사람 id
                    member.put("userName", uInfo.get(2)); //보낸 사람 이름
                    member.put("msg", imageUrl); //url
                    member.put("timestamp", ServerValue.TIMESTAMP); //작성 시간
                    member.put("msgType","1"); //메세지 타입
                    ref.push().setValue(member); //DB에 저장

                    HashMap<String, Object> chatroom = new HashMap<String, Object>();
                    chatroom.put("lastMsg","사진");
                    chatroom.put("lastTime",ServerValue.TIMESTAMP);
                    database.getReference("ChatRoom").child(roomid).updateChildren(chatroom);
                }
            }
        });


    }


    public void UsersInfo() {
        //현재 로그인한 유저 정보 가져오기
        database.getReference().child("userInfo").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    uInfo.add((String)dataSnapshot1.getValue());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }


    //==================================================================================================================//

    public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
        private static final String TAG = "ChatAdapter";

        private ArrayList<ChatModel> chatModel;

        //생성자에서 데이터 리스트 객체를 전달받음
        public ChatAdapter(ArrayList<ChatModel> list){
            chatModel=list;

            //하위 이벤트 수신
            ChildEventListener childEventListener = new ChildEventListener() {
                //새로운 항목이 추가될때마다 다시 트리거 된다.


                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    //Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    ChatModel c = dataSnapshot.getValue(ChatModel.class);
                    String commentKey = dataSnapshot.getKey();
                    String uName = c.getUserName();
                    String meg = c.getMsg();
                    Object timestamp = c.getTimestamp();
                    String type = c.getMsgType();
                    chatModels.add(c);
                    cAdapter.notifyDataSetChanged();

                    cRecyclerView.scrollToPosition(cAdapter.getItemCount()-1);
                    String a = Integer.toString(cAdapter.getItemCount());
                }

                //하위 노드가 수정될때마다 다시 트리거
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {  }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {  }

                //항목 순서가 변경될때마다 다시 트리거
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {  }

                @Override
                public void onCancelled(DatabaseError databaseError) {  }
            };
            DatabaseReference myRef = database.getReference("Message").child(roomid);
            myRef.addChildEventListener(childEventListener);

        }

        //아이템 뷰를 저장하는 뷰홀더 클래스
        public class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView userImage;
            public TextView userName;
            public TextView textView;
            public TextView timestamp;
            public ImageView sendPhoto;

            ViewHolder(View view){
                super(view);
                userImage = (ImageView)view.findViewById(R.id.user_image);
                userName = (TextView)view.findViewById(R.id.user_name);
                textView = (TextView)view.findViewById(R.id.tvChat);
                timestamp = (TextView)view.findViewById(R.id.timestamp);
                sendPhoto = (ImageView)view.findViewById(R.id.ivChat);

            }

        }

        //상대방이 보낸 메세지인지 구분
        @Override
        public int getItemViewType(int position) {
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
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
        public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

            ChatAdapter.ViewHolder vh = new ChatAdapter.ViewHolder(view);

            cRecyclerView.scrollToPosition(cAdapter.getItemCount()-1);
            String a = Integer.toString(cAdapter.getItemCount());
            Log.d(TAG, "onClick"+a);


            return vh;

        }


        //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
            long unixTime = (long) chatModel.get(position).getTimestamp();
            Date date = new Date(unixTime);
            writeTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = writeTimeFormat.format(date);
            holder.timestamp.setText(time);


            if(chatModel.get(position).getMsgType().equals("0")){
                holder.textView.setText(chatModel.get(position).getMsg());
            }else{
                Glide.with(holder.sendPhoto.getContext()).load(chatModel.get(position).getMsg()).into(holder.sendPhoto);
            }


            //내 uid가 아니면 다른 뷰가 오기 때문에
            if(!chatModel.get(position).getUID().equals(currentUser.getUid())){
                holder.userName.setText(chatModel.get(position).getUserName());
            }


        }

        //개수만큼 아이템 생성
        @Override
        public int getItemCount() {
            return chatModel.size();
        }

    }

    //키보드 내리기
    public boolean onTouchEvent(MotionEvent event) {
        EditText email = (EditText)findViewById(R.id.chatRoom_text);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
        return true;
    }

    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(AutoChatActivity.this, modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // 모델을 읽어오는 함수
    // MappedByteBuffer 바이트 버퍼를 Interpreter 객체에 전달하면 모델 해석을 할 수 있다.
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private HashMap<Integer, String> Word() {

        HashMap<Integer, String> word_set = new HashMap<Integer, String>();

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.wordset);
            InputStreamReader reader = new InputStreamReader(inputStream);
            // 입력 버퍼 생성
            BufferedReader bufReader = new BufferedReader(reader);
            String line = "";

            int i=0;
            while ((line = bufReader.readLine()) != null) {
                System.out.println(line);

                String[] word = line.split(":");
                word_set.put(Integer.parseInt(word[1]), word[0]);
                i++;
            }
            bufReader.close();
        } catch (IOException e) {
            System.out.println(e);
        }

        return word_set;
    }

}