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
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.base_objects.LiveStreams;
import com.marcn.mediathek.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LiveStreamAdapter {
//
//    private final LiveStreams mValues;
//    private OnVideoInteractionListener mListener;
//
//    public LiveStreamAdapter(LiveStreams items, OnVideoInteractionListener listener) {
//        mValues = items;
//        mListener = listener;
//    }
//
////    public void updateValue(Video l) {
////        int index = mValues.mVideos.indexOf(l);
////        mValues.pushLiveStream(l);
////        if (index < 0)
////            notifyDataSetChanged();
////        else
////            notifyItemChanged(index);
////    }
//
//    public void updateZdfValues(ArrayList<Video> ls) {
//        mValues.pushLiveStreams(ls);
//        notifyItemRangeChanged(0, 5);
//    }
//
//    public void updateArteValue(Video l) {
//        mValues.pushLiveStream(l);
//        notifyItemChanged(5);
//    }
//
//    public void updateArdValues(ArrayList<Video> ls) {
//        mValues.pushLiveStreams(ls);
////        notifyDataSetChanged();
//        notifyItemRangeChanged(6, ls.size());
//    }
//
//    public void setVideoClickListener(OnVideoInteractionListener listener) {
//        mListener = listener;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_livestream, parent, false);
//
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
//        //holder.mTitle.setText(holder.mItem.station);
//        Context context = holder.mView.getContext();
////        final Video liveStream = mValues.get(position);
//
//        // Thumbnail Image
//        String thumb = holder.mItem.getThumb_url();
//        if (thumb == null || thumb.isEmpty())
//            holder.mThumb.setImageResource(R.drawable.placeholder_stream);
//        else
//            Picasso.with(context)
//                    .load(thumb)
//                    .placeholder(R.drawable.placeholder_stream)
//                    .config(Bitmap.Config.RGB_565)
//                    .resize(Constants.SIZE_THUMB_MEDIUM_X, Constants.SIZE_THUMB_MEDIUM_Y)
//                    .onlyScaleDown()
//                    .centerCrop()
//                    .into(holder.mThumb);
//
//        if (holder.mItem.stationObject != null) {
//            if (holder.mStation != null) {
//                holder.mStation.setText(holder.mItem.stationObject.getTitle() + " - Jetzt live:");
////                int color = holder.mItem.stationObject.getColor(context);
////                holder.mStation.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
//            }
//
//            if (holder.mItem.getCurrentEpisode() != null) {
//                if (holder.mTitle != null)
//                    holder.mTitle.setText(holder.mItem.getCurrentEpisode().getTitle());
//
//                if (holder.mTime != null)
//                    holder.mTime.setText(holder.mItem.getCurrentEpisode().getRemainingTime());
//            }
//
////            holder.mView.findViewById(R.id.liveInfoContainer).setVisibility(View.VISIBLE);
//        } else {
//            if (holder.mTitle != null)
//                holder.mTitle.setText(null);
//
//            if (holder.mTime != null)
//                holder.mTime.setText(null);
//        }
//
//        // OnClick
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    mListener.onLiveStreamClicked(holder.mItem, holder.mThumb, Episode.ACTION_INTERNAL_PLAYER);
//                }
//            }
//        });
//
//        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                mListener.playVideoExternal(holder.mItem.getLiveM3U8(), holder.mItem.title, Episode.ACTION_SHARE_VIDEO_DIALOG);
//                return true;
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mValues.size();
//    }
//
//    @Nullable
//    public Video getItem(int position) {
//        if (position >= getItemCount() || position < 0)
//            return null;
//        return mValues.get(position);
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public final View mView;
//        public final ImageView mThumb;
//        //        public final TextView mLogo;
//        public final TextView mTitle, mTime, mStation;
//
//        public Video mItem;
//
//        public ViewHolder(View view) {
//            super(view);
//            mView = view;
//            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
//            mTitle = (TextView) view.findViewById(R.id.textTitle);
//            mTime = (TextView) view.findViewById(R.id.textLiveTime);
//            mStation = (TextView) view.findViewById(R.id.textStation);
//        }
//    }
}
