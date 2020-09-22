package com.example.Moody.Background.Room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private AppRepository mUserRepository;
    private LiveData<List<UserEntity>> allUserItem;


    public UserViewModel(@NonNull Application application) {
        super(application);
        mUserRepository = new AppRepository(application);
        allUserItem = mUserRepository.getAllUserItem();
    }

    public LiveData<List<UserEntity>> getAllList(){
        return allUserItem;
    }


    public void insert(UserEntity users){
        mUserRepository.insertUserItem(users);
    }
}
