package com.example.moan.mogmussic.music;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.TimeformatUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private static final int LOOPING = 0;
    private static final int ORDER = 1;
    private static final int RANDOM = 2;
    private int type = ORDER;
    private List<Music> playingMusicList;
//    private MusicService.ServiceHandler mMyBinder;
    private MyConn mMyConn;
    private String where;
    private Music playingMusic;
    private static final int[] typeIcons = {R.drawable.ic_repeat_one_orange_a400_24dp,
            R.drawable.ic_repeat_orange_a400_24dp,
            R.drawable.ic_shuffle_orange_a400_24dp};
    private Messenger serviceMessenger;
    private Messenger clientMessenger = new Messenger(new ClientHandler());



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
                    playingMusicList = mMusicPresenter.setMusicList(playingMusicList);
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
//        musicIntent.putExtra(Constant.MUSIC_SERVICE, MusicConvert.fromMusicList((ArrayList<Music>) playingMusicList));
        musicIntent.putExtra(Constant.MUSIC_SERVICE, playingMusic);
        bindService(musicIntent, mMyConn, BIND_AUTO_CREATE);
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
//                mMyBinder.seekToPosition(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_music_back:
                mMusicPresenter.onClickBack();
                break;
            case R.id.activity_music_control:
                if (isMusicPlaying) {
                    Message message = new Message();
                    message.what = Constant.Message.MEDIA_PAUSE;
                    try {
                        serviceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Message message = new Message();
                    message.what = Constant.Message.MEDIA_START;
                    try {
                        serviceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mMusicPresenter.onClickControl(isMusicPlaying);
                isMusicPlaying = !isMusicPlaying;
                break;
            case R.id.activity_music_like:
                mMusicPresenter.onClickLike();
                break;
            case R.id.activity_music_list:
                mMusicPresenter.onClickList();
                break;
            case R.id.activity_music_next:
                mMusicPresenter.onClickNext();
                break;
            case R.id.activity_music_previous:
                mMusicPresenter.onClickPrevious();
                break;
            case R.id.activity_music_type:
                mMusicPresenter.onClickType(typeIcons[type]);
                break;
        }
    }


    @Override
    public void setCurrentTime() {
//        currentTimeView.setText((int) mMyBinder.getCurrentDuration());
    }

    @Override
    public void setTotalTime(Music music) {
        totalTimeView.setText(TimeformatUtil.getPerfectTime(music.getDuration()));
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
        btnType.setBackground(ContextCompat.getDrawable(this, index));
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
            serviceMessenger = new Messenger(service);
            Message message = Message.obtain();
            message.what = 614;
            message.replyTo = clientMessenger;

            Log.d(TAG, "onServiceConnected: connected successfully and " +
                    "binder init finished.");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: connected failed");
            serviceMessenger = null;
        }
    }

    @SuppressLint("HandlerLeak")
    class ClientHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 614:
                    Log.d(TAG, "handleMessage: tessssssssssssss");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMyConn);

    }

}
