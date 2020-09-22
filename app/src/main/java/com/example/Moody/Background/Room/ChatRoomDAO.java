package com.example.Moody.Background.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ChatRoomDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)//중복 id일 경우 덮어쓴다.
    void insert(ChatRoomEntity chatRoom);

    @Delete
    void delete(ChatRoomEntity chatRoom);

    @Update
    void update(ChatRoomEntity chatRoom);

    @Query("SELECT * FROM CHATROOM")
    LiveData<List<ChatRoomEntity>> getAll();

    @Query("SELECT * FROM CHATROOM")
    List<ChatRoomEntity> init();

    @Query("DELETE FROM CHATROOM")
    void deleteAll();
}
