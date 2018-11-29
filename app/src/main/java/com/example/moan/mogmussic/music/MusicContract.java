package com.example.moan.mogmussic.music;

import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface MusicContract {
    interface MusicView {
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
        void onClickType(int index);

        void onClickPrevious();

        void onClickNext();

        void onClickList();

        void onClickLike();

        void onClickControl(Boolean isPlaying);

        void onClickBack();

        List<Music> setMusicList(List<Music> playingMusicList, Music music);

        List<Music> setMusicList(List<Music> playingMusicList, MusicList musicList);

        List<Music> setMusicList(List<Music> playingMusicList) throws ExecutionException, InterruptedException;
    }

}
