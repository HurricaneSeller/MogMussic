package com.example.moan.mogmussic.util;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class MusicUtil {
    private static final Uri albumArtUrl = Uri.parse("content://media/external/audio/albumart");
    private static String TAG = "moanbigking";

    @TypeConverter
    public static String fromMusicList(ArrayList<Music> musicArrayList) {
        Gson gson = new Gson();
        return gson.toJson(musicArrayList);
    }

    @TypeConverter
    public static ArrayList<Music> fromString(String value) {
        Type listTYpe = new TypeToken<ArrayList<Music>>() {
        }.getType();
        return new Gson().fromJson(value, listTYpe);
    }

    private static Bitmap getArtWorkFromFile(Context context, int songId, int albumId) {
        Bitmap bitmap = null;
        if (albumId < 0 && songId < 0) {
            throw new IllegalArgumentException("must specif an album or a song");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fileDescriptor = null;
            if (albumId < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + albumId + "/albumart");
                ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().
                        openFileDescriptor(uri, "r");
                if (parcelFileDescriptor != null) {
                    fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUrl, albumId);
                ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().
                        openFileDescriptor(uri, "r");
                if (parcelFileDescriptor != null) {
                    fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            options.inSampleSize = calculateInSampleSize(options, 50, 50);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getArtWork(Context context, int songId, int albumId, boolean allowDefault, String name) {
        if (albumId < 0) {
            if (songId < 0) {
                Bitmap bitmap = getArtWorkFromFile(context, songId, albumId);
                if (bitmap != null) {
                    return bitmap;
                }
            }
            return null;
        }
        Uri uri = ContentUris.withAppendedId(albumArtUrl, albumId);
        if (uri != null) {
            Bitmap bitmap_ = BitmapFactory.decodeFile("/storage/emulated/0/MogMusicImage/" + name + ".jpg");
            if (bitmap_ != null) {
                return bitmap_;
            }
            InputStream inputStream;
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                // if small
                options.inSampleSize = calculateInSampleSize(options, 600, 600);
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                inputStream = context.getContentResolver().openInputStream(uri);
                return BitmapFactory.decodeStream(inputStream, null, options);
            } catch (FileNotFoundException e) {
                Bitmap bitmap = getArtWorkFromFile(context, songId, albumId);
                if (bitmap != null) {
                    if (bitmap.getConfig() == null) {
                        bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
                    }
                } else if (allowDefault) {
                    Log.d(TAG, "getArtWork: ");
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.sample);
                }

                return bitmap;
            }
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int requestWidth,
                                             int requestHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > requestHeight || width > requestWidth) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;
            // calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and weight

            while ((halfHeight / inSampleSize) > requestHeight
                    && (halfWidth / inSampleSize) > requestWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
