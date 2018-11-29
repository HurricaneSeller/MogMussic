package com.example.moan.mogmussic.data.music;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Music.class}, version = 1)
public abstract class MusicDatabase extends RoomDatabase {
    private static MusicDatabase INSTANCE;

    public abstract MusicDao musicDao();

    private static final Object o = new Object();

    public static MusicDatabase getInstance(Context context) {
        synchronized (o) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MusicDatabase.class, "music.db").build();
            }
        }
        return INSTANCE;
    }
}
