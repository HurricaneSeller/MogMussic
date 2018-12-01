package com.example.moan.mogmussic.show.showsong;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.music.MusicDatabase;
import com.example.moan.mogmussic.show.ShowActivity;
import com.example.moan.mogmussic.show.ShowContract;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.Pool;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class ShowSongPresenter implements ShowContract.ShowSongsPresenter {
    private ShowContract.ShowSongsView mShowSongsView;
    private String TAG = "moanbigking";

    public ShowSongPresenter(ShowContract.ShowSongsView showSongsView) {
        mShowSongsView = showSongsView;
    }


    @Override
    public void scanLocalSong(final Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        final List<Music> musicList = new ArrayList<>();
        if (cursor != null && cursor.moveToNext()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                Music music = new Music();

                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                long album_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                if (isMusic != 0 && duration / (500 * 60) >= 1) {
                    setMusic(music, id, title, artist, size, url, duration, album_id,
                            album);
                    musicList.add(music);
                }
                cursor.moveToNext();
            }
        }
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                List<Music> music = MusicDatabase.getInstance(context).musicDao().getAll();
                if (music != null) {
                    MusicDatabase.getInstance(context).musicDao().deleteAll(music);
                }
                MusicDatabase.getInstance(context).musicDao().insertAll(musicList);
            }
        });
    }

    private void setMusic(Music music, long id, String title, String artist, long size,
                          String url, long duration, long album_id, String album) {
        music.setId(id);
        music.setTitle(title);
        music.setArtist(artist);
        music.setSize(size);
        music.setUrl(url);
        music.setDuration(duration);
        music.setAlbum_id(album_id);
        music.setAlbum(album);
    }

    @Override
    public void changeFragment(ShowContract.IChangeFra iChangeFra, Fragment fragment) {
        iChangeFra.change(fragment);
    }

    @Override
    public void getTotalMusic(final Context context) {
        new Pool().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                final List<Music> music = MusicDatabase.getInstance(context).musicDao().getAll();
                mShowSongsView.setTotalMusic(music);
                ((ShowActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mShowSongsView.setTotalSongNumber(music.size() + "");
                    }
                });
            }
        });
    }

    @Override
    public void askForPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
        }
    }

    @Override
    public void startMusicActivity(final Intent intent, final Context context) {
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
