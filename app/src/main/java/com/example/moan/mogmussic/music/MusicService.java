package com.example.moan.mogmussic.music;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.Pool;

import java.io.IOException;

public class MusicService extends Service {
    private Music mMusic;
    private MediaPlayer mMediaPlayer;
    private boolean isMusicPlaying = false;
    private String TAG = "moanbigking";


    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mMusic = (Music) intent.getSerializableExtra(Constant.MUSIC_SERVICE);
        firstLoadSong();
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void firstLoadSong() {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mMusic.getUrl());
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(false);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("HandlerLeak")
    class MyBinder extends Binder implements MusicContract.IMusicControl {

        @Override
        public void start() {
            mMediaPlayer.start();
            isMusicPlaying = true;
            new Pool().getCachedThread().execute(new Runnable() {
                @Override
                public void run() {
                    while (isMusicPlaying) {
                        try {
                            Thread.sleep(100);
                            sendUpdateSeekBarBroadcast();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }


        @Override
        public void pause() {
            isMusicPlaying = false;
            mMediaPlayer.pause();
        }

        @Override
        public void seekToPosition(int position) {
            mMediaPlayer.seekTo(position * 1000);
        }

        @Override
        public void changeSong(Music music) {
            mMusic = music;
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(mMusic.getUrl());
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping(false);
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                sendBroadcast(new Intent().setAction(Constant.Action.ACTION_RESET_FINISHED));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public long getCurrentDuration() {
            return mMediaPlayer.getCurrentPosition();
        }


        @Override
        public void setLooping(Boolean isLooping) {
            mMediaPlayer.setLooping(isLooping);
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

    private void sendUpdateSeekBarBroadcast() {
        sendBroadcast(new Intent().setAction(Constant.Action.ACTION_UPDATE_TIME));
    }

    private void sendMusicFinishedBroadcast() {
        sendBroadcast(new Intent().setAction(Constant.Action.ACTION_SONG_FINISHED));
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            sendMusicFinishedBroadcast();
        }
    };

}

