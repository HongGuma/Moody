package com.example.Moody.Background.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppRepository {
    private UserDAO mUserDAO;
    private ChatRoomDAO mChatRoomDAO;
    private LikedDAO mLikedDAO;
    private LiveData<List<UserEntity>> allUserItem;
    private LiveData<List<ChatRoomEntity>> allChatRoomItem;
    private LiveData<List<LikedEntity>> allLikedItem;

    public AppRepository(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        mUserDAO = db.userDAO();
        mChatRoomDAO = db.chatRoomDAO();
        allUserItem = mUserDAO.getAll();
        allChatRoomItem = mChatRoomDAO.getAll();
    }

    //user의 모든 데이터 출력
    public LiveData<List<UserEntity>> getAllUserItem(){
        return allUserItem;
    }

    //user삽입
    public void insertUserItem(final UserEntity users){
        Runnable addRun = new Runnable() {
            @Override
            public void run() {
                mUserDAO.insert(users);
            }
        };
        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(addRun);

    }

    //chatRoom의 모든 데이터 출력
    public LiveData<List<ChatRoomEntity>> getAllChatRoomItem(){
        return allChatRoomItem;
    }

    //chatroom삽입
    public void insertChatRoomItem(final ChatRoomEntity chatRoom){
        Runnable addRun = new Runnable() {
            @Override
            public void run() {
                mChatRoomDAO.insert(chatRoom);
            }
        };
        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(addRun);

    }

    //liked의 모든 데이터 출력
    public LiveData<List<LikedEntity>> getAllLikedItem() {return allLikedItem;}

    //liked삽입
    public void insertLikedItem(final LikedEntity liked){
        Runnable addRun = new Runnable() {
            @Override
            public void run() {
                mLikedDAO.insert(liked);
            }
        };
        Executor diskIO = Executors.newSingleThreadExecutor();
        diskIO.execute(addRun);
    }
}
