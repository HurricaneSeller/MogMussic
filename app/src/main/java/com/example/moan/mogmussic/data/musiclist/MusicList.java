package com.example.moan.mogmussic.data.musiclist;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MusicList implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int lid;

    @ColumnInfo(name = "list_name")
    private String name;

    @ColumnInfo(name = "list_password")
    private String password;

    @ColumnInfo(name = "music_json_string")
    private String musicJsonString;

    @ColumnInfo(name = "has_password")
    private boolean hasPassword = false;

    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public String getMusicJsonString() {
        return musicJsonString;
    }

    public void setMusicJsonString(String musicJsonString) {
        this.musicJsonString = musicJsonString;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
