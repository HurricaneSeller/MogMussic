package com.example.moan.mogmussic.online;

import android.content.Context;
import android.content.Intent;

import com.example.moan.mogmussic.gson.OnlineSong;

import java.util.List;

public interface OnlineContract {
    interface OnlineActivityView{
        void showEmptyIfo();
        void setAdapter(List<OnlineSong> onlineSongs, OAPresenter.ImageCache imageCache);
        void showProgressingBar();
        void hideProgressingBar();
    }
    interface OnlineActivityContract{
        void sendOkHttpRequest(String input);
        void downloadSong(OnlineSong onlineSong, Context context);
        void startMusicActivity(final Intent musicIntent, final Context context);
    }
}
