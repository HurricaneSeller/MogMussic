package com.example.moan.mogmussic.data;

import com.example.moan.mogmussic.data.music.Music;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class MusicConvert {
    @TypeConverter
    public static String fromMusicList(ArrayList<Music> musicArrayList) {
        Gson gson = new Gson();
        return gson.toJson(musicArrayList);
    }
    @TypeConverter
    public static ArrayList<Music> fromString(String value) {
        Type listTYpe = new TypeToken<ArrayList<Music>>(){}.getType();
        return new Gson().fromJson(value, listTYpe);
    }
}
