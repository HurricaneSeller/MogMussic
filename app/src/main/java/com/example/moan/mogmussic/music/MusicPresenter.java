package com.example.moan.mogmussic.music;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TabHost;

import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.music.MusicDatabase;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.data.musiclist.MusicListDatabase;
import com.example.moan.mogmussic.gson.OnlineSong;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.HTTPUtil;
import com.example.moan.mogmussic.util.MusicUtil;
import com.example.moan.mogmussic.util.Pool;
import com.example.moan.mogmussic.util.TimeFormatUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicPresenter implements MusicContract.Presenter {
    private MusicContract.MusicView mMusicView;
    private String TAG = "moanbigking";
    private boolean isFirstPlay = true;
    private Bitmap mBitmap;


    MusicPresenter(MusicContract.MusicView musicView) {
        mMusicView = musicView;
    }

    @Override
    public void initSong(Music music, SeekBar seekBar, Context context) {
        mMusicView.setTotalTime(TimeFormatUtil.getPerfectTime(music.getDuration()));
        mMusicView.setInfo(music.getArtist() + "-" + music.getAlbum());
        mMusicView.setTitle(music.getTitle());
        mMusicView.initCurrentTime();
        if (!isFirstPlay) {
            mMusicView.animatorChangeSong();
        }
        seekBar.setMax((int) music.getDuration() / 1000);
        isFirstPlay = false;
        getLyrics(music, mHandler);
        mMusicView.setCover(MusicUtil.getArtWork(context, (int) music.getId(), (int) music.getAlbum_id(),
                true, music.getTitle()));
    }

    @Override
    public void initSong(Music music, SeekBar seekBar, Context context, final OnlineSong onlineSong) {
        mMusicView.setTotalTime(TimeFormatUtil.getPerfectTime(music.getDuration()));
        mMusicView.setInfo(music.getArtist() + "-" + music.getAlbum());
        mMusicView.setTitle(music.getTitle());
        mMusicView.initCurrentTime();
        mMusicView.setDownloadButton();
        if (!isFirstPlay) {
            mMusicView.animatorChangeSong();
        }
        seekBar.setMax((int) music.getDuration() / 1000);
        isFirstPlay = false;
        new Pool().getCachedThread().execute(new Runnable() {
            @Override
            public void run() {
                mBitmap = HTTPUtil.downloadImage(onlineSong.getPic());
                while (mBitmap == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (mBitmap != null) {
                    Message message = Message.obtain();
                    message.what = 666;
                    mHandler.sendMessage(message);
                }
            }
        });
        getLyricsByKuGouAPI(onlineSong, mHandler);
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
        playingMusicList = MusicUtil.fromString(musicList.getMusicJsonString());
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
                List<Music> list = MusicUtil.fromString(formalList.getMusicJsonString());
                if (list == null) {
                    list = new ArrayList<>();
                }
                if (!list.contains(music)) {
                    list.add(music);
                    musicList.setMusicJsonString(MusicUtil
                            .fromMusicList((ArrayList<Music>) list));
                    MusicListDatabase
                            .getInstance(context).musicListDao().updateMusicList(musicList);
                }
            }
        });
    }

    @Override
    public void downloadSong(final OnlineSong onlineSong, final Context context) {
        new Pool().getCachedThread().execute(new Runnable() {
            @Override
            public void run() {
                HTTPUtil.downloadSong(onlineSong.getUrl(), onlineSong.getTitle(), context);
            }
        });
        new Pool().getCachedThread().execute(new Runnable() {
            @Override
            public void run() {
                HTTPUtil.downloadCover(onlineSong.getPic(), onlineSong.getTitle(), context);
            }
        });


    }

    private void getLyrics(Music music, Handler handler) {
        String lyrics = getLocalLyrics(music);
        if (lyrics == null) {
            getLyricsByGeCiMiAPI(music, handler);
        }
        Message message = new Message();
        message.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString(Constant.Key.LYRICS, lyrics);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private String getLocalLyrics(Music music) {
        File file = new File("/storage/emulated/0/lyrics/" + music.getTitle() + ".lrc");
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String content;
            StringBuilder stringBuffer = new StringBuilder();
            while ((content = bufferedReader.readLine()) != null) {
                if (content.length() <= 20) {
                    stringBuffer.append(content).append("\n");
                } else {
                    String temp = content.replace("\\n", "\n");
                    String[] temps = temp.split("\n");
                    for (String s : temps) {
                        stringBuffer.append(s).append("\n");
                    }
                }
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void getLyricsByKuGouAPI(final OnlineSong onlineSong, final Handler handler) {
        String standard = onlineSong.getUrl();
        String id = null;
        Pattern pattern = Pattern.compile("id=([\\S\\s])*&");
        Matcher matcher = pattern.matcher(standard);
        if (matcher.find()) {
            id = matcher.group();
        }
        final String finalId = id.substring(3, id.length() - 1);
        Log.d(TAG, "getLyricsByKuGouAPI: " + finalId);
        new Pool().getCachedThread().execute(new Runnable() {
            String base = null;
            String lyrics = null;

            @Override
            public void run() {
                base = HTTPUtil.getResponse("020222", "SongId", finalId);
                String temp = null;
                while (base == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Pattern pattern1 = Pattern.compile("\"Body\":\"([\\S\\s])*&type=lrc");
                Matcher matcher1 = pattern1.matcher(base);
                if (matcher1.find()) {
                    temp = matcher1.group();
                    temp = temp.substring(8, temp.length());
                    lyrics = HTTPUtil.getLyricsOnline(temp);
                    while (lyrics == null) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Message message = Message.obtain();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.Key.LYRICS, lyrics);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void getLyricsByGeCiMiAPI(final Music music, final Handler handler) {
        new Pool().getCachedThread().execute(new Runnable() {
            String base = null;
            String lyrics = null;

            @Override
            public void run() {
                base = HTTPUtil.getLyricsOnline("http://geci.me/api/lyric/" + music.getTitle());
                while (base == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                base = HTTPUtil.spitResponse(base);
                if (base == null) return;
                lyrics = HTTPUtil.getLyricsOnline(base);
                while (lyrics == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = new Message();
                message.what = 0;
                Bundle bundle = new Bundle();
                bundle.putString(Constant.Key.LYRICS, lyrics);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public List<Music> setMusicList(List<Music> nowList, String musicString, Music music) {
        if (nowList == null) {
            nowList = MusicUtil.fromString(musicString);
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Bundle bundle = msg.getData();
                    String lyrics = bundle.getString(Constant.Key.LYRICS);
                    List<LrcRow> lrcRows;
                    try {
                        lrcRows = LrcRow.getLrcRows(lyrics);
                        mMusicView.setLyrics(lrcRows);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                case 666:
                    mMusicView.setCover(mBitmap);
                    break;
                case 1:
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };


}
