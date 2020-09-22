package com.example.Moody.Background.Room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class LikedViewModel extends AndroidViewModel {
    private AppRepository mLikedRepository;
    private LiveData<List<LikedEntity>> allLikeItem;


    public LikedViewModel(@NonNull Application application) {
        super(application);
        mLikedRepository = new AppRepository(application);
        allLikeItem = mLikedRepository.getAllLikedItem();
    }

    public LiveData<List<LikedEntity>> getAllList(){
        return allLikeItem;
    }


    public void insert(LikedEntity liked){
        mLikedRepository.insertLikedItem(liked);
    }
}
