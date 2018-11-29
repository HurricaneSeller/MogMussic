package com.example.moan.mogmussic.show.showsong;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.music.MusicActivity;
import com.example.moan.mogmussic.show.ShowActivity;
import com.example.moan.mogmussic.show.ShowContract;
import com.example.moan.mogmussic.show.showmain.ShowFragment;
import com.example.moan.mogmussic.util.Constant;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowSongFragment extends Fragment implements ShowContract.ShowSongsView, View.OnClickListener {
    private static final String TAG = "moanbigking";
    @BindView(R.id.fra_local_back)
    Button btnBack;
    @BindView(R.id.fra_local_play_all)
    ImageButton btnPlayAll;
    @BindView(R.id.fra_local_number)
    TextView numberView;
    @BindView(R.id.fra_local_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.fra_local_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ShowContract.ShowSongsPresenter mShowSongsPresenter;
    private List<Music> mMusics = new ArrayList<>();
    private View popupWindowView;
    private PopupWindow popupWindow;

    public ShowSongFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowSongsPresenter = new ShowSongPresenter(this);
        mShowSongsPresenter.askForPermission(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Action.ACTION_START_MUSIC);
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_song, container, false);
        new LoadAsyncTask().execute();
        ButterKnife.bind(this, view);
        btnBack.setOnClickListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),
                R.color.colorOrange400));
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        return view;
    }

    @Override
    public void setTotalSongNumber(String number) {
        String temp = "(共" + number + "首)";
        numberView.setText(temp);
    }


    @Override
    public void setTotalMusic(List<Music> musics) {
        mMusics = musics;
    }

    private void showScanView() {
        popupWindowView = LayoutInflater.from(getActivity())
                .inflate(R.layout.popup_window_scan, null);
        try {
            ViewGroup parent = (ViewGroup) popupWindowView.getParent();
            parent.setBackgroundResource(android.R.color.transparent);

        } catch (Exception e) {
            e.printStackTrace();
        }

        popupWindow = new PopupWindow(popupWindowView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        new ScanLocalSongAsyncTask().execute();
        popupWindow.showAsDropDown(btnBack);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fra_local_back:
                mShowSongsPresenter.changeFragment((ShowActivity) getActivity(), new ShowFragment());
                break;
            case R.id.fra_local_play_all:
                Intent musicIntent = new Intent();
                musicIntent.setAction(Constant.Action.ACTION_START_MUSIC);
                getActivity().sendBroadcast(musicIntent);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class LoadAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mShowSongsPresenter.getTotalMusic(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: " + mMusics.size());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(new SongAdapter(mMusics));
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ScanLocalSongAsyncTask extends AsyncTask<Void, Void, Void> {
        private View searchView;
        private View finishView;
        private SearchAnimation animation;

        @Override
        protected Void doInBackground(Void... voids) {
            mShowSongsPresenter.scanLocalSong(getActivity());
            mShowSongsPresenter.getTotalMusic(getActivity());
            searchView = popupWindowView.findViewById(R.id.fragment_scan_song_search);
            finishView = popupWindowView.findViewById(R.id.fragment_scan_song_finished);
            animation = new SearchAnimation(50);
            animation.setRepeatCount(1);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(2000);
            searchView.startAnimation(animation);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            animation.cancel();
            finishView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            popupWindow.dismiss();
            super.onPostExecute(aVoid);
            Toast.makeText(getActivity(), "扫描完成", Toast.LENGTH_SHORT).show();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.setRefreshing(false);
            showScanView();
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.Action.ACTION_START_MUSIC.equals(action)) {
                String where = intent.getStringExtra(Constant.Where.WHERE);
                if(Constant.Where.WHERE_CLICK_LOCAL_SONG.equals(where)) {
                    Music music = (Music) intent.getSerializableExtra(Constant.MUSIC_CLICKED);
                    Intent musicIntent = new Intent(getActivity(), MusicActivity.class);
                    musicIntent.putExtra(Constant.Where.WHERE, Constant.Where.WHERE_CLICK_LOCAL_SONG);
                    musicIntent.putExtra(Constant.MUSIC_CLICKED, music);
                    startActivity(musicIntent);
                }
                else if (Constant.Where.WHERE_CLICK_LOCAL_PLAY_ALL.equals(where)) {
                    Intent musicIntent = new Intent(getActivity(), MusicActivity.class);
                    musicIntent.putExtra(Constant.Where.WHERE, Constant.Where.WHERE_CLICK_LOCAL_PLAY_ALL);
                    startActivity(musicIntent);
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
