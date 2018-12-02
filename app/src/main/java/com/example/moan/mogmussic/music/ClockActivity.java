package com.example.moan.mogmussic.music;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.music.MusicService;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.MusicUtil;
import com.example.moan.mogmussic.util.TimeFormatUtil;

import java.util.Calendar;
import java.util.List;

public class ClockActivity extends AppCompatActivity {
    private MusicService.MyBinder mMyBinder;
    private MyConn mMyConn = new MyConn();
    private Music playingMusic;
    private List<Music> playingMusicList;
    private MusicList musicList;
    private String TAG = "moanbigking";
    private TextView textView;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        Intent intent = getIntent();
        musicList = (MusicList) intent.getSerializableExtra("test");
        mMyConn = new MyConn();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Action.ACTION_SONG_FINISHED);
        intentFilter.addAction("timeUp");
        registerReceiver(mBroadcastReceiver, intentFilter);
        textView = findViewById(R.id.clock_clock);

        setTimer();
    }

    private void setTimer() {
        Calendar calendar = Calendar.getInstance();
        final int HourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        final int Minute = calendar.get(Calendar.MINUTE);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar another = Calendar.getInstance();
                another.set(Calendar.HOUR_OF_DAY, hourOfDay);
                another.set(Calendar.MINUTE, minute);
                Intent intent = new Intent();
                intent.setAction("timeUp");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ClockActivity.this,
                        888, intent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, another.getTimeInMillis(), pendingIntent);
                bindService();
                time = ((hourOfDay - HourOfDay) * 60 + minute - Minute) * 1000;
                new Thread(new Runnable() {
                    int temp = time;

                    @Override
                    public void run() {
                        try {
                            Log.d(TAG, "run: " + temp);
                            sendBroadcast(new Intent().setAction("fresh_view"));
                            Thread.sleep(1000 * 60);
                            temp -= 1000 * 60;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }, HourOfDay, Minute, true);
        timePickerDialog.show();
    }

    private void bindService() {
        playingMusicList = MusicUtil.fromString(musicList.getMusicJsonString());
        if (playingMusicList == null || playingMusicList.size() == 0) {
            Toast.makeText(ClockActivity.this, "没有音乐哦", Toast.LENGTH_SHORT).show();
        } else {
            playingMusic = playingMusicList.get(0);
            Intent musicIntent = new Intent(ClockActivity.this, MusicService.class);
            musicIntent.putExtra(Constant.MUSIC_SERVICE, playingMusic);
            bindService(musicIntent, mMyConn, BIND_AUTO_CREATE);
        }
    }

    private class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyBinder = (MusicService.MyBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || action.length() == 0) {
                return;
            }
            switch (action) {
                case Constant.Action.ACTION_SONG_FINISHED:
                    playingMusic = playingMusicList.get(playingMusicList.indexOf(playingMusic) + 1);
                    mMyBinder.changeSong(playingMusic);
                    break;
                case "timeUp":
                    finish();
                    break;
                case "fresh_view":
                    textView.setText(TimeFormatUtil.getPerfectTime(time));
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
