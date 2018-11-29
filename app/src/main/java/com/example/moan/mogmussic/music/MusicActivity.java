package com.example.moan.mogmussic.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.TimeFormatUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.ActionBar;
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
    @BindView(R.id.activity_music_current_time)
    TextView currentTimeView;
    @BindView(R.id.activity_music_total_time)
    TextView totalTimeView;
    @BindView(R.id.activity_music_seek_bar)
    SeekBar mSeekBar;

    private String TAG = "moanbigking";
    private MusicPresenter mMusicPresenter;
    private boolean isMusicPlaying = false;
    private int type = Constant.Type.ORDER;
    private List<Music> playingMusicList;
    //    private MusicService.ServiceHandler mMyBinder;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ButterKnife.bind(this);
        mMusicPresenter = new MusicPresenter(this);
        hideActionBar();

        Intent intent = getIntent();
        String where = intent.getStringExtra(Constant.Where.WHERE);
        switch (where) {
            case Constant.Where.WHERE_CLICK_LOCAL_SONG:
                playingMusic = (Music) intent.getSerializableExtra(Constant.MUSIC_CLICKED);
                playingMusicList = mMusicPresenter.setMusicList(playingMusicList, playingMusic);
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
        }

        setOnclickListener();
        mMyConn = new MyConn();
        Intent musicIntent = new Intent(this, MusicService.class);
        musicIntent.putExtra(Constant.MUSIC_SERVICE, playingMusic);
        bindService(musicIntent, mMyConn, BIND_AUTO_CREATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Action.ACTION_UPDATE_TIME);
        intentFilter.addAction(Constant.Action.ACTION_SONG_FINISHED);
        intentFilter.addAction(Constant.Action.ACTION_BINDER_INIT);
        intentFilter.addAction(Constant.Action.ACTION_RESET_FINISHED);
        registerReceiver(mBroadcastReceiver, intentFilter);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_music_back:
                moveTaskToBack(false);
                break;
            case R.id.activity_music_control:
                onClickControl();
                break;
            case R.id.activity_music_like:
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
        }
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
                        mMusicPresenter.initSong(playingMusic, mSeekBar);
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
                }));
        bottomSheetDialog.show();
    }

    private void onClickControl() {
        if (isMusicPlaying) {
            mMyBinder.pause();
            changeControlIcon(toPause);
        } else {
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
    public void initCurrentTime() {
        currentTimeView.setText(TimeFormatUtil.getPerfectTime(0));
    }

    @Override
    public void setTotalTime(Music music) {
        totalTimeView.setText(TimeFormatUtil.getPerfectTime(music.getDuration()));
    }

    @Override
    public void setTitle(Music music) {
        nameView.setText(music.getTitle());
    }

    @Override
    public void setInfo(Music music) {
        String info = music.getArtist() + "-" + music.getAlbum();
        artistView.setText(info);
    }

    @Override
    public void setCover(Music music) {

    }

    @Override
    public void setLyrics(Music music) {

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
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case Constant.Action.ACTION_UPDATE_TIME:
                    setCurrentTime();
                    mSeekBar.setProgress((int) mMyBinder.getCurrentDuration() / 1000);
                    break;
                case Constant.Action.ACTION_SONG_FINISHED:
                    changeSong(true);
                    break;
                case Constant.Action.ACTION_BINDER_INIT:
                    mMusicPresenter.initSong(playingMusic, mSeekBar);
                    break;
                case Constant.Action.ACTION_RESET_FINISHED:
                    isMusicPlaying = false;
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
        mMusicPresenter.initSong(playingMusic, mSeekBar);
        mMyBinder.changeSong(playingMusic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMyConn);
        unregisterReceiver(mBroadcastReceiver);
    }

}
