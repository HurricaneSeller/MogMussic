package com.example.moan.mogmussic.util;

import android.util.Log;

//183002->183  00:00  -> 03:03
public class TimeFormatUtil {
    private static String TAG = "moanbigking";

    public static String getLyricsTime(long time) {
        int tempTime = (int) time / 1000;
        String left, right;
        int a, b, c;
        if ((a = tempTime / 60) > 0) {
            if ((b = a / 10) > 0) {
                left = b + "";
            } else {
                left = "0" + a;
            }
        } else {
            left = "00";
        }
        if ((c = tempTime % 60) >= 10) {
            right = c + "";
        } else {
            right = "0" + c;
        }
        return left + ":" + right;
    }

    public static String getClockTime(int minute) {
        Log.d(TAG, "getClockTime: ");
        if (minute < 60) {
            if (minute < 10) {
                return "00:0" + minute;
            } else {
                return "00:" + minute;
            }
        } else {
            int temp = minute / 60;
            if (temp < 10) {
                return "0" + temp + ":" + minute % 60;
            } else {
                return temp + ":" + minute % 60;
            }
        }
    }
}
