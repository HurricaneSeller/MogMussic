package com.example.moan.mogmussic.show;

import android.content.Context;
import android.widget.LinearLayout;

import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;

import java.util.List;

import androidx.fragment.app.Fragment;

public interface ShowContract {
    interface IChangeFra {
        void change(Fragment fragment);
    }

    interface ShowView {
        void setListsNumber(String number);

        void setMusicLists(List<MusicList> musicLists);

        void showCheckDialog(String PASSWORD);
    }

    interface ShowMainPresenter {
        void getTotalLists(Context context);

        void createList(String name, String password, Context context);

        void createList(String name, Context context);

        void changeFragment(IChangeFra iChangeFra, Fragment fragment);

        boolean hasPassword(MusicList musicList);

        void check(MusicList musicList);
    }


    interface ShowListView {
        void setChosenSongNumber(String number);

        void setListName(String name);

        void setListCover(int albumId);
    }

    interface ShowListPresenter {
        void playAll();

        void addSong();

    }


    interface ShowSongsView {
        void setTotalSongNumber(String number);

        void setTotalMusic(List<Music> musics);

    }

    interface ShowSongsPresenter {
        void playAll();

        void scanLocalSong(Context context);

        void changeFragment(IChangeFra iChangeFra, Fragment fragment);

        void getTotalMusic(Context context);

        void askForPermission(Context context);
    }
}
