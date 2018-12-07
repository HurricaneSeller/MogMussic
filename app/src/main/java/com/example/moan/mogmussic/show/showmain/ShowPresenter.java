package com.example.moan.mogmussic.show.showmain;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.data.musiclist.MusicListDatabase;
import com.example.moan.mogmussic.show.ShowContract;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.Pool;

import java.util.ArrayList;
import java.util.List;

public class ShowPresenter implements ShowContract.ShowMainPresenter {
    private ShowContract.ShowView mShowView;
    private Context mContext;
    private String TAG = "moanbigking";
    private List<MusicList> mMusicLists = new ArrayList<>();
    private static final String TRANSCODE_LRC = "020222";
    private static final String TRANSCODE_PIC = "020223";
    private static final String TRANSCODE_MUSIC_URL = "020224";
    private static final String TRANSCODE_SEARCH_MUSIC = "020225";


    public ShowPresenter(ShowContract.ShowView showView, Context context) {
        mShowView = showView;
        mContext = context;
        mShowView.setPresenter(this);
    }


    @Override
    public void getTotalLists() {
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                mMusicLists =
                        MusicListDatabase.getInstance(mContext).musicListDao().getAll();
                mShowView.setMusicLists(mMusicLists);
            }
        });
    }

    @Override
    public void createList(final String name, final String password) {
        Log.d(TAG, "createList:  2");
        final Pool model = new Pool();
        model.getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                MusicList musicList = new MusicList();
                musicList.setName(name);
                musicList.setHasPassword(true);
                musicList.setPassword(password);
                MusicListDatabase.getInstance(mContext).musicListDao().insert(musicList);
                mMusicLists.add(musicList);
            }
        });
    }

    @Override
    public void createList(final String name) {
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                MusicList musicList = new MusicList();
                musicList.setName(name);
                MusicListDatabase.getInstance(mContext).musicListDao().insert(musicList);
                mMusicLists.add(musicList);
                Log.d(TAG, "run: musiclist inset finish");
            }
        });
    }

    @Override
    public boolean hasPassword(MusicList musicList) {
        return musicList.isHasPassword();
    }

    @Override
    public void setMusicList(ShowContract.ISetMusicList iSetMusicList, MusicList musicList) {
        iSetMusicList.setMusicList(musicList);
    }


    void finishMusicActivityIfExisting(final Context context, final Intent intent) {
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                context.sendBroadcast(new Intent().setAction(Constant.Action.ACTION_FINISH));
            }
        });
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                context.startActivity(intent);
            }
        });
    }
}
