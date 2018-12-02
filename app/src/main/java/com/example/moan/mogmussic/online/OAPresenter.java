package com.example.moan.mogmussic.online;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import com.example.moan.mogmussic.gson.OnlineResponse;
import com.example.moan.mogmussic.gson.OnlineSong;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.HTTPUtil;
import com.example.moan.mogmussic.util.Pool;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class OAPresenter implements OnlineContract.OnlineActivityContract {
    private OnlineContract.OnlineActivityView mOnlineActivityView;
    public static final String TYPE_SONG = "song";
    public static final String TYPE_SINGER = "singer";
    public static final String TYPE_LIST = "list";
    private static final String TAG = "moanbigking";

    private static final String BASE_URL = "https://api.hibai.cn/api/index/index";
    private static final String TRANSCODE_LRC = "020222";
    private static final String TRANSCODE_PIC = "020223";
    private static final String TRANSCODE_MUSIC_URL = "020224";
    private static final String TRANSCODE_SEARCH_MUSIC = "020225";
    private static final String songID = null;
    private ImageCache imageCache;
    private List<OnlineSong> songs;


    OAPresenter(OnlineContract.OnlineActivityView onlineActivityView) {
        mOnlineActivityView = onlineActivityView;
    }

    @Override
    public void sendOkHttpRequest(final String input) {
        mOnlineActivityView.showProgressingBar();
        new Pool().getCachedThread().execute(new Runnable() {
            String info = null;

            @Override
            public void run() {
                info = HTTPUtil.getResponse(TRANSCODE_SEARCH_MUSIC, "key", input);
                Log.d(TAG, "run: ");
                while (info == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = new Message();
                message.what = 1;
                message.obj = info;
                mHandler.sendMessage(message);
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
        new Pool().getCachedThread().execute(new Runnable() {
            @Override
            public void run() {
                HTTPUtil.downloadLyrics(onlineSong.getLrc(), onlineSong.getTitle(), context);
            }
        });
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


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1://get song
                    String info = (String) msg.obj;
                    if ("wrong".equals(info)) {
                        mOnlineActivityView.showEmptyIfo();
                    } else {
                        OnlineResponse onlineResponse = new Gson().fromJson(info, OnlineResponse.class);
                        songs = onlineResponse.Body;
                        imageCache = new ImageCache();
                        setCoverToCache();
                    }
                    break;
                case 2:
                    mOnlineActivityView.hideProgressingBar();
                    mOnlineActivityView.setAdapter(songs, imageCache);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private void setCoverToCache() {
        final int length = songs.size();
        for (final OnlineSong song : songs) {
            new Pool().getCachedThread().execute(new Runnable() {
                Bitmap bitmap = null;

                @Override
                public void run() {
                    String temp = song.getPic();
                    bitmap = HTTPUtil.downloadImage(temp);
                    while (bitmap == null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    imageCache.putBitmap(temp, bitmap);
                    if (imageCache.getBitmap(songs.get(length - 1).getPic()) != null) {
                        Message message = Message.obtain();
                        message.what = 2;
                        mHandler.sendMessage(message);
                    }
                }
            });
        }
    }

    class ImageCache implements Serializable {
        private LruCache<String, Bitmap> mBitmapLruCache;

        ImageCache() {
            final int cachedSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 4;
            mBitmapLruCache = new LruCache<String, Bitmap>(cachedSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight() / 1024;
                }
            };
        }

        Bitmap getBitmap(String url) {
            return mBitmapLruCache.get(url);
        }

        void putBitmap(String url, Bitmap bitmap) {
            mBitmapLruCache.put(url, bitmap);
        }
    }

}
