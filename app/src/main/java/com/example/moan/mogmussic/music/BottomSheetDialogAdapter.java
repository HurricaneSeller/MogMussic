package com.example.moan.mogmussic.music;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.data.music.Music;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetDialogAdapter extends RecyclerView.Adapter<BottomSheetDialogAdapter.ViewHolder> {
    private List<Music> mMusicList = new ArrayList<>();
    private IChangeSong mIChangeSong;

    public BottomSheetDialogAdapter(List<Music> musicList, IChangeSong IChangeSong) {
        mMusicList = musicList;
        mIChangeSong = IChangeSong;
    }

    @NonNull
    @Override
    public BottomSheetDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_song_item,
                parent, false);
        final BottomSheetDialogAdapter.ViewHolder viewHolder = new BottomSheetDialogAdapter.ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music music = mMusicList.get(viewHolder.getAdapterPosition());
                mIChangeSong.getSong(music);
                mIChangeSong.bottomDialogDismiss();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetDialogAdapter.ViewHolder holder, int position) {
        Music music = mMusicList.get(position);
        holder.titleView.setText(music.getTitle());
        String info = music.getArtist() + " - " + music.getAlbum();
        holder.infoView.setText(info);
        if ((int)music.getId() == mIChangeSong.getMusicId()) {
            holder.titleView.setTextColor(Color.parseColor("#FFA726"));
        } else {
            holder.titleView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return (mMusicList == null) ? 0 :mMusicList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView infoView;
        View mView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.bss_single_song);
            titleView = itemView.findViewById(R.id.bss_title);
            infoView = itemView.findViewById(R.id.bss_info);
        }
    }

    public interface IChangeSong {
        void getSong(Music music);
        int getMusicId();
        void bottomDialogDismiss();
    }
}
