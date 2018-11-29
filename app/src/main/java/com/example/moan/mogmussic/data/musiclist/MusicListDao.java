package com.example.moan.mogmussic.data.musiclist;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MusicListDao {
    @Insert
    void insert(MusicList musicList);

    @Delete
    void delete(MusicList musicList);

    @Query("SELECT * FROM musiclist WHERE list_name =(:name)")
    MusicList findByName(String name);

    @Update
    void updateMusicList(MusicList musicList);

    @Query("SELECT * FROM musiclist")
    List<MusicList> getAll();
}
