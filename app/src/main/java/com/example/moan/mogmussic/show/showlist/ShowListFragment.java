package com.example.moan.mogmussic.show.showlist;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.util.Pools;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.music.MusicActivity;
import com.example.moan.mogmussic.show.ShowActivity;
import com.example.moan.mogmussic.show.ShowContract;
import com.example.moan.mogmussic.show.showsong.SongAdapter;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.MusicUtil;
import com.example.moan.mogmussic.util.Pool;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
// TODO: 11/27/18 if click list song !!!!!!!!
public class ShowListFragment extends Fragment implements ShowContract.ShowListView, View.OnClickListener {
    private MusicList musicList;
    private ShowContract.ShowListPresenter mShowListPresenter;
    private String TAG = "moanbigking";
    @BindView(R.id.as_back)
    ImageButton btnBack;
    @BindView(R.id.as_play_all)
    ImageButton btnPlayAll;
    @BindView(R.id.as_check)
    ImageButton btnCheck;
    @BindView(R.id.as_cover)
    ImageView randomCoverView;
    @BindView(R.id.as_name)
    TextView nameView;
    @BindView(R.id.song_list)
    RecyclerView mRecyclerView;
    private List<Music> mMusics = new ArrayList<>();
    private SongAdapter songAdapter;

    public ShowListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowListPresenter = new ShowListPresenter(this);
        musicList = mShowListPresenter.getMusicList((ShowActivity)getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Action.ACTION_START_MUSIC);
        intentFilter.addAction(Constant.Action.ACTION_REFRESH_LIST);
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_list, container, false);
        ButterKnife.bind(this, view);
        btnBack.setOnClickListener(this);
        btnPlayAll.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        mShowListPresenter.getListInfo(musicList, getActivity());
        return view;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.Action.ACTION_START_MUSIC.equals(action)) {
                String where = intent.getStringExtra(Constant.Where.WHERE);
                switch (where) {
                    case Constant.Where.WHERE_CLICK_LIST_PLAY_ALL:
                        Intent musicIntent = new Intent(getActivity(), MusicActivity.class);
                        musicIntent.putExtra(Constant.Where.WHERE, Constant.Where.WHERE_CLICK_LOCAL_PLAY_ALL);
                        musicIntent.putExtra(Constant.LIST_CLICKED, musicList);
                        mShowListPresenter.startMusicActivity(musicIntent, getActivity());
                        break;
                    case Constant.Where.WHERE_CLICK_SONG:
                        Music music = (Music) intent.getSerializableExtra(Constant.MUSIC_CLICKED);
                        Intent songIntent = new Intent(getActivity(), MusicActivity.class);
                        songIntent.putExtra(Constant.Where.WHERE, Constant.Where.WHERE_CLICK_LIST_SONG);
                        songIntent.putExtra(Constant.MUSIC_CLICKED, music);
                        songIntent.putExtra(Constant.LIST_CLICKED, musicList);
                        mShowListPresenter.startMusicActivity(songIntent, getActivity());
                        break;
                }
            }
            if (Constant.Action.ACTION_REFRESH_LIST.equals(action)) {
                mShowListPresenter.refreshList(musicList, getActivity());
            }
        }
    };

    @Override
    public void initRecyclerView(List<Music> music) {
        mMusics = music;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songAdapter = new SongAdapter(mMusics);
        mRecyclerView.setAdapter(songAdapter);
    }

    @Override
    public void setListName(String name) {
        nameView.setText(name);
    }

    @Override
    public void setListCover(Bitmap bm) {
        randomCoverView.setImageBitmap(bm);
    }

    @Override
    public void changeFragment(ShowContract.IChangeFra iChangeFra, Fragment fragment) {
        iChangeFra.change(fragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.as_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.as_play_all:
                new Pool().getCachedThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        Intent musicIntent = new Intent();
                        musicIntent.setAction(Constant.Action.ACTION_START_MUSIC);
                        musicIntent.putExtra(Constant.Where.WHERE, Constant.Where.WHERE_CLICK_LIST_PLAY_ALL);
                        getActivity().sendBroadcast(musicIntent);
                    }
                });
                break;
            case R.id.as_check:
                changeFragment((ShowActivity) getActivity(), new SelectSongFragment());
                break;
        }
    }

}
