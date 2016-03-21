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
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SeriesWidgetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<Series> mValues;
    private final OnVideoInteractionListener mListener;

    public SeriesWidgetAdapter(ArrayList<Series> items, OnVideoInteractionListener listener) {
        if (items == null)
            mValues = new ArrayList<>();
        else {
            mValues = items;
        }
        mListener = listener;
    }

    public void updateValues(Series episode) {
        mValues.add(episode);
        notifyItemChanged(mValues.size()- 1);
    }

    public void updateValues(ArrayList<Series> ls) {
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
        Series series = mValues.get(position);

            final VideoViewHolder holder = (VideoViewHolder) viewHolder;
            holder.mItem = series;
            holder.mTitle.setText(holder.mItem.title);
            holder.mAirTime.setText(holder.mItem.getEpisodesInfo());
            Context context = holder.mView.getContext();

            // Thumbnail Image
            String thumb = holder.mItem.getThumb_url();
            if (thumb == null || thumb.isEmpty())
                holder.mThumb.setImageResource(R.drawable.placeholder_stream);
            else
                Picasso.with(context)
                        .load(thumb)
                        .placeholder(R.drawable.placeholder_stream)
                        .config(Bitmap.Config.RGB_565)
                        .resize(Constants.SIZE_THUMB_MEDIUM_X, Constants.SIZE_THUMB_MEDIUM_Y)
                        .onlyScaleDown()
                        .centerCrop()
                        .into(holder.mThumb);

            // OnClick
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener)
                        mListener.onSendungClicked(holder.mItem, holder.mThumb, null);
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
        public Series mItem;

        public VideoViewHolder(View view) {
            super(view);
            mView = view;
            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mAirTime = (TextView) view.findViewById(R.id.textDate);
        }
    }
}
