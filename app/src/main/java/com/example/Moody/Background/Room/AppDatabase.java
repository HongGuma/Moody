package com.example.Moody.Background.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserEntity.class,ChatRoomEntity.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract ChatRoomDAO chatRoomDAO();
    public static AppDatabase mAppDatabase;

    public static AppDatabase getInstance(Context context){
        if(mAppDatabase==null){
            mAppDatabase = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"room_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mAppDatabase;
    }

    public static void RemoveDatabase(){
        mAppDatabase = null;
    }
}
