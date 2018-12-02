package com.example.moan.mogmussic.online;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.gson.OnlineSong;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnlineSongAdapter extends RecyclerView.Adapter<OnlineSongAdapter.ViewHolder> {
    private List<OnlineSong> mOnlineSongs;
    private OAPresenter.ImageCache mImageCache;
    private IHelper mIHelper;

    OnlineSongAdapter(List<OnlineSong> onlineSongs, OAPresenter.ImageCache imageCache,
                      IHelper iHelper) {
        mOnlineSongs = onlineSongs;
        mImageCache = imageCache;
        mIHelper = iHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_online_song,
                parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIHelper.downloadSong(mOnlineSongs.get(viewHolder.getAdapterPosition()));
            }
        });
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIHelper.playSong(mOnlineSongs.get(viewHolder.getAdapterPosition()) );
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnlineSong onlineSong = mOnlineSongs.get(position);
        holder.coverView.setImageBitmap(mImageCache.getBitmap(onlineSong.getPic()));
        holder.artistView.setText(onlineSong.getAuthor());
        holder.titleView.setText(onlineSong.getTitle());
    }

    @Override
    public int getItemCount() {
        return mOnlineSongs == null ? 0 : mOnlineSongs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverView;
        TextView titleView;
        TextView artistView;
        ImageButton downloadButton;
        View mView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            coverView = itemView.findViewById(R.id.online_cover);
            titleView = itemView.findViewById(R.id.online_title);
            artistView = itemView.findViewById(R.id.online_artist);
            downloadButton = itemView.findViewById(R.id.online_download);
        }
    }
    interface IHelper {
        void downloadSong(OnlineSong onlineSong);
        void playSong(OnlineSong onlineSong);
    }
}
