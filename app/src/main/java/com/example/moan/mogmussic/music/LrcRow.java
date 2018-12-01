package com.example.moan.mogmussic.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcRow implements Comparable<LrcRow> {
    public String strTime;
    public long time;
    public String content;

    public LrcRow(String strTime, long time, String content) {
        this.strTime = strTime;
        this.time = time;
        this.content = content;
    }

    @Override
    public int compareTo(LrcRow o) {
        return (int)(this.time - o.time);
    }

    //[02:34.14][01:07.00x]当你我不小心又想起她
    public static List<LrcRow> createRows(String standardLrcLine) {
        if (standardLrcLine == null||standardLrcLine.indexOf("[") != 0 || standardLrcLine.indexOf("]") != 9) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\[\\d{2}[\\s\\S]*");
        Matcher matcher = pattern.matcher(standardLrcLine);
        if (!matcher.find()) {
            return null;
        }
        int lastIndexOfRightBracket = standardLrcLine.lastIndexOf("]");
        String content = standardLrcLine.substring(lastIndexOfRightBracket + 1,
                standardLrcLine.length());
        String times = standardLrcLine.substring(0, lastIndexOfRightBracket + 1)
                .replace("[", "-").replace("]", "-");
        String[] arrTimes = times.split("-");
        List<LrcRow> listTimes = new ArrayList<>();
        for (String s : arrTimes) {
            if (s.trim().length() == 0) {
                continue;
            }
            LrcRow lrcRow = new LrcRow(s, timeConvert(s), content);
            listTimes.add(lrcRow);
        }
        return listTimes;


    }

    private static long timeConvert(String timeString) {
        timeString = timeString.replace(".", ":");
        String[] times = timeString.split(":");
        return Integer.valueOf(times[0]) * 1000 * 60
                + Integer.valueOf(times[1]) * 1000
                + Integer.valueOf(times[2]);
    }

    public static List<LrcRow> getLrcRows(String rawLrc) throws IOException {
        if (rawLrc == null || rawLrc.length() == 0) {
            return null;
        }
        StringReader reader = new StringReader(rawLrc);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        List<LrcRow> lrcRows = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            if (line.length() > 0) {
                List<LrcRow> temp = LrcRow.createRows(line);
                if (temp != null && temp.size() > 0) {
                    lrcRows.addAll(temp);
                }
            }
        }
        if (lrcRows.size() > 0) {
            Collections.sort(lrcRows);
        }
        bufferedReader.close();
        return lrcRows;
    }

}
