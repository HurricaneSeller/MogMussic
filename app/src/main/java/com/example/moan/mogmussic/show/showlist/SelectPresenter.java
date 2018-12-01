package com.example.moan.mogmussic.show.showlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.music.MusicDatabase;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.data.musiclist.MusicListDatabase;
import com.example.moan.mogmussic.music.MusicContract;
import com.example.moan.mogmussic.show.ShowContract;
import com.example.moan.mogmussic.util.MusicUtil;
import com.example.moan.mogmussic.util.Pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectPresenter implements ShowContract.SelectSongPresenter {
    private ShowContract.SelectView mSelectView;
    private String TAG = "moanbigking";
    private List<Music> totalSong;
    private List<Music> alreadyExistedSongs = new ArrayList<>();
    private MusicList musicList;

    SelectPresenter(ShowContract.SelectView selectView) {
        mSelectView = selectView;
    }

    @Override
    public void addSelectedSongs(HashMap<Integer, Boolean> isSelectHashMap, final Context context, final MusicList toBeInsertMusicList) {
        mSelectView.toastStartInsert();

        Set<Map.Entry<Integer, Boolean>> set = isSelectHashMap.entrySet();
        for (Map.Entry<Integer, Boolean> map : set) {
            if (map.getValue()) {
                for (Music music : totalSong) {
                    if ((int) music.getId() == map.getKey()) {
                        alreadyExistedSongs.add(music);
                    }
                }
            }
        }

        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                musicList = MusicListDatabase.getInstance(context)
                        .musicListDao().findByName(toBeInsertMusicList.getName());
                while (musicList == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                musicList.setMusicJsonString(MusicUtil
                        .fromMusicList((ArrayList<Music>) alreadyExistedSongs));
                MusicListDatabase.getInstance(context).musicListDao()
                        .updateMusicList(musicList);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    public void loadLocalSong(final Context context) {
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 1");
                totalSong = MusicDatabase.getInstance(context).musicDao().getAll();
                while (totalSong == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = Message.obtain();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    public MusicList getMusicList(ShowContract.ISetMusicList iSetMusicList) {
        musicList = iSetMusicList.getMusicList();
        alreadyExistedSongs = MusicUtil.fromString(musicList.getMusicJsonString());
        return musicList;
    }


    private List<Music> getWaitToBeSelectedSongs(List<Music> alreadyExistsSongs) {
        List<Music> waitToBeAddedSongs = new ArrayList<>();
        for (Music music : totalSong) {
            if (!alreadyExistsSongs.contains(music)) {
                waitToBeAddedSongs.add(music);
            }
        }
        return waitToBeAddedSongs;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mSelectView.toastLoadFinished();
                    mSelectView.initRecyclerView(getWaitToBeSelectedSongs(alreadyExistedSongs));
                    break;
                case 1:
                    mSelectView.toastLoadFinished();
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

}
