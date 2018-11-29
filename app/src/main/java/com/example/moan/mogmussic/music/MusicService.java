package com.example.moan.mogmussic.music;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.util.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    private Music mMusic;
    private MediaPlayer mMediaPlayer;
    private List<Music> mMusicList = new ArrayList<>();
    //    private int index;
    private Messenger serviceMessenger = new Messenger(new ServiceHandler());
    private Messenger clientMessenger = null;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mMusic = (Music) intent.getSerializableExtra(Constant.MUSIC_SERVICE);
        firstLoadSong();
        return serviceMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void firstLoadSong() {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mMusic.getUrl());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("HandlerLeak")
    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.Message.MEDIA_PAUSE:
                    mMediaPlayer.pause();
                    break;
                case Constant.Message.MEDIA_START:
                    mMediaPlayer.start();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
            clientMessenger = msg.replyTo;
            Message message = Message.obtain();
            message.what = 614;
            if (clientMessenger != null) {
                try {
                    clientMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void seekToPosition(int position) {
        mMediaPlayer.seekTo(position);
    }

    private void changeSong(Music music) {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(music.getUrl());
            mMediaPlayer.prepareAsync();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getCurrentDuration() {
        return mMediaPlayer.getDuration();
    }

}

