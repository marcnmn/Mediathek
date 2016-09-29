package com.marcn.mediathek.views.sidescroller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marcn.mediathek.R;
import com.marcn.mediathek.model.base.Episode;

import java.util.List;

public class SideScrollerAdapter extends RecyclerView.Adapter<SideScrollerAdapter.EpisodeViewHolder> {
    private final List<Episode> mValues;
    private final Context mContext;

    public SideScrollerAdapter(List<Episode> values, Context context) {
        mValues = values;
        mContext = context;
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_widget_video, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        holder.mTitle.setText(mValues.get(position).getTitle());
        Glide.with(mContext)
                .load(mValues.get(position).getThumbUrl())
                .centerCrop()
                .into(holder.mThumb);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mThumb;
        public final TextView mTitle, mAirTime;

        public EpisodeViewHolder(View view) {
            super(view);
            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mAirTime = (TextView) view.findViewById(R.id.textDate);
        }
    }
}
