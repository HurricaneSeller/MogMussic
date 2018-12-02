package com.example.moan.mogmussic.online;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.gson.OnlineSong;
import com.example.moan.mogmussic.music.MusicActivity;
import com.example.moan.mogmussic.util.Constant;

import java.nio.ByteBuffer;
import java.util.List;

public class OnlineActivity extends AppCompatActivity implements OnlineContract.OnlineActivityView {
    private ProgressBar mProgressBar;
    private OAPresenter mOAPresenter;
    private String TAG = "moanbigking";
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        Intent intent = getIntent();
        String input = intent.getStringExtra(Constant.Key.ONLINE);
        hideActionBar();
        mOAPresenter = new OAPresenter(this);
        mProgressBar = findViewById(R.id.online_progressing_bar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFA726"),
                PorterDuff.Mode.SRC_IN);
        mRecyclerView = findViewById(R.id.online_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mOAPresenter.sendOkHttpRequest(input);
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorOrange400));

    }

    @Override
    public void showProgressingBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressingBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyIfo() {
        // TODO: 12/2/18
        Log.d(TAG, "showEmptyIfo: ");
    }



    @Override
    public void setAdapter(List<OnlineSong> onlineSongs, final OAPresenter.ImageCache imageCache) {
        mRecyclerView.setAdapter(new OnlineSongAdapter(onlineSongs, imageCache,
                new OnlineSongAdapter.IHelper() {
                    @Override
                    public void downloadSong(OnlineSong onlineSong) {
                        showDialog(onlineSong);
                    }

                    @Override
                    public void playSong(OnlineSong onlineSong) {
                        Intent intent = new Intent(OnlineActivity.this, MusicActivity.class);
                        intent.putExtra(Constant.Where.WHERE, Constant.Where.WHERE_ONLINE);
                        intent.putExtra(Constant.ONLINE_SONG_CLICKED, onlineSong);
                        mOAPresenter.startMusicActivity(intent, OnlineActivity.this);
                    }
                }));
    }


    private void showDialog(final OnlineSong onlineSong) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定下载")
                .setMessage(onlineSong.getTitle() + "-" + onlineSong.getAuthor())
                .setCancelable(true)
                .setPositiveButton(Constant.Words.PERMITTING_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOAPresenter.downloadSong(onlineSong, OnlineActivity.this);

                    }
                }).create().show();
    }

}
