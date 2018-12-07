package com.example.moan.mogmussic.clock;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.moan.mogmussic.music.MusicService;
import com.example.moan.mogmussic.util.TimeFormatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClockPresenter implements ClockContract.Presenter {
    private ClockContract.View mView;
    private int mLastMinute;
    private Timer mTimer;
    private String TAG = "moanbigking";


    ClockPresenter(ClockContract.View view) {
        mView = view;
    }

    @Override
    public void init() {
        mView.initHourWheelView(getListString(0, 24));
        mView.initMinuteWheelView(getListString(0, 60));
        mView.initCountingView();
    }

    @Override
    public void setTimer(int lastingMinutes) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mLastMinute = lastingMinutes;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 1000 * 60);
    }

    @Override
    public void deleteTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

    }

    private List<String> getListString(int start, int end) {
        List<String> result = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            result.add(i + "");
        }
        return result;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mLastMinute--;
                    if (mLastMinute > 0) {
                        mView.setTime(TimeFormatUtil.getClockTime(mLastMinute));
                    } else {
                        mTimer.cancel();
                        mTimer = null;
                        mView.finishClockActivity();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
}
