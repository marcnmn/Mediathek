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
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.stations.Station;

import java.util.ArrayList;

public class LiveStreamAdapter2 extends RecyclerView.Adapter<LiveStreamAdapter2.ViewHolder> {

    private final ArrayList<Station> mValues;
    private OnVideoInteractionListener mListener;

    public LiveStreamAdapter2(ArrayList<Station> items, OnVideoInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void updateStation(Station s, Episode e) {
        if (s == null || e == null) return;
        int index = mValues.indexOf(s);
        if (index >= 0 && index < mValues.size()) {
            mValues.get(index).setCurrentEpisode2(e);
            notifyItemChanged(index);
        }
    }

    public void setVideoClickListener(OnVideoInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_livestream, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mTitle.setText(holder.mItem.station);
        Context context = holder.mView.getContext();
//        final Video liveStream = mValues.get(position);
        if (holder.mItem == null) return;

        if (holder.mStation != null) {
            String string = holder.mItem.getTitle() + " - Jetzt live:";
            holder.mStation.setText(string);
//                int color = holder.mItem.stationObject.getColor(context);
//                holder.mStation.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        Episode current = holder.mItem.getCurrentEpisode2();

        if (current == null || current.getThumbUrl() == null)
            holder.mThumb.setImageResource(R.drawable.placeholder_stream2);
        else {
            String thumb = current.getThumbUrl();
            Glide.with(context)
                    .load(thumb)
                    .placeholder(R.drawable.placeholder_stream2)
                    .centerCrop()
                    .into(holder.mThumb);
        }

        if (holder.mTitle != null && current != null)
            holder.mTitle.setText(current.getTitle());

        if (holder.mTime != null && current != null)
            holder.mTime.setText(current.getRemainingTime());


        // OnClick
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onLiveStreamClicked(holder.mItem.getLiveStream(), holder.mThumb, Episode.ACTION_INTERNAL_PLAYER);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.playVideoExternal(holder.mItem.getLiveStream().getStreamUrl(), holder.mItem.getCurrentEpisode().getTitle(), Episode.ACTION_SHARE_VIDEO_DIALOG);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Nullable
    public Station getItem(int position) {
        if (position >= getItemCount() || position < 0)
            return null;
        return mValues.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumb;
        //        public final TextView mLogo;
        public final TextView mTitle, mTime, mStation;

        public Station mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mTime = (TextView) view.findViewById(R.id.textLiveTime);
            mStation = (TextView) view.findViewById(R.id.textStation);
        }
    }
}
