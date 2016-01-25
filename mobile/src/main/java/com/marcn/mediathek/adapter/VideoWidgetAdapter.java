package com.marcn.mediathek.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.util.ArrayList;

public class VideoWidgetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<Video> mValues;
    private final OnVideoInteractionListener mListener;

    public VideoWidgetAdapter(ArrayList<Video> items, OnVideoInteractionListener listener) {
        if (items == null)
            mValues = new ArrayList<>();
        else {
            mValues = items;
        }
        mListener = listener;
    }

    public void updateValues(Video video) {
        mValues.add(video);
        notifyItemChanged(mValues.size()- 1);
    }

    public void updateValues(ArrayList<Video> ls) {
        mValues.addAll(ls);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_widget_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        Video video = mValues.get(position);

            final VideoViewHolder holder = (VideoViewHolder) viewHolder;
            holder.mItem = video;
            holder.mTitle.setText(holder.mItem.title);
            holder.mAirTime.setText(holder.mItem.airtime);
            Context context = holder.mView.getContext();

            // Thumbnail Image
            String thumb = holder.mItem.thumb_url;
            if (thumb == null || thumb.isEmpty())
                holder.mThumb.setImageResource(R.drawable.placeholder_stream);
            else
                Picasso.with(context)
                        .load(thumb)
                        .placeholder(R.drawable.placeholder_stream)
                        .config(Bitmap.Config.RGB_565)
                        .into(holder.mThumb);

            // OnClick
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener)
                        mListener.onVideoClicked(holder.mItem, holder.mThumb, Video.ACTION_INTERNAL_PLAYER);
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onVideoClicked(holder.mItem, holder.mThumb, Video.ACTION_EXTERNAL_PLAYER_DIALOG);
                    return true;
                }
            });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumb;
        public final TextView mTitle, mAirTime;
        public Video mItem;

        public VideoViewHolder(View view) {
            super(view);
            mView = view;
            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mAirTime = (TextView) view.findViewById(R.id.textDate);
        }
    }
}
