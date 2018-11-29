package com.example.moan.mogmussic.show.showmain;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.data.musiclist.MusicListDatabase;
import com.example.moan.mogmussic.show.ShowContract;
import com.example.moan.mogmussic.show.showlist.ShowListFragment;
import com.example.moan.mogmussic.util.Pool;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class ShowPresenter implements ShowContract.ShowMainPresenter {
    private ShowContract.ShowView mShowView;
    private String TAG = "moanbigking";
    private List<MusicList> mMusicLists = new ArrayList<>();

    public ShowPresenter(ShowContract.ShowView showView) {
        mShowView = showView;
    }


    @Override
    public void getTotalLists(final Context context) {
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                mMusicLists =
                        MusicListDatabase.getInstance(context).musicListDao().getAll();
                mShowView.setMusicLists(mMusicLists);
            }
        });
    }

    @Override
    public void createList(final String name, final String password, final Context context) {
        Log.d(TAG, "createList:  2");
        final Pool model = new Pool();
        model.getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                MusicList musicList = new MusicList();
                musicList.setName(name);
                musicList.setHasPassword(true);
                musicList.setPassword(password);
                MusicListDatabase.getInstance(context).musicListDao().insert(musicList);
                mMusicLists.add(musicList);
            }
        });
    }

    @Override
    public void createList(final String name, final Context context) {
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                MusicList musicList = new MusicList();
                musicList.setName(name);
                MusicListDatabase.getInstance(context).musicListDao().insert(musicList);
                mMusicLists.add(musicList);
                Log.d(TAG, "run: musiclist inset finish");
            }
        });
    }

    @Override
    public void changeFragment(ShowContract.IChangeFra iChangeFra, Fragment fragment) {
        iChangeFra.change(fragment);
    }

    @Override
    public boolean hasPassword(MusicList musicList) {
        return musicList.isHasPassword();
    }

    @Override
    public void check(MusicList musicList) {
        mShowView.showCheckDialog(musicList.getPassword());
    }


}
