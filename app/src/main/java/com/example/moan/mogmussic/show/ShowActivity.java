package com.example.moan.mogmussic.show;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.music.MusicActivity;
import com.example.moan.mogmussic.show.showmain.ShowFragment;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.MusicUtil;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowActivity extends AppCompatActivity implements ShowContract.IChangeFra,ShowContract.ISetMusicList {
    @BindView(R.id.activity_main_name)
    TextView nameView;
    @BindView(R.id.activity_main_artist)
    TextView artistView;
    @BindView(R.id.activity_main_control)
    ImageButton controlButton;
    private String TAG = "moanbigking";
    @BindView(R.id.bar)
    View barView;
    @BindView(R.id.activity_main_cover)
    ImageView coverView;

    MusicList mMusicList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        hideActionBar();
        change(new ShowFragment());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Action.ACTION_SET_VIEW);
        registerReceiver(changeBarBroadcastReceiver, intentFilter);
        setOnClickListener();
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorOrange400));

    }
    @Override
    public void change(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack("");
        transaction.replace(R.id.activity_main_frame_layout, fragment);
        transaction.commit();
    }

    private void setOnClickListener() {
        barView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowActivity.this, MusicActivity.class));
            }
        });
    }

    private BroadcastReceiver changeBarBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.Action.ACTION_SET_VIEW.equals(action)) {
                Music music = (Music)intent.getSerializableExtra("music");
                setView(music);
            }
        }
    };

    private void setView(Music music) {
        nameView.setText(music.getTitle());
        String info = music.getArtist() + ":" + music.getAlbum();
        artistView.setText(info);
        coverView.setImageBitmap(MusicUtil.getArtWork(this, (int)music.getId() ,
                (int)music.getAlbum_id(), true, music.getTitle()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(changeBarBroadcastReceiver);
    }

    private long firstTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(this, Constant.Toast.DOUBLE_PRESS, Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                sendBroadcast(new Intent().setAction(Constant.Action.ACTION_FINISH));
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setMusicList(MusicList musicList) {
        mMusicList = musicList;
    }

    @Override
    public MusicList getMusicList() {
        return mMusicList;
    }
}