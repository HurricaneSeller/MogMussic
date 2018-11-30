package com.example.moan.mogmussic.music;

import android.content.Context;
import android.widget.SeekBar;

import com.example.moan.mogmussic.util.MusicConvert;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.music.MusicDatabase;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.data.musiclist.MusicListDatabase;
import com.example.moan.mogmussic.util.Pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
        mMusicView.animatorChangeSong();
        seekBar.setMax((int) music.getDuration() / 1000);
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
        return Executors.newSingleThreadExecutor().submit(new getListMusicsThread(context)).get();
    }

    @Override
    public boolean checkIndex(Music music, List<Music> musicList, boolean isPlayNext) {
        if (isPlayNext) {
            return musicList.indexOf(music) + 1 < musicList.size();
        } else {
            return musicList.indexOf(music) - 1 != 0;
        }

    }

    @Override
    public List<MusicList> getTotalList(Context context) throws ExecutionException, InterruptedException {
        return Executors.newSingleThreadExecutor().submit(new getListsThread(context)).get();
    }

    @Override
    public void addToPlayingList(final MusicList musicList, final Context context, final Music music) {
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                MusicList formalList = MusicListDatabase
                        .getInstance(context).musicListDao().findByName(musicList.getName());
                List<Music> list = MusicConvert.fromString(formalList.getMusicJsonString());
                if (list == null){
                    list = new ArrayList<>();
                }
                if (!list.contains(music)) {
                    list.add(music);
                    musicList.setMusicJsonString(MusicConvert
                            .fromMusicList((ArrayList<Music>) list));
                    MusicListDatabase
                            .getInstance(context).musicListDao().updateMusicList(musicList);
                }
            }
        });
    }

    @Override
    public List<Music> setMusicList(List<Music> nowList, String musicString, Music music) {
        if (nowList == null) {
            nowList = MusicConvert.fromString(musicString);
        }
        if (!nowList.contains(music)) {
            nowList.add(music);
        }
        return nowList;
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

    private class getListsThread implements Callable<List<MusicList>> {
        private Context mContext;

        getListsThread(Context context) {
            mContext = context;
        }

        @Override
        public List<MusicList> call() throws Exception {

            return MusicListDatabase.getInstance(mContext).musicListDao().getAll();
        }
    }
}
