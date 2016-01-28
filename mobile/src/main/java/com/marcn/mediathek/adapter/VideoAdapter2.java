package com.marcn.mediathek.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.Video;
import com.squareup.picasso.Picasso;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;

public class VideoAdapter2 extends RecyclerView.Adapter<VideoAdapter2.SendungViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private Context mContext;
    private boolean mIsLoading;
    private final ArrayList<Video> mValues;
    private final OnVideoInteractionListener mListener;

    public VideoAdapter2(ArrayList<Video> items, OnVideoInteractionListener onVideoInteractionListener) {
        if (items == null)
            mValues = new ArrayList<>();
        else
            mValues = items;

        mListener = onVideoInteractionListener;
        notifyDataSetChanged();
    }

    public void updateValues(ArrayList<Video> ls) {
        mValues.addAll(ls);
        notifyDataSetChanged();
    }

    public void updateValues(Video ls) {
        mValues.add(ls);
        notifyDataSetChanged();
    }

    public void setLoading(boolean b) {
        mIsLoading = b;
        notifyDataSetChanged();
    }

    @Nullable
    public String getMember(int position) {
        if (position < 0 || position >= mValues.size())
            return null;
        return mValues.get(position).airtime;
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
        return mValues.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(final SendungViewHolder viewHolder, int position) {
        if (mIsLoading && position == getItemCount() - 1) {
            GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(viewHolder.mView.getLayoutParams());
            lp.setFirstPosition(getFirstSectionPosition(position));
            viewHolder.mView.setLayoutParams(lp);
            return;
        }

        final Video item = mValues.get(position);
        View itemView = viewHolder.mView;
        viewHolder.mItem = item;

        if(viewHolder.mTitle != null)
            viewHolder.mTitle.setText(item.title);

        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
            if(viewHolder.mVideoInfo != null)
                viewHolder.mVideoInfo.setText("Sendezeit: " + item.getAirtime());

            if (item.thumb_url != null)
                Picasso.with(mContext)
                        .load(item.thumb_url)
                        .placeholder(R.drawable.placeholder_stream)
                        .config(Bitmap.Config.RGB_565)
                        .into(viewHolder.mThumbnail);
            else
                viewHolder.mThumbnail.setImageDrawable(null);

            if (item.channel != null)
                viewHolder.mChannel.setImageResource(item.channel.getLogoResId());
            else
                viewHolder.mChannel.setImageDrawable(null);
        }

        GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
        lp.setSlm(LinearSLM.ID);
        lp.setFirstPosition(getFirstSectionPosition(position));
        itemView.setLayoutParams(lp);

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onVideoClicked(viewHolder.mItem, viewHolder.mThumbnail, Video.ACTION_INTERNAL_PLAYER);
            }
        });

        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null)
                    mListener.onVideoClicked(viewHolder.mItem, viewHolder.mThumbnail, Video.ACTION_SHARE_VIDEO_DIALOG);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIsLoading ? mValues.size() + 1 : mValues.size();
    }

    private int getFirstSectionPosition(int position) {
        if (mIsLoading && getItemViewType(position) == VIEW_TYPE_LOADING) return 0;
        for (int i = position; i >= 0; i--)
            if (mValues.get(i).isHeader)
                return i;
        return 0;
    }

    public class SendungViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle, mVideoInfo;
        public final ImageView mThumbnail, mChannel;
        public Video mItem;

        public SendungViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mVideoInfo = (TextView) view.findViewById(R.id.textVideoInfo);
            mThumbnail = (ImageView) view.findViewById(R.id.imageThumbnail);
            mChannel = (ImageView) view.findViewById(R.id.imageChannel);
        }
    }
}
