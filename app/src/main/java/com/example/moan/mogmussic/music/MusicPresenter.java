package com.example.moan.mogmussic.music;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.MusicConvert;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.util.Pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicPresenter implements MusicContract.Presenter {
    private MusicContract.MusicView mMusicView;
    private static final int toPause = R.drawable.ic_play_circle_outline_orange_a400_24dp;
    private static final int toPlay = R.drawable.ic_pause_circle_outline_orange_a400_24dp;


    public MusicPresenter(MusicContract.MusicView musicView) {
        mMusicView = musicView;
    }

    @Override
    public void onClickType(int index) {
        index = (index + 1 > 2) ? 0 : index + 1;
        mMusicView.changeTypeIcon(index);
    }

    @Override
    public void onClickPrevious() {

    }

    @Override
    public void onClickNext() {

    }

    @Override
    public void onClickList() {

    }

    @Override
    public void onClickLike() {

    }

    @Override
    public void onClickControl(Boolean isPlaying) {
        if (isPlaying) {
            mMusicView.changeControlIcon(toPause);
        } else {
            mMusicView.changeControlIcon(toPlay);
        }
    }

    @Override
    public void onClickBack() {

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
    public List<Music> setMusicList(List<Music> playingMusicList) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        return pool.submit(new MyThread(playingMusicList)).get();
    }

    private class MyThread implements Callable<List<Music>> {
        private List<Music> mMusics;

        MyThread(List<Music> musics) {
            mMusics = musics;
        }

        @Override
        public List<Music> call() throws Exception {
            return mMusics;
        }
    }
}
