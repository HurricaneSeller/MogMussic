package com.example.moan.mogmussic.data.musiclist;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MusicList.class}, version = 1)
public abstract class MusicListDatabase extends RoomDatabase {
    private static MusicListDatabase INSTANCE;

    public abstract MusicListDao musicListDao();

    private static final Object o = new Object();

    public static MusicListDatabase getInstance(Context context) {
        synchronized (o) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MusicListDatabase.class, "music_list.db").build();
            }
        }
        return INSTANCE;
    }
}
