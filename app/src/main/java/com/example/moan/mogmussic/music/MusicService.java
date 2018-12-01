package com.example.moan.mogmussic.music;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.show.ShowActivity;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.Pool;

import java.io.IOException;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MusicService extends Service {
    private Music mMusic;
    private MediaPlayer mMediaPlayer;
    private boolean isMusicPlaying = false;
    private String TAG = "moanbigking";
    private RemoteViews notificationView;
    private static final int toPause = R.drawable.ic_play_circle_outline_orange_400_24dp;
    private static final int toPlay = R.drawable.ic_pause_circle_outline_orange_400_24dp;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mMusic = (Music) intent.getSerializableExtra(Constant.MUSIC_SERVICE);
        firstLoadSong();
        setForegroundService();
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
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
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
            sendUpdateSeekBarBroadcast();
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
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
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

        @Override
        public void updateNotificationControlIcon() {
            if(isMusicPlaying) {
                notificationView.setImageViewResource(R.id.ntf_pause, toPause);
                Log.d(TAG, "updateNotificationControlIcon: 1");
            } else {
                notificationView.setImageViewResource(R.id.ntf_pause, toPlay);
                Log.d(TAG, "updateNotificationControlIcon: 2");
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

    private void sendUpdateSeekBarBroadcast() {
        isMusicPlaying = true;
        new Pool().getCachedThread().execute(new Runnable() {
            @Override
            public void run() {
                while (isMusicPlaying) {
                    try {
                        Thread.sleep(100);
                        sendBroadcast(new Intent().setAction(Constant.Action.ACTION_UPDATE_TIME));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            new Pool().getCachedThread().execute(new Runnable() {
                @Override
                public void run() {
                    sendBroadcast(new Intent().setAction(Constant.Action.ACTION_SONG_FINISHED));
                }
            });
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            Log.d(TAG, "onPrepared: ");
            Log.d(TAG, "onPrepared: " + mp.isPlaying());
            new Pool().getCachedThread().execute(new Runnable() {
                @Override
                public void run() {
                    sendSetUpBottomControlViewBroadcast();
                    sendUpdateSeekBarBroadcast();
                }
            });
        }
    };


    // TODO: 11/30/18 refresh the view !
    private void setForegroundService() {
        int importance = NotificationManager.IMPORTANCE_MIN;
        NotificationChannel notificationChannel =
                new NotificationChannel(Constant.Notification.CHANNEL_ID,
                        Constant.Notification.CHANNEL_NAME, importance);
        notificationChannel.enableVibration(false);
        notificationChannel.setSound(null, null);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                Constant.Notification.CHANNEL_ID);
        notificationView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        notificationView.setTextViewText(R.id.ntf_title, mMusic.getTitle());
        String temp = mMusic.getArtist() + "-" + mMusic.getAlbum();
        notificationView.setTextViewText(R.id.ntf_artist, temp);
        notificationView.setTextColor(R.id.ntf_title, ContextCompat.getColor(this,
                R.color.colorDarkDarkGrey));
        notificationView.setTextColor(R.id.ntf_artist, ContextCompat.getColor(this,
                R.color.colorDarkDarkGrey));

        Intent startActivityIntent = new Intent(MusicService.this, MusicActivity.class);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(this,
                Constant.Notification.REQUEST_ENTER_ACTIVITY,
                startActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intentPause = new Intent();
        intentPause.setAction(Constant.Action.ACTION_CONTROL_NOTIFICATION);
        intentPause.putExtra(Constant.Notification.KEY, Constant.Notification.KEY_CONTROL);
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(this,
                Constant.Notification.REQUEST_CONTROL, intentPause, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.ntf_pause, pendingIntentPause);

        Intent intentNext = new Intent();
        intentNext.setAction(Constant.Action.ACTION_CONTROL_NOTIFICATION);
        intentNext.putExtra(Constant.Notification.KEY, Constant.Notification.KEY_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this,
                Constant.Notification.REQUEST_NEXT, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.ntf_next, pendingIntentNext);

        Intent intentPrevious = new Intent();
        intentPrevious.setAction(Constant.Action.ACTION_CONTROL_NOTIFICATION);
        intentPrevious.putExtra(Constant.Notification.KEY, Constant.Notification.KEY_PREVIOUS);
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(this,
                Constant.Notification.REQUEST_PREVIOUS, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.ntf_prev, pendingIntentPrevious);


        builder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(startActivityPendingIntent)
                .setSound(null)
                .setVibrate(null)
                .setOnlyAlertOnce(true)
                .setContent(notificationView);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        startForeground(Constant.Notification.ID, builder.build());
    }
    private void sendSetUpBottomControlViewBroadcast() {
        new Pool().getCachedThread().execute(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(Constant.Action.ACTION_SET_VIEW);
                intent.putExtra("music", mMusic);
                sendBroadcast(intent);
            }
        });
    }
}

