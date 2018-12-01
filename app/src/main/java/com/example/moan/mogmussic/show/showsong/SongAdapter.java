package com.example.moan.mogmussic.show.showsong;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;
import com.example.moan.mogmussic.util.Constant;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private List<Music> mMusicList;

    public SongAdapter(List<Music> musicList) {
        mMusicList = musicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song,
                parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Music music = mMusicList.get(position);
                Intent musicIntent = new Intent();
                musicIntent.setAction(Constant.Action.ACTION_START_MUSIC);
                musicIntent.putExtra(Constant.Where.WHERE, Constant.Where.WHERE_CLICK_SONG);
                musicIntent.putExtra(Constant.MUSIC_CLICKED, music);
                parent.getContext().sendBroadcast(musicIntent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = mMusicList.get(position);
        holder.titleView.setText(music.getTitle());
        String info = music.getArtist() + " - " + music.getAlbum();
        holder.infoView.setText(info);
    }

    @Override
    public int getItemCount() {
        return mMusicList == null ? 0 : mMusicList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView infoView;
        View mView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.single_song);
            titleView = itemView.findViewById(R.id.song_title);
            infoView = itemView.findViewById(R.id.song_info);
        }
    }
}
