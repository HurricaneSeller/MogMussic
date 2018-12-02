package com.example.moan.mogmussic.util;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import com.example.moan.mogmussic.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HTTPUtil {
    public static final String TAG = "moanbigking";
    private static final String BASE_URL = "https://api.hibai.cn/api/index/index";
    private static final String TRANSCODE_LRC = "020222";
    private static final String TRANSCODE_PIC = "020223";
    private static final String TRANSCODE_MUSIC_URL = "020224";
    private static final String TRANSCODE_SEARCH_MUSIC = "020225";
    private static final String songID = null;
    public static final String GE_CI_MI_URI = "http://geci.me/api/lyric/";
    static String test = "http://geci.me/api/lyric/海阔天空";

    public static final String CLOUD_BASE_BASE = "https://api.bzqll.com/music/netease/search?key=579621905&s=";
    public static final String CLOUD_BASE_TYPE = "&type=";
    public static final String TYPE_SONG = "song";
    public static final String TYPE_SINGER = "singer";
    public static final String TYPE_LIST = "list";

    public static final int MAX_SIZE = 10 * 1024 * 1024;


    public static String getResponse(String transCode, String body, String code) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json;charset=UTF-8");
        RequestBody requestBody = RequestBody.create(JSON,
                "{\n" +
                        "    \"TransCode\": \"" + transCode + "\",\n" +
                        "    \"OpenId\": \"123456789\",\n" +
                        "    \"Body\": {\n" +
                        "        \"" + body + "\": \"" + code + "\"\n" +
                        "    }\n" +
                        "}\n");
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "wrong";
        }
        return "wrong";
    }

    public static String getLyricsOnline(String uri) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(uri)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public static String spitResponse(String standardResponse) {
        String[] strings = standardResponse.split("\\}");
        Pattern pattern = Pattern.compile("\"lrc\":\"([\\S\\s])*.lrc\"");
        Matcher matcher = pattern.matcher(strings[0]);
        if (matcher.find()) {
            return matcher.group().substring(7, matcher.group().length() - 1);
        }
        return null;
    }

    public static String getResponse(String input, String type) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(CLOUD_BASE_BASE + input + CLOUD_BASE_TYPE + type)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static Bitmap downloadImage(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        ResponseBody body;
        try {
            body = okHttpClient.newCall(request).execute().body();
            InputStream inputStream;
            if (body != null) {
                inputStream = body.byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    return bitmap;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(HTTPUtil.class.getResourceAsStream("/res/drawable/sample.jpg"));
    }

    public static void downloadSong(String play_url, String name, Context context) {
        Log.d(TAG, "downloadSong: " + play_url);
        String path = Environment.getExternalStorageDirectory().getPath();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(play_url));
        request.setDestinationInExternalPublicDir(path, name + ".mp3");
        request.setTitle("下载完成");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }
    public static void downloadCover(String img, String name, Context context) {
        String path = "/MogMusicImage/";
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(img));
        request.setDestinationInExternalPublicDir(path, name + ".jpg");
        downloadManager.enqueue(request);
    }


}
