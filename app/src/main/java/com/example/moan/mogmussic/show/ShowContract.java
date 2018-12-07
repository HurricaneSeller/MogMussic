package com.example.moan.mogmussic.show;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.show.showmain.ShowPresenter;

import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;

public interface ShowContract {
    interface IChangeFra {
        void change(Fragment fragment);
    }
    interface ISetMusicList{
        void setMusicList(MusicList musicList);
        MusicList getMusicList();
    }

    interface ShowView {
        void setPresenter(ShowPresenter showPresenter);

        void setMusicLists(List<MusicList> musicLists);

        void showCheckDialog(final MusicList musicListChosen);

        void changeFragment(IChangeFra iChangeFra, Fragment fragment);
    }

    interface ShowMainPresenter {
        void getTotalLists();

        void createList(String name, String password);

        void createList(String name);

        boolean hasPassword(MusicList musicList);

        void setMusicList(ISetMusicList iSetMusicList, MusicList musicList);

    }


    interface ShowListView {
        void initRecyclerView(List<Music> music);

        void setListName(String name);

        void setListCover(Bitmap bm);

        void changeFragment(IChangeFra iChangeFra, Fragment fragment);

    }

    interface ShowListPresenter {

        void startMusicActivity(Intent musicIntent, Context context);

        void getListInfo(MusicList musicList, Context context);

        MusicList getMusicList(ISetMusicList iSetMusicList);

        void refreshList(MusicList musicList, Context context);

    }


    interface ShowSongsView {

    }

    interface ShowSongsPresenter {
        void scanLocalSong(Context context);

        void changeFragment(IChangeFra iChangeFra, Fragment fragment);

        void getTotalMusic(Context context, ShowContract.Callback callback);

        void askForPermission(Context context);

        void startMusicActivity(Intent intent, Context context);
    }

    interface SelectView {
        void toastLoadFinished();

        void toastStartInsert();

        void initRecyclerView(List<Music> waitedSongs);

        void setNumberView(int selectNumber);


    }

    interface SelectSongPresenter {
        void addSelectedSongs(HashMap<Integer, Boolean> isSelectSongs, final Context context,
                              final MusicList toBeInsertMusicList);

        void loadLocalSong(Context context);

        MusicList getMusicList(ISetMusicList iSetMusicList);

    }

    interface Callback<T> {
        void showResponse(T result);
    }
}
