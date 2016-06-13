package com.marcn.mediathek.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.model.zdf.ZdfEpisode;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;
import java.util.Calendar;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.SendungViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private Context mContext;
    private boolean mIsLoading;
    private ArrayList<ZdfEpisode> mValues;
    private final OnVideoInteractionListener mListener;

    public VideoAdapter(ArrayList<ZdfEpisode> items, OnVideoInteractionListener onVideoInteractionListener) {
        if (items == null)
            mValues = new ArrayList<>();
        else
            mValues = items;

        mListener = onVideoInteractionListener;
        notifyDataSetChanged();
    }

    public void updateValues(ArrayList<ZdfEpisode> ls) {
        mValues.addAll(ls);
//        mValues = DataUtils.addDateHeaders(mValues);
        notifyDataSetChanged();
    }

    public void updateValues(ZdfEpisode ls) {
        mValues.add(ls);
        notifyDataSetChanged();
    }

    public void setLoading(boolean b) {
        mIsLoading = b;
        try {
            notifyDataSetChanged();
        } catch (IllegalStateException ignored) {
        }
    }

    public void addHeaders() {
        notifyDataSetChanged();
    }

    @Nullable
    public ZdfEpisode getItem(int position) {
        if (position < 0 || position >= mValues.size())
            return null;
        if (mValues.get(position).isHeader())
            return getItem(position + 1);
        return mValues.get(position);
    }

    @Nullable
    public String getMember(int position) {
        if (position < 0 || position >= mValues.size())
            return null;
        return mValues.get(position).getAirtime();
    }

    @Override
    public SendungViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        mContext = parent.getContext();

        if (viewType == VIEW_TYPE_LOADING)
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
        else if (viewType == VIEW_TYPE_HEADER)
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video_header, parent, false);
        else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video_content, parent, false);
        return new SendungViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (mIsLoading && position == getItemCount() - 1)
            return VIEW_TYPE_LOADING;
        return mValues.get(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(final SendungViewHolder viewHolder, int position) {
        if (mIsLoading && position == getItemCount() - 1) {
            GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(viewHolder.mView.getLayoutParams());
            lp.setFirstPosition(getFirstSectionPosition(position));
            viewHolder.mView.setLayoutParams(lp);
            return;
        }

        final ZdfEpisode item = mValues.get(position);
        View itemView = viewHolder.mView;
        viewHolder.mItem = item;

        if (viewHolder.mTitle != null)
            viewHolder.mTitle.setText(item.getTitle());

        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
            if (viewHolder.mVideoInfo != null)
                viewHolder.mVideoInfo.setText(item.getAirtime());

            if (item.getThumbUrl() != null)
                Glide.with(mContext)
                        .load(item.getThumbUrl())
                        .placeholder(R.drawable.placeholder_stream)
                        .centerCrop()
                        .into(viewHolder.mThumbnail);
            else
                viewHolder.mThumbnail.setImageDrawable(null);

            if (viewHolder.mChannel != null)
                viewHolder.mChannel.setText(item.getStationTitle());

//            Station station = viewHolder.mItem.getStation();
//            if (station != null && viewHolder.mChannel != null) {
//                viewHolder.mChannel.setText(station.getTitle());
//                int color = station.getColor(mContext);
//                viewHolder.mChannel.getBackground().setColorFilter(color, PorterDuff.Mode.OVERLAY);
//            }
//            if (item.station != null)
//                viewHolder.mChannel.setImageResource(item.station.getLogoResId());
//            else
//                viewHolder.mChannel.setImageDrawable(null);
        }

        GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
        lp.setSlm(LinearSLM.ID);
        lp.setFirstPosition(getFirstSectionPosition(position));
        itemView.setLayoutParams(lp);

        viewHolder.mView.setOnClickListener(v -> {
//                if (mListener != null)
//                    mListener.onVideoClicked(viewHolder.mItem, viewHolder.mThumbnail, Episode.ACTION_INTERNAL_PLAYER);
        });

        viewHolder.mView.setOnLongClickListener((View.OnLongClickListener) v -> {
//            if (mListener != null)
//                    mListener.onVideoClicked(viewHolder.mItem, viewHolder.mThumbnail, Episode.ACTION_SHARE_VIDEO_DIALOG);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mIsLoading ? mValues.size() + 1 : mValues.size();
    }

    private int getFirstSectionPosition(int position) {
        if (mIsLoading && getItemViewType(position) == VIEW_TYPE_LOADING) return 0;
        for (int i = position; i >= 0; i--)
            if (mValues.get(i).isHeader())
                return i;
        return 0;
    }

    public void addHeader(Calendar mDay) {
        if (mValues.get(mValues.size() - 1).isHeader())
            mValues.remove(mValues.size() - 1);
//        Episode.addHeader(mValues, mDay);
        notifyItemInserted(mValues.size() - 1);
    }

    public class SendungViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle, mVideoInfo, mChannel;
        public final ImageView mThumbnail;
        public ZdfEpisode mItem;

        public SendungViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mVideoInfo = (TextView) view.findViewById(R.id.textVideoInfo);
            mThumbnail = (ImageView) view.findViewById(R.id.imageThumbnail);
            mChannel = (TextView) view.findViewById(R.id.imageChannel);
        }
    }
}
