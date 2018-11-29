package com.example.moan.mogmussic.music;

import android.content.Context;
import android.widget.SeekBar;

import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface MusicContract {
    interface MusicView {
        void initCurrentTime();

        void setCurrentTime();

        void setTotalTime(Music music);

        void setTitle(Music music);

        void setInfo(Music music);

        void setCover(Music music);

        void setLyrics(Music music);

        void changeControlIcon(int index);

        void changeTypeIcon(int index);

    }

    interface Presenter {
        // the init
        void initSong(Music music, SeekBar seekBar);

        List<Music> setMusicList(List<Music> playingMusicList, Music music);

        List<Music> setMusicList(List<Music> playingMusicList, MusicList musicList);

        List<Music> setMusicList(Context context) throws ExecutionException, InterruptedException;

        boolean checkIndex(Music music, List<Music> musicList, boolean isPlayNext);
    }

    interface IMusicControl {
        void start();

        void pause();

        void seekToPosition(int position);

        void changeSong(Music music);

        long getCurrentDuration();

        void setLooping(Boolean isLooping);
    }
}
