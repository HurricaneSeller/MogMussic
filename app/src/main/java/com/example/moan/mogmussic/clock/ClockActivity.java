package com.example.moan.mogmussic.clock;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.music.MusicService;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.MusicUtil;
import com.example.moan.mogmussic.util.TimeFormatUtil;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ClockActivity extends AppCompatActivity implements ClockContract.View, View.OnClickListener {
    @BindView(R.id.hour_wheel_view)
    WheelView mHourWheelView;
    @BindView(R.id.minute_wheel_view)
    WheelView mMinuteWheelView;
    @BindView(R.id.hour_increase)
    ImageButton btnHourI;
    @BindView(R.id.hour_decrease)
    ImageButton btnHourD;
    @BindView(R.id.minute_decrease)
    ImageButton btnMinuteD;
    @BindView(R.id.minute_increase)
    ImageButton btnMinuteI;
    @BindView(R.id.set_clock)
    CircleImageView btnSetClock;
    @BindView(R.id.cancel_clock)
    CircleImageView btnCancelClock;
    @BindView(R.id.counting_view)
    TextView mCountingView;
    @BindView(R.id.choose_time_view)
    LinearLayout mLinearLayout;

    private MusicService.MyBinder mMyBinder;
    private MyConn mMyConn = new MyConn();
    private Music playingMusic;
    private List<Music> playingMusicList;
    private MusicList musicList;
    private String TAG = "moanbigking";
    private ClockContract.Presenter mPresenter;
    private int hour = 12;
    private int minute = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        ButterKnife.bind(this);
        hideActionBar();
        mPresenter = new ClockPresenter(this);
        Intent intent = getIntent();
        musicList = (MusicList) intent.getSerializableExtra("test");
        mMyConn = new MyConn();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Action.ACTION_SONG_FINISHED);
        registerReceiver(mBroadcastReceiver, intentFilter);
        mPresenter.init();
        setOnclickListener();
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

    @Override
    public void initHourWheelView(final List<String> hours) {
        mHourWheelView.setData(hours);
        mHourWheelView.setOnclickListener(new WheelView.IOnSelectListener() {
            @Override
            public void onSelect(String text) {
                hour = Integer.valueOf(text);
            }
        });
    }

    @Override
    public void initMinuteWheelView(List<String> minutes) {
        mMinuteWheelView.setData(minutes);
        mMinuteWheelView.setOnclickListener(new WheelView.IOnSelectListener() {
            @Override
            public void onSelect(String text) {
                minute = Integer.valueOf(text);
            }
        });
    }

    @Override
    public void initCountingView() {
        mCountingView.setText("00:00");
    }

    @Override
    public void setTime(String time) {
        mCountingView.setText(time);
    }

    @Override
    public void finishClockActivity() {
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hour_decrease:
                mHourWheelView.increaseRowNum();
                break;
            case R.id.hour_increase:
                mHourWheelView.decreaseRowNum();
                break;
            case R.id.minute_decrease:
                mMinuteWheelView.increaseRowNum();
                break;
            case R.id.minute_increase:
                mMinuteWheelView.decreaseRowNum();
                break;
            case R.id.set_clock:
                onSetClock();
                break;
            case R.id.cancel_clock:
                onCancelClock();
                break;
        }
    }

    private void onCancelClock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setTitle(Constant.Words.QUIT_CLOCK_MODE)
                .setMessage(Constant.Words.WARNING)
                .setPositiveButton(Constant.Words.PERMITTING_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.deleteTimer();
                        finishClockActivity();
                    }
                })
                .setNegativeButton(Constant.Words.PERMITTING_DENY, null)
                .create()
                .show();
    }

    private void onSetClock() {
        btnCancelClock.setVisibility(View.VISIBLE);
        btnSetClock.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);
        int lastingMinutes = hour * 60 + minute;
        mPresenter.setTimer(lastingMinutes);
        setTime(TimeFormatUtil.getClockTime(lastingMinutes));
        bindService();
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
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        unbindService(mMyConn);
    }


    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorOrange400));
    }

    private void setOnclickListener() {
        btnHourD.setOnClickListener(this);
        btnHourI.setOnClickListener(this);
        btnMinuteD.setOnClickListener(this);
        btnMinuteI.setOnClickListener(this);
        btnSetClock.setOnClickListener(this);
        btnCancelClock.setOnClickListener(this);
    }
}
