package com.example.moan.mogmussic.show.showmain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.util.MusicUtil;
import com.example.moan.mogmussic.data.musiclist.MusicList;
import com.example.moan.mogmussic.data.musiclist.MusicListDatabase;
import com.example.moan.mogmussic.util.Pool;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private static final String TAG = "moanbigking";
    private List<MusicList> mMusicListList;
    private IMusicChosen mIChangeFragment;

    public MusicListAdapter(List<MusicList> musicListList, IMusicChosen iChangeFragment) {
        mMusicListList = musicListList;
        mIChangeFragment = iChangeFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_list,
                parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIChangeFragment.enterMusicListChosenPage(mMusicListList.get(viewHolder.getAdapterPosition()));
            }
        });
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                final MusicList musicList = mMusicListList.get(position);
                new Pool().getSingleThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        MusicListDatabase.getInstance(parent.getContext()).musicListDao()
                                .delete(musicList);
                        mIChangeFragment.deleteMusicListChosen(musicList);
                    }
                });
            }
        });
        viewHolder.clockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIChangeFragment.setClockPlayingChosenListMusics(mMusicListList.get(viewHolder.getAdapterPosition()));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicList musicList = mMusicListList.get(position);
        holder.titleView.setText(musicList.getName());
        String count = ((MusicUtil.fromString(musicList.getMusicJsonString())) == null)
                ? "0" : MusicUtil.fromString(musicList.getMusicJsonString()).size() + "";
        String temp = "共" + count + "首";
        holder.numberView.setText(temp);
        if (musicList.isHasPassword()) {
            holder.lockButton.setVisibility(View.VISIBLE);
        } else {
            holder.lockButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (mMusicListList == null) ? 0 : mMusicListList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView numberView;
        ImageButton deleteButton;
        View mView;
        ImageButton lockButton;
        ImageButton clockButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            titleView = itemView.findViewById(R.id.item_title);
            numberView = itemView.findViewById(R.id.item_number);
            deleteButton = itemView.findViewById(R.id.item_delete);
            lockButton = itemView.findViewById(R.id.item_lock);
            clockButton = itemView.findViewById(R.id.item_clock);
        }
    }

    public interface IMusicChosen {
        void enterMusicListChosenPage(MusicList musicListChosen);

        void deleteMusicListChosen(MusicList musicListChosen);

        void setClockPlayingChosenListMusics(MusicList musicListChosen);
    }
}
