package com.example.moan.mogmussic.show.showlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.data.musiclist.MusicListDatabase;
import com.example.moan.mogmussic.show.ShowContract;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.MusicUtil;
import com.example.moan.mogmussic.util.Pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShowListPresenter implements ShowContract.ShowListPresenter {
    private ShowContract.ShowListView mShowListView;
    private String TAG = "moanbigking";
    private MusicList musicList;

    ShowListPresenter(ShowContract.ShowListView showListView) {
        mShowListView = showListView;
    }

    @Override
    public void startMusicActivity(final Intent musicIntent, final Context context) {
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                context.sendBroadcast(new Intent().setAction(Constant.Action.ACTION_FINISH));
            }
        });
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                context.startActivity(musicIntent);
            }
        });
    }

    @Override
    public void getListInfo(MusicList musicList, Context context) {
        mShowListView.setListName(musicList.getName());
        List<Music> musics = MusicUtil.fromString(musicList.getMusicJsonString());
        if (musics != null && musics.size() != 0) {
            int random = new Random().nextInt(musics.size());
            Music music = musics.get(random);
            mShowListView.setListCover(MusicUtil.getArtWork(context, (int) music.getId(),
                    (int) music.getAlbum_id(), true, music.getTitle()));
        } else {
            mShowListView.setListCover(BitmapFactory
                    .decodeResource(context.getResources(), R.drawable.sample));
            musics = new ArrayList<>();
        }
        mShowListView.initRecyclerView(musics);
    }


    @Override
    public MusicList getMusicList(ShowContract.ISetMusicList iSetMusicList) {
        return iSetMusicList.getMusicList();
    }

    @Override
    public void refreshList(final MusicList musicList, final Context context) {
        new Pool().getSingleThread().execute(new Runnable() {
            MusicList musicList1 = null;
            @Override
            public void run() {
                musicList1 = MusicListDatabase.getInstance(context).musicListDao().findByName(musicList.getName());
                Message message = new Message();
                message.what = 0;
                message.obj = musicList1;
                mHandler.sendMessage(message);
            }
        });
    }



    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    musicList = (MusicList)msg.obj;
                    mShowListView.initRecyclerView(MusicUtil.fromString(musicList.getMusicJsonString()));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
}
