package com.example.musicplayer.ui.songs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SongsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SongsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is songs fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}