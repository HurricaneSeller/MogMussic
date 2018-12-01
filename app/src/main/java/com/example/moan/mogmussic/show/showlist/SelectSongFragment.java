package com.example.moan.mogmussic.show.showlist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.show.ShowActivity;
import com.example.moan.mogmussic.show.ShowContract;
import com.example.moan.mogmussic.util.Constant;
import com.example.moan.mogmussic.util.Pool;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectSongFragment extends Fragment implements View.OnClickListener, ShowContract.SelectView {
    @BindView(R.id.fra_select_back)
    ImageButton btnBack;
    @BindView(R.id.fra_select_text)
    TextView numberView;
    @BindView(R.id.fra_select_all)
    Button btnSelectAll;
    @BindView(R.id.fra_select_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.fra_select_floating_button)
    FloatingActionButton btnCheck;
    @BindView(R.id.fs_progress_bar)
    ProgressBar mProgressBar;

    private HashMap<Integer, Boolean> isSelectHashMap = new HashMap<>();
    private SelectPresenter mSelectPresenter;
    private String TAG = "moanbigking";
    private MusicList musicList;
    private MyAdapter myAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectPresenter = new SelectPresenter(this);
        musicList = mSelectPresenter.getMusicList((ShowActivity) getActivity());
        mSelectPresenter.loadLocalSong(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);
        ButterKnife.bind(this, view);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFA726"),
                PorterDuff.Mode.SRC_IN);
        btnBack.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        btnSelectAll.setOnClickListener(this);
        setNumberView(0);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fra_select_back:
                popBackStack();
                break;
            case R.id.fra_select_floating_button:
                mSelectPresenter.addSelectedSongs(isSelectHashMap, getActivity(), musicList);
                new Pool().getCachedThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().sendBroadcast(new Intent()
                                .setAction(Constant.Action.ACTION_REFRESH_LIST));
                    }
                });
                popBackStack();
                break;
            case R.id.fra_select_all:
                // TODO: 12/1/18  
                break;
        }
    }

    private void popBackStack() {
        getFragmentManager().popBackStack();
    }


    @Override
    public void toastLoadFinished() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void toastStartInsert() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void initRecyclerView(List<Music> music) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myAdapter = new MyAdapter(music);
        mRecyclerView.setAdapter(myAdapter);
    }

    @Override
    public void setNumberView(int selectNumber) {
        String temp = "已选择" + selectNumber + "首";
        numberView.setText(temp);
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private List<Music> mMusicList;

        MyAdapter(List<Music> musicList) {
            mMusicList = musicList;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_song_item,
                    parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.checkBoxView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Music music = mMusicList.get(viewHolder.getAdapterPosition());
                    isSelectHashMap.remove((int) music.getId());
                    isSelectHashMap.put((int) music.getId(), viewHolder.checkBoxView.isChecked());
                    Set<Map.Entry<Integer, Boolean>> temp = isSelectHashMap.entrySet();
                    int i = 0;
                    for (Map.Entry<Integer, Boolean> entry : temp) {
                        if (entry.getValue()) {
                            i++;
                        }
                    }
                    String temp2 = "已选择" + i + "首";
                    numberView.setText(temp2);

                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Music music = mMusicList.get(position);
            holder.titleView.setText(music.getTitle());
            String temp = music.getArtist() + " - " + music.getAlbum();
            holder.infoView.setText(temp);
        }

        @Override
        public int getItemCount() {
            return mMusicList == null ? 0 : mMusicList.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox checkBoxView;
            TextView titleView;
            TextView infoView;
            View mView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView.findViewById(R.id.inner_single_song);
                checkBoxView = itemView.findViewById(R.id.inner_ss_check_box);
                titleView = itemView.findViewById(R.id.inner_ss_title);
                infoView = itemView.findViewById(R.id.inner_ss_info);
            }
        }
    }



    @Override
    public void onDestroy() {
//        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

//    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(final Context context, Intent intent) {
//            String action = intent.getAction();
//            if (Constant.Action.ACTION_CHANGE_FRAGMENT_ANOTHER.equals(action)) {
//                musicList = (MusicList) intent.getSerializableExtra(Constant.LIST_CLICKED);
////                alreadyCollectedSongs = MusicUtil.fromString(musicList.getMusicJsonString());
//                Message message = new Message();
//                message.what = 1;
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("test", musicList);
//                message.setData(bundle);
//                mSelectPresenter.mHandler.sendMessage(message);
//
//                setNumberView(0);
//                mSelectPresenter.loadLocalSong(getActivity());
//            }
//        }
//    };

}
