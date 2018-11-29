package com.example.moan.mogmussic.util;

//183002->183  00:00  -> 03:03
public class TimeFormatUtil {
    public static String getPerfectTime(long time) {
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
            right = c +"";
        } else {
            right = "0" + c;
        }
        return left + ":" + right;
    }
}
