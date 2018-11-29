package com.example.moan.mogmussic.util;

public class TimeformatUtil {
    public static String getPerfectTime(long time) {
        int tempTime = (int) time / 1000;
        int a = 0;
        if (tempTime >= 60) {
            a = tempTime / 60;
        }
        return (a >= 10) ? a + ":" + tempTime % 60 : "0" + a + ":" + tempTime % 60;
    }
}
