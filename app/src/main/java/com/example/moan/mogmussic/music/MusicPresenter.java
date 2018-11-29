package com.example.moan.mogmussic.music;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.SeekBar;

import com.example.moan.mogmussic.data.MusicConvert;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.music.MusicDatabase;
import com.example.moan.mogmussic.data.musiclist.MusicList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicPresenter implements MusicContract.Presenter {
    private MusicContract.MusicView mMusicView;
    private String TAG = "moanbigking";

    MusicPresenter(MusicContract.MusicView musicView) {
        mMusicView = musicView;
    }

    @Override
    public void initSong(Music music, SeekBar seekBar) {
        mMusicView.setTotalTime(music);
        mMusicView.setInfo(music);
        mMusicView.setTitle(music);
        mMusicView.initCurrentTime();
        seekBar.setMax((int)music.getDuration() / 1000);
    }

    @Override
    public List<Music> setMusicList(List<Music> playingMusicList, Music music) {
        if (playingMusicList == null) {
            playingMusicList = new ArrayList<>();
        }
        playingMusicList.add(music);
        return playingMusicList;
    }

    @Override
    public List<Music> setMusicList(List<Music> playingMusicList, MusicList musicList) {
        playingMusicList = MusicConvert.fromString(musicList.getMusicJsonString());
        return playingMusicList;
    }

    @Override
    public List<Music> setMusicList(Context context) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        return pool.submit(new getListMusicsThread(context)).get();
    }

    @Override
    public boolean checkIndex(Music music, List<Music> musicList, boolean isPlayNext) {
        if (isPlayNext){
            return musicList.indexOf(music) + 1 < musicList.size();
        }
        else {
            return musicList.indexOf(music) - 1 == 0;
        }

    }

    private class getListMusicsThread implements Callable<List<Music>> {
        private Context mContext;

        getListMusicsThread(Context context) {
            mContext = context;
        }

        @Override
        public List<Music> call() throws Exception {
            return MusicDatabase.getInstance(mContext).musicDao().getAll();
        }
    }
}
