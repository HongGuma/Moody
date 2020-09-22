package com.example.Moody.Background.Room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ChatRoomViewModel extends AndroidViewModel {
    private AppRepository mChatRoomRepository;
    private LiveData<List<ChatRoomEntity>> allChatRoomItem;


    public ChatRoomViewModel(@NonNull Application application) {
        super(application);
        mChatRoomRepository = new AppRepository(application);
        allChatRoomItem = mChatRoomRepository.getAllChatRoomItem();
    }

    public LiveData<List<ChatRoomEntity>> getAllList(){
        return allChatRoomItem;
    }


    public void insert(ChatRoomEntity chatRoom){
        mChatRoomRepository.insertChatRoomItem(chatRoom);
    }
}
