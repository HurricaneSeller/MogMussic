package com.example.moan.mogmussic.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.gson.OnlineSong;
import com.example.moan.mogmussic.music.view.ILrcViewListener;
import com.example.moan.mogmussic.music.view.LrcView;
import com.example.moan.mogmussic.online.OAPresenter;
import com.example.moan.mogmussic.online.OnlineActivity;
import com.example.moan.mogmussic.util.MusicUtil;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.music.view.DiskView;
import com.example.moan.mogmussic.music.view.NeedleView;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.TimeFormatUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener,
        MusicContract.MusicView {
    @BindView(R.id.activity_music_info)
    TextView artistView;
    @BindView(R.id.activity_music_name)
    TextView nameView;
    @BindView(R.id.activity_music_control)
    ImageButton btnControl;
    @BindView(R.id.activity_music_back)
    ImageButton btnBack;
    @BindView(R.id.activity_music_like)
    ImageButton btnLike;
    @BindView(R.id.activity_music_list)
    ImageButton btnList;
    @BindView(R.id.activity_music_next)
    ImageButton btnNext;
    @BindView(R.id.activity_music_previous)
    ImageButton btnPrevious;
    @BindView(R.id.activity_music_type)
    ImageButton btnType;
    @BindView(R.id.activity_music_download)
    ImageButton btnDownload;
    @BindView(R.id.activity_music_current_time)
    TextView currentTimeView;
    @BindView(R.id.activity_music_total_time)
    TextView totalTimeView;
    @BindView(R.id.activity_music_seek_bar)
    SeekBar mSeekBar;
    @BindView(R.id.changeable_view)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.disk_layout)
    LinearLayout diskLayout;
    @BindView(R.id.lyrics_layout)
    LinearLayout lyricsLayout;
    @BindView(R.id.lyrics_view)
    LrcView lrcView;
    @BindView(R.id.black_disk)
    DiskView blackDiskView;
    @BindView(R.id.cover_disk)
    DiskView coverView;
    @BindView(R.id.needle_view)
    NeedleView needleView;

    private String TAG = "moanbigking";
    private MusicPresenter mMusicPresenter;
    private boolean isMusicPlaying = true;
    private int type = Constant.Type.ORDER;
    private List<Music> playingMusicList;
    private MyConn mMyConn;
    private Music playingMusic;

    //loop 0;order 1;random 2;
    private static final int[] typeIcons = {R.drawable.ic_repeat_one_orange_400_24dp,
            R.drawable.ic_repeat_orange_400_24dp,
            R.drawable.ic_shuffle_orange_400_24dp};
    private static final String[] typeChangeToasts = {Constant.Type.T_LOOPING,
            Constant.Type.T_ORDER, Constant.Type.T_RANDOM};
    private static final int toPause = R.drawable.ic_play_circle_outline_orange_400_24dp;
    private static final int toPlay = R.drawable.ic_pause_circle_outline_orange_400_24dp;

    private MusicService.MyBinder mMyBinder;
    private String where;
    private boolean isDiskViewShowing = true;
    private boolean isOnline = false;
    private OnlineSong onlineSong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ButterKnife.bind(this);
        mMusicPresenter = new MusicPresenter(this);
        hideActionBar();

        Intent intent = getIntent();
        where = intent.getStringExtra(Constant.Where.WHERE);
        switch (where) {
            case Constant.Where.WHERE_CLICK_LOCAL_SONG:
                SharedPreferences sharedPreferences =
                        getSharedPreferences(Constant.MUSIC_FORMAL_PLAY, MODE_PRIVATE);
                String musicString = sharedPreferences.getString(Constant.MUSIC_FORMAL_PLAY,
                        Constant.DEFAULT_VALUE);
                playingMusic = (Music) intent.getSerializableExtra(Constant.MUSIC_CLICKED);
                if (!Constant.DEFAULT_VALUE.equals(musicString)) {
                    playingMusicList =
                            mMusicPresenter.setMusicList(playingMusicList, musicString, playingMusic);
                } else {
                    playingMusicList = mMusicPresenter.setMusicList(playingMusicList, playingMusic);
                }
                break;
            case Constant.Where.WHERE_CLICK_LIST_SONG:
                MusicList musicList = (MusicList) intent.getSerializableExtra(Constant.LIST_CLICKED);
                playingMusicList = mMusicPresenter.setMusicList(playingMusicList, musicList);
                playingMusic = (Music) intent.getSerializableExtra(Constant.MUSIC_CLICKED);
                break;
            case Constant.Where.WHERE_CLICK_LOCAL_PLAY_ALL:
                try {
                    playingMusicList = mMusicPresenter.setMusicList(this);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                playingMusic = playingMusicList.get(0);
                break;
            case Constant.Where.WHERE_CLICK_LIST_PLAY_ALL:
                MusicList musicList2 = (MusicList) intent.getSerializableExtra(Constant.LIST_CLICKED);
                playingMusicList = mMusicPresenter.setMusicList(playingMusicList, musicList2);
                playingMusic = playingMusicList.get(0);
                break;
            case Constant.Where.WHERE_ONLINE:
                isOnline = true;
                onlineSong = (OnlineSong) intent.getSerializableExtra(Constant.ONLINE_SONG_CLICKED);
                Music music = new Music();
                music.setAlbum(null);
                music.setTitle(onlineSong.getTitle());
                music.setArtist(onlineSong.getAuthor());
                music.setUrl(onlineSong.getUrl());
                music.setAlbum_id(-1);
                music.setDuration(onlineSong.getTime() * 1000);
                playingMusic = music;
                playingMusicList = mMusicPresenter.setMusicList(playingMusicList, music);
                break;

        }

        setOnclickListener();
        mMyConn = new MyConn();
        Intent musicIntent = new Intent(this, MusicService.class);
        musicIntent.putExtra(Constant.MUSIC_SERVICE, playingMusic);
        bindService(musicIntent, mMyConn, BIND_AUTO_CREATE);

        IntentFilter intentFilter = new IntentFilter();
        setAction(intentFilter);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void setAction(IntentFilter intentFilter) {
        intentFilter.addAction(Constant.Action.ACTION_UPDATE_TIME);
        intentFilter.addAction(Constant.Action.ACTION_SONG_FINISHED);
        intentFilter.addAction(Constant.Action.ACTION_BINDER_INIT);
        intentFilter.addAction(Constant.Action.ACTION_CONTROL_NOTIFICATION);
        intentFilter.addAction(Constant.Action.ACTION_FINISH);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    }

    private void setOnclickListener() {
        btnBack.setOnClickListener(this);
        btnControl.setOnClickListener(this);
        btnLike.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnType.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMyBinder.seekToPosition(seekBar.getProgress());
            }
        });
        mRelativeLayout.setOnClickListener(this);
        lrcView.setListener(new ILrcViewListener() {
            @Override
            public void onLrcSeeking(int newPosition, LrcRow lrcRow) {
                mMyBinder.seekToPosition((int) lrcRow.time / 1000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_music_back:
                moveTaskToBack(true);
                addSongToList();
                break;
            case R.id.activity_music_control:
                onClickControl();
                break;
            case R.id.activity_music_like:
                onClickLike();
                break;
            case R.id.activity_music_list:
                onClickList();
                break;
            case R.id.activity_music_next:
                onClickNext();
                break;
            case R.id.activity_music_previous:
                onClickPrevious();
                break;
            case R.id.activity_music_type:
                onClickType();
                break;
            case R.id.changeable_view:
                onClickChangeableView();
                break;
        }
    }

    private void onClickChangeableView() {
        if (isDiskViewShowing) {
            diskLayout.setVisibility(View.GONE);
            lyricsLayout.setVisibility(View.VISIBLE);
        } else {
            lyricsLayout.setVisibility(View.GONE);
            diskLayout.setVisibility(View.VISIBLE);
        }
        isDiskViewShowing = !isDiskViewShowing;

    }

    private void onClickLike() {
        View popupWindowView = LayoutInflater.from(MusicActivity.this)
                .inflate(R.layout.popup_window, null);
        final PopupWindow popupWindow = new PopupWindow(popupWindowView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(nameView, Gravity.BOTTOM, 0, 950);
        RecyclerView recyclerView = popupWindowView.findViewById(R.id.pp_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MusicActivity.this));
        try {
            recyclerView.setAdapter(new PopupWindowListAdapter(mMusicPresenter.getTotalList(this),
                    new PopupWindowListAdapter.IAddSongToList() {
                        @Override
                        public void getMusicList(MusicList musicList) {
                            if (!musicList.isHasPassword()) {
                                mMusicPresenter.addToPlayingList(musicList,
                                        MusicActivity.this, playingMusic);
                            } else {
                                showCheckDialog(musicList.getPassword(), musicList);
                            }
                            popupWindow.dismiss();
                        }
                    }));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showCheckDialog(final String PASSWORD, final MusicList musicList) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.check_dialog_layout, null);
        final EditText editText = view.findViewById(R.id.check_pswd);
        builder.setView(view)
                .setCancelable(true)
                .setPositiveButton(Constant.Words.PERMITTING_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = editText.getText().toString();
                        if (password.equals(PASSWORD)) {
                            mMusicPresenter.addToPlayingList(musicList, MusicActivity.this, playingMusic);
                        } else {
                            Toast.makeText(MusicActivity.this, Constant.Toast.WRONG_PASSWORD,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create().show();
    }


    private void onClickNext() {
        changeSong(true);
    }

    private void onClickPrevious() {
        changeSong(false);
    }

    private void onClickList() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_list, null);
        bottomSheetDialog.setContentView(view);
        RecyclerView recyclerView = view.findViewById(R.id.bottom_song_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomSheetDialogAdapter(playingMusicList,
                new BottomSheetDialogAdapter.IChangeSong() {
                    @Override
                    public void getSong(Music music) {
                        mMyBinder.pause();
                        isMusicPlaying = false;
                        playingMusic = music;
                        mMusicPresenter.initSong(playingMusic, mSeekBar, MusicActivity.this);
                        mMyBinder.changeSong(playingMusic);
                    }

                    @Override
                    public int getMusicId() {
                        return (int) playingMusic.getId();
                    }

                    @Override
                    public void bottomDialogDismiss() {
                        bottomSheetDialog.dismiss();
                    }

                    @Override
                    public void deleteSong(Music music) {
                        playingMusicList.remove(music);
                    }

                    @Override
                    public void toastCannotDelete() {
                        Toast.makeText(MusicActivity.this,
                                Constant.Toast.CANNOT_DONE, Toast.LENGTH_SHORT).show();
                    }
                }));
        bottomSheetDialog.show();
    }

    private void onClickControl() {
        if (isMusicPlaying) {
            animatorPause();
            mMyBinder.pause();
            changeControlIcon(toPause);
        } else {
            animatorResume();
            mMyBinder.start();
            changeControlIcon(toPlay);
        }
        isMusicPlaying = !isMusicPlaying;
    }

    private void onClickType() {
        type = (type + 1 > 2) ? 0 : type + 1;
        changeTypeIcon(type);
        mMyBinder.setLooping(type == Constant.Type.LOOPING);
    }

    @Override
    public void setCurrentTime() {
        currentTimeView.setText(TimeFormatUtil.getPerfectTime(mMyBinder.getCurrentDuration()));
    }

    @Override
    public void setDownloadButton() {
        btnDownload.setVisibility(View.VISIBLE);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(onlineSong);
            }
        });
    }


    private void showDialog(final OnlineSong onlineSong) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定下载")
                .setMessage(onlineSong.getTitle() + "-" + onlineSong.getAuthor())
                .setCancelable(true)
                .setPositiveButton(Constant.Words.PERMITTING_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMusicPresenter.downloadSong(onlineSong, MusicActivity.this);

                    }
                }).create().show();
    }


    @Override
    public void initCurrentTime() {
        currentTimeView.setText(TimeFormatUtil.getPerfectTime(0));
    }

    @Override
    public void setTotalTime(String totalTime) {
        totalTimeView.setText(totalTime);
    }

    @Override
    public void setTitle(String title) {
        nameView.setText(title);
    }

    @Override
    public void setInfo(String info) {
        artistView.setText(info);
    }

    @Override
    public void setCover(Bitmap bm) {
        coverView.setImageBitmap(bm);
    }

    @Override
    public void setLyrics(List<LrcRow> lyrics) {
        lrcView.setLrc(lyrics);
    }

    @Override
    public void changeControlIcon(int index) {
        btnControl.setBackground(ContextCompat.getDrawable(this, index));
    }


    @Override
    public void changeTypeIcon(int index) {
        btnType.setBackground(ContextCompat.getDrawable(this, typeIcons[index]));
        Toast.makeText(this, typeChangeToasts[index], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void animatorStart() {
        blackDiskView.start();
        coverView.start();
        needleView.setPivotX(65f);
        needleView.setPivotY(40f);
        needleView.spinDown();
    }

    @Override
    public void animatorPause() {
        blackDiskView.pause();
        coverView.pause();
        needleView.spinUp();
    }

    @Override
    public void animatorResume() {
        blackDiskView.resume();
        coverView.resume();
        needleView.spinDown();
    }

    @Override
    public void animatorChangeSong() {
        needleView.upThenDown();
    }

    @Override
    public void animatorEnd() {
        blackDiskView.stop();
        coverView.stop();
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorDimGrey));
    }

    class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyBinder = (MusicService.MyBinder) service;
            Intent binderInitFinished = new Intent();
            binderInitFinished.setAction(Constant.Action.ACTION_BINDER_INIT);
            sendBroadcast(binderInitFinished);
            Log.d(TAG, "onServiceConnected: connected successfully and " +
                    "binder init finished.");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: connected failed");
        }

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        boolean isBindFinished = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case Constant.Action.ACTION_UPDATE_TIME:
                    if (isBindFinished) {
                        setCurrentTime();
                        mSeekBar.setProgress((int) mMyBinder.getCurrentDuration() / 1000);
                        lrcView.seekLrcToTime(mMyBinder.getCurrentDuration());
                    }
                    break;
                case Constant.Action.ACTION_SONG_FINISHED:
                    changeSong(true);
                    break;
                case Constant.Action.ACTION_BINDER_INIT:
                    if (!isOnline) {
                        mMusicPresenter.initSong(playingMusic, mSeekBar, MusicActivity.this);
                    } else {
                        mMusicPresenter.initSong(playingMusic, mSeekBar, MusicActivity.this, onlineSong);
                    }
                    isBindFinished = true;
                    animatorStart();
                    break;
                case Constant.Action.ACTION_CONTROL_NOTIFICATION:
                    String key = intent.getStringExtra(Constant.Notification.KEY);
                    switch (key) {
                        case Constant.Notification.KEY_CONTROL:
                            mMyBinder.updateNotificationControlIcon();
                            onClickControl();
                            break;
                        case Constant.Notification.KEY_NEXT:
                            onClickNext();
                            break;
                        case Constant.Notification.KEY_PREVIOUS:
                            onClickPrevious();
                            break;
                    }
                    break;
                case Constant.Action.ACTION_FINISH:
                    finish();
                    break;
                case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                    onClickControl();
                    break;
            }
        }
    };

    private void changeSong(boolean isPlayingNext) {
        mMyBinder.pause();
        if (type == Constant.Type.ORDER) {
            if (isPlayingNext && mMusicPresenter.checkIndex(playingMusic, playingMusicList,
                    true)) {
                playingMusic = playingMusicList.get(playingMusicList.indexOf(playingMusic) + 1);
            } else if (!isPlayingNext && mMusicPresenter.checkIndex(playingMusic, playingMusicList,
                    false)) {
                playingMusic = playingMusicList.get(playingMusicList.indexOf(playingMusic) - 1);
            } else playingMusic = playingMusicList.get(0);
        } else if (type == Constant.Type.RANDOM) {
            playingMusic = playingMusicList.get(new Random().nextInt(playingMusicList.size()));
        }
        mMusicPresenter.initSong(playingMusic, mSeekBar, MusicActivity.this);
        mMyBinder.changeSong(playingMusic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMyConn);
        unregisterReceiver(mBroadcastReceiver);
        if (Constant.Where.WHERE_CLICK_SONG.equals(where)) {
            addSongToList();
        }
        if (isDiskViewShowing) {
            animatorEnd();
        }
    }

    private void addSongToList() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.MUSIC_FORMAL_PLAY,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.MUSIC_FORMAL_PLAY,
                MusicUtil.fromMusicList((ArrayList<Music>) playingMusicList));
        editor.apply();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            addSongToList();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
