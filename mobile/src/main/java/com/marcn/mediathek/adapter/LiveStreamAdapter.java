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
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.LiveStreams;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LiveStreamAdapter extends RecyclerView.Adapter<LiveStreamAdapter.ViewHolder> {

    private final LiveStreams mValues;
    private OnVideoInteractionListener mListener;

    public LiveStreamAdapter(LiveStreams items, OnVideoInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

//    public void updateValue(LiveStream l) {
//        int index = mValues.mLiveStreams.indexOf(l);
//        mValues.pushLiveStream(l);
//        if (index < 0)
//            notifyDataSetChanged();
//        else
//            notifyItemChanged(index);
//    }

    public void updateZdfValues(ArrayList<LiveStream> ls) {
        mValues.pushLiveStreams(ls);
        notifyItemRangeChanged(0, 5);
    }

    public void updateArteValue(LiveStream l) {
        mValues.pushLiveStream(l);
        notifyItemChanged(5);
    }

    public void updateArdValues(ArrayList<LiveStream> ls) {
        mValues.pushLiveStreams(ls);
//        notifyDataSetChanged();
        notifyItemRangeChanged(6, ls.size());
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
        final LiveStream liveStream = mValues.get(position);

        // Thumbnail Image
        String thumb = liveStream.getThumb_url();
        if (thumb == null || thumb.isEmpty())
            holder.mThumb.setImageResource(R.drawable.placeholder_stream);
        else
            Picasso.with(context)
                    .load(thumb)
                    .placeholder(R.drawable.placeholder_stream)
                    .fit()
                    .config(Bitmap.Config.RGB_565)
                    .into(holder.mThumb);

        if (liveStream.stationObject != null)
            holder.mStation.setText(liveStream.stationObject.title);

        if (liveStream.getCurrentEpisode() != null) {
            if (holder.mTitle != null)
                holder.mTitle.setText(liveStream.getCurrentEpisode().getTitle());

            if (holder.mTime != null)
                holder.mTime.setText(liveStream.getCurrentEpisode().getRemainingTime());

            int logo = holder.mItem.stationObject.getLogoResId();
            if (holder.mLogo != null) {
                if (logo > 0)
                    holder.mLogo.setImageResource(logo);
                else
                    holder.mLogo.setImageBitmap(null);
            }

            holder.mView.findViewById(R.id.liveInfoContainer).setVisibility(View.VISIBLE);
        } else {
            if (holder.mTitle != null)
                holder.mTitle.setText(null);

            if (holder.mTime != null)
                holder.mTime.setText(null);
            holder.mView.findViewById(R.id.liveInfoContainer).setVisibility(View.GONE);
        }



        // OnClick
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onLiveStreamClicked(liveStream, holder.mThumb, Episode.ACTION_INTERNAL_PLAYER);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.playVideoExternal(liveStream.getLiveM3U8(), holder.mItem.title, Episode.ACTION_SHARE_VIDEO_DIALOG);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Nullable
    public LiveStream getItem(int position) {
        if (position >= getItemCount() || position < 0)
            return null;
        return mValues.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumb;
        public final ImageView mLogo;
//        public final TextView mLogo;
        public final TextView mTitle, mTime, mStation;

        public LiveStream mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
            mLogo = (ImageView) view.findViewById(R.id.imageStationLogo);
            mTitle = (TextView) view.findViewById(R.id.textLiveTitle);
            mTime = (TextView) view.findViewById(R.id.textLiveTime);
            mStation = (TextView) view.findViewById(R.id.textStationName);
        }
    }
}
