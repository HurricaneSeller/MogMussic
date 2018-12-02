package com.example.moan.mogmussic.gson;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class OnlineSong implements Serializable {
    private String title;
    private String author;
    private int time;
    private String url;
    private String pic;
    private String lrc;

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        OnlineSong otherSong = (OnlineSong) obj;
        if (otherSong == null) return false;
        return otherSong.pic.equals(pic);
    }
}
