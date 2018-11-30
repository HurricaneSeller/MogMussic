package com.example.moan.mogmussic.show.showmain;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.music.MusicActivity;
import com.example.moan.mogmussic.show.ShowActivity;
import com.example.moan.mogmussic.show.ShowContract;
import com.example.moan.mogmussic.show.showlist.ShowListFragment;
import com.example.moan.mogmussic.show.showsong.ShowSongFragment;
import com.example.moan.mogmussic.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class ShowFragment extends Fragment implements ShowContract.ShowView, View.OnClickListener {
    @BindView(R.id.fra_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.fra_create)
    Button btnCreateList;
    @BindView(R.id.fra_edit)
    EditText searchView;
    @BindView(R.id.fra_search)
    ImageButton btnSearchOnline;
    @BindView(R.id.fra_number)
    TextView numberView;
    @BindView(R.id.fra_local)
    View localView;

    private ShowPresenter mShowPresenter;
    private String TAG = "moanbigking";
    //    private ShowContract.IChangeFragment mIChangeFragment;
    private MusicListAdapter mMusicListAdapter;
    private List<MusicList> mMusicLists = new ArrayList<>();


    public ShowFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowPresenter = new ShowPresenter(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("mog.refresh_list");
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
        Log.d(TAG, "onCreate: register");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        ButterKnife.bind(this, view);
        btnCreateList.setOnClickListener(this);
        localView.setOnClickListener(this);
        new LoadAsyncTask().execute();
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    class LoadAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mShowPresenter.getTotalLists(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initView();
        }
    }

    @Override
    public void setListsNumber(String number) {
        numberView.setText(number);
    }


    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMusicListAdapter = new MusicListAdapter(mMusicLists, new MusicListAdapter.IMusicChosen() {
            @Override
            public void getMusicChosen(MusicList musicListChosen) {
                if (!mShowPresenter.hasPassword(musicListChosen)) {
                    mShowPresenter.changeFragment((ShowActivity) getActivity(), new ShowListFragment());
                } else {
                    mShowPresenter.check(musicListChosen);
                }
            }

            @Override
            public void deleteMusicListChosen(MusicList musicListChosen) {
                mMusicLists.remove(musicListChosen);
                sendRefreshBroadcast();
            }
        });
        mRecyclerView.setAdapter(mMusicListAdapter);
    }

    @Override
    public void setMusicLists(List<MusicList> musicLists) {
        mMusicLists = musicLists;
    }

    @Override
    public void showCheckDialog(final String PASSWORD) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.check_dialog_layout, null);
        final EditText editText = view.findViewById(R.id.check_pswd);
        builder.setView(view)
                .setCancelable(true)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = editText.getText().toString();
                        if (password.equals(PASSWORD)) {
                            mShowPresenter.changeFragment((ShowActivity) getActivity(),
                                    new ShowListFragment());
                        } else {
                            Toast.makeText(getActivity(), Constant.Toast.WRONG_PASSWORD,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create().show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fra_create:
                Log.d(TAG, "onClick: ");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.list_dialog_layout, null);
                final EditText titleView = dialogView.findViewById(R.id.cld_title);
                final CheckBox checkBox = dialogView.findViewById(R.id.cld_check_box);
                final EditText passwordView = dialogView.findViewById(R.id.cld_password);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            passwordView.setVisibility(View.VISIBLE);
                        } else {
                            passwordView.setText("");
                            passwordView.setVisibility(View.GONE);
                        }
                    }
                });
                builder.setView(dialogView)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (checkBox.isChecked()) {
                                    mShowPresenter.createList(titleView.getText().toString(),
                                            passwordView.getText().toString(), getActivity());
                                } else {
                                    mShowPresenter.createList(titleView.getText().toString(), getActivity());
                                }
                                sendRefreshBroadcast();
                            }
                        })
                        .create().show();
                break;
            case R.id.fra_search:
                String input = searchView.getText().toString();
                // TODO: 11/27/18
                break;
            case R.id.fra_local:
                mShowPresenter.changeFragment((ShowActivity)getActivity(), new ShowSongFragment());
                break;
        }
    }

    private void sendRefreshBroadcast() {
        Intent refreshListIntent = new Intent();
        refreshListIntent.setAction("mog.refresh_list");
        getActivity().sendBroadcast(refreshListIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.Action.ACTION_REFRESH_LIST.equals(action)) {
                mMusicListAdapter.notifyDataSetChanged();
            }
        }
    };
}
