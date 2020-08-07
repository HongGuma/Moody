package com.example.Moody.Chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Moody.Activity.IntroActivity;
import com.example.Moody.Activity.MainActivity;
import com.example.Moody.Model.ChatModel;
import com.example.Moody.Model.ChatRoomModel;
import com.example.Moody.Model.FeedItems;

import com.example.Moody.Model.UserModel;
import com.example.Moody.R;
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
import java.util.HashMap;
import java.util.Map;

public class AutoChatActivity extends Activity {
    private static final String TAG = "AutoChatActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public RecyclerView tRecyclerView;//태그 리사이클러뷰
    public static RecyclerView chatRecyclerView;//채팅 내용 리사이클러뷰
    private PersonalAdapter pAdapter; //1:1 채팅 어뎁터
    private GroupAdapter gAdapter;//단체 채팅 어뎁터

    private ArrayList<ChatModel> chatModels = new ArrayList<ChatModel>();
    private ArrayList<ChatRoomModel> chatRoomModels = new ArrayList<>();

    private String receiver;
    private String uid;
    public static String uName; //사용자 이름
    public static String roomid; //채팅방 id
    private String chatRoomName; //상단에 채팅방 이름
    private String check;

    private String imageUrl;
    private int GET_GALLERY_IMAGE=200;
    public static String sText;

    //작성 시간
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    String date = dateFormat1.format(Calendar.getInstance().getTime());
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
        UsersInfo();//현재 사용자 정보 가져오는 함수

        roomid = getIntent().getStringExtra("roomid");//채팅방 id
        chatRoomName = getIntent().getStringExtra("name"); //채팅방 상단 이름 받아옴

        TextView recUser = (TextView) findViewById(R.id.chatRoom_users);//채팅방 상단
        final EditText sendText = (EditText) findViewById(R.id.chatRoom_text); //메세지 입력창
        recUser.setText(chatRoomName);//채팅방 상단 이름 설정

        chatRecyclerView = (RecyclerView) findViewById(R.id.chatRoom_recyclerView); //리사이클러뷰
        chatRecyclerView.setHasFixedSize(true); //리사이클러뷰 크기 고정

        check = getIntent().getStringExtra("check");
        ChatDisplay(check);
        if(check.equals("1")){//1:1 채팅
            receiver = getIntent().getStringExtra("receiver"); //상대방 id
            pAdapter = new PersonalAdapter(receiver,roomid,chatModels);
            chatRecyclerView.setAdapter(pAdapter);
            chatRecyclerView.scrollToPosition(pAdapter.getItemCount() - 1);

        }else{//단체 채팅
            gAdapter = new GroupAdapter(roomid,chatModels);
            chatRecyclerView.setAdapter(gAdapter);
            chatRecyclerView.scrollToPosition(gAdapter.getItemCount() - 1);

        }

        //버튼 선언
        Button backBtn = (Button) findViewById(R.id.chatRoom_backBtn);
        Button calendarBtn = (Button) findViewById(R.id.chatRoom_calendarBtn);
        Button sendBtn = (Button) findViewById(R.id.chatRoom_sendBtn);
        Button galleryBtn = (Button) findViewById(R.id.chatRoom_galleryBtn);
        final Button autoBtn = (Button) findViewById(R.id.chatRoom_autoBtn);

        sText=ChatActivity.sText;
        sendText.setText(sText);

        tRecyclerView=(RecyclerView)findViewById(R.id.tag_recyclerview);
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
                finish();

            }
        });

        //send버튼 클릭시
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sText = sendText.getText().toString();
                if (!(sText.equals(""))) {
                    DatabaseReference ref = database.getReference("Message").child(roomid);
                    Map<String,Object> read = new HashMap<>();
                    read.put(currentUser.getUid(),true);
                    HashMap<String, Object> member = new HashMap<String, Object>();
                    member.put("uID", currentUser.getUid()); //보낸사람 id
                    member.put("userName", uName); //보낸 사람 이름
                    member.put("msg", sText);
                    member.put("timestamp", ServerValue.TIMESTAMP);
                    member.put("msgType", "0");
                    member.put("readUsers",read);
                    sendText.setText(null);
                    ref.push().setValue(member);

                    chatRecyclerView.scrollToPosition(chatModels.size()-1);

                    HashMap<String, Object> chatroom = new HashMap<String, Object>();
                    chatroom.put("lastMsg",sText);//마지막 메시지
                    chatroom.put("lastTime",ServerValue.TIMESTAMP); //마지막 시간
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

    public void ChatDisplay(final String check){

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                String commentKey = dataSnapshot.getKey();

                //읽었는지
                Map<String,Object> read = new HashMap<>();
                read.put(currentUser.getUid(),true);
                database.getReference("Message").child(roomid).child(commentKey).child("readUsers").updateChildren(read);

                chatModels.add(chat);

                if (check.equals("1")) {
                    pAdapter.notifyDataSetChanged();
                } else {
                    gAdapter.notifyDataSetChanged();
                }
                chatRecyclerView.scrollToPosition(chatModels.size()-1);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        database.getReference("Message").child(roomid).addChildEventListener(childEventListener);

        database.getReference("ChatRoom").child(roomid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatRoomModel chatRoomModel = dataSnapshot.getValue(ChatRoomModel.class);
                chatRoomModels.add(chatRoomModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
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
                    database.getReference("ChatRoom").child(roomid).updateChildren(chatroom);
                }
            }
        });


    }


    public void UsersInfo() {
        //현재 로그인한 유저 정보 가져오기
        database.getReference("userInfo").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel um = dataSnapshot.getValue(UserModel.class);
                uName = um.getName();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }


    //==================================================================================================================//

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