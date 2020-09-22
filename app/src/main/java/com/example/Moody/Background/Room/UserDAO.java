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
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)//중복 id일 경우 덮어쓴다.
    void insert(UserEntity users);

    @Delete
    void delete(UserEntity users);

    @Update
    void update(UserEntity users);

    @Query("SELECT * FROM FriendInfo")
    LiveData<List<UserEntity>> getAll();

    @Query("SELECT * FROM FriendInfo")
    List<UserEntity> init();

    @Query("DELETE FROM FriendInfo")
    void deleteAll();

    @Query("SELECT num FROM FriendInfo WHERE num = 0")
    int debugging();
}
