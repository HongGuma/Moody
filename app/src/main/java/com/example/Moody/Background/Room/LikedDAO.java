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
public interface LikedDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)//중복 id일 경우 덮어쓴다.
    void insert(LikedEntity liked);

    @Delete
    void delete(LikedEntity liked);

    @Update
    void update(LikedEntity liked);

    @Query("SELECT * FROM liked")
    LiveData<List<String>> getAll();

    @Query("DELETE FROM liked")
    void deleteAll();
}
