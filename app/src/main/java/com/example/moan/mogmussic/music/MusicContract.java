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

        void animatorStart();

        void animatorPause();

        void animatorResume();

        void animatorChangeSong();

        void animatorEnd();

    }

    interface Presenter {
        // the init
        void initSong(Music music, SeekBar seekBar);

        List<Music> setMusicList(List<Music> nowList, String musicString, Music music);

        List<Music> setMusicList(List<Music> playingMusicList, Music music) ;

        List<Music> setMusicList(List<Music> playingMusicList, MusicList musicList);

        List<Music> setMusicList(Context context) throws ExecutionException, InterruptedException;

        boolean checkIndex(Music music, List<Music> musicList, boolean isPlayNext);

        List<MusicList> getTotalList(Context context) throws ExecutionException, InterruptedException;

        void addToPlayingList(MusicList musicList, Context context, Music music);
    }

    interface IMusicControl {
        void start();

        void pause();

        void seekToPosition(int position);

        void changeSong(Music music);

        long getCurrentDuration();

        void setLooping(Boolean isLooping);

        void updateNotificationControlIcon();
    }
    interface IBlackDiskView{
        void start();
        void pause();
        void resume();
    }

}
