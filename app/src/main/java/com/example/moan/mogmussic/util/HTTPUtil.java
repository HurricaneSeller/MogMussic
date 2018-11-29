package com.example.moan.mogmussic.util;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPUtil {
    public static final String TAG = "moanbigking";
    private static final String BASE_URL = "https://api.hibai.cn/api/index/index";
    private static final String TRANSCODE_LRC = "020222";
    private static final String TRANSCODE_PIC = "020223";
    private static final String TRANSCODE_MUSIC_URL = "020224";
    private static final String TRANSCODE_SEARCH_MUSIC = "020225";
    private static final String songID = null;


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
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
