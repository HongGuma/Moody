package com.example.Moody.Background.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "liked")
public class LikedEntity {
    @NonNull
    @PrimaryKey
    String FID;

    @NonNull
    public String getFID() {
        return FID;
    }

    public void setFID(@NonNull String FID) {
        this.FID = FID;
    }

}
