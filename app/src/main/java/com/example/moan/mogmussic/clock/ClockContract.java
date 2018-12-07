package com.example.moan.mogmussic.clock;

import java.util.List;

public interface ClockContract {
    interface View{
        void initHourWheelView(List<String> hours);
        void initMinuteWheelView(List<String> minutes);
        void initCountingView();
        void setTime(String time);
        void finishClockActivity();
    }
    interface Presenter{
        void init();
        void setTimer(int lastingMinutes);
        void deleteTimer();
    }
}
