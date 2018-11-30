package com.example.moan.mogmussic.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.util.MusicConvert;
import com.example.moan.mogmussic.data.musiclist.MusicList;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PopupWindowListAdapter extends RecyclerView.Adapter<PopupWindowListAdapter.ViewHolder> {
    private List<MusicList> mMusicLists;
    private IAddSongToList mIAddSongToList;

    public PopupWindowListAdapter(List<MusicList> musicLists, IAddSongToList IAddSongToList) {
        mMusicLists = musicLists;
        mIAddSongToList = IAddSongToList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_list,
                parent, false);
        final PopupWindowListAdapter.ViewHolder viewHolder = new PopupWindowListAdapter.ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIAddSongToList.getMusicList(mMusicLists.get(viewHolder.getAdapterPosition()));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicList musicList = mMusicLists.get(position);
        holder.titleView.setText(musicList.getName());
        String count = ((MusicConvert.fromString(musicList.getMusicJsonString())) == null)
                ? "0" : MusicConvert.fromString(musicList.getMusicJsonString()).size() + "";
        String temp = "共" + count + "首";
        holder.numberView.setText(temp);
        if (musicList.isHasPassword()) {
            holder.lockView.setVisibility(View.VISIBLE);
        } else {
            holder.lockView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mMusicLists == null ? 0 : mMusicLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView numberView;
        View mView;
        ImageView lockView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            titleView = itemView.findViewById(R.id.item_title);
            numberView = itemView.findViewById(R.id.item_number);
            lockView = itemView.findViewById(R.id.item_lock);
        }
    }
    public interface IAddSongToList{
        void getMusicList(MusicList musicList);
    }
}