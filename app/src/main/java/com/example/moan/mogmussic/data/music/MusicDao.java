package com.example.moan.mogmussic.data.music;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface MusicDao {
    @Query("SELECT * FROM music")
    List<Music> getAll();

    @Query("SELECT * FROM music WHERE artist LIKE :artistName")
    List<Music> getByArtist(String artistName);

    @Query("SELECT * FROM music WHERE album_title LIKE :albumName")
    List<Music> getByAlbum(String albumName);

    @Query("SELECT * FROM music WHERE title LIKE :title")
    Music getByTitle(String title);

    @Query("SELECT * FROM music WHERE id LIKE :id")
    Music getById(int id);

    @Query("SELECT * FROM music WHERE id IN (:Ids)")
    List<Music> getAllById(List<Integer> Ids);

    @Insert
    void insertAll(List<Music> musics);

    @Insert
    void insert(Music music);

    @Delete
    void deleteAll(List<Music> musics);

    @Delete
    void delete(Music music);
}
