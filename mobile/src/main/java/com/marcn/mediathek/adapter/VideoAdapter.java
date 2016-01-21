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
import com.marcn.mediathek.utils.DateFormat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_VIDEO = 1;

    private final ArrayList<Video> mValues;
    private final OnVideoInteractionListener mListener;

    public VideoAdapter(ArrayList<Video> items, OnVideoInteractionListener listener) {
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

    public void addHeadline(Calendar calendar) {
        Video v = new Video(DateFormat.calendarToHeadlineFormat(calendar));
        mValues.add(v);
        notifyItemChanged(mValues.size()- 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        Video video = mValues.get(position);
        if (getItemViewType(position) == TYPE_HEADER) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            holder.mDate.setText(video.dayAndDate);
        } else {
            final VideoViewHolder holder = (VideoViewHolder) viewHolder;
            holder.mItem = video;
            holder.mTitle.setText(holder.mItem.detail);
            holder.mAirTime.setText(holder.mItem.airtime);
            holder.mDetail.setText(holder.mItem.title);
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

            // Logo Image
            int logo = holder.mItem.channel.getLogoResId();
            if (logo > 0)
                holder.mLogo.setImageResource(logo);

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
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).isHeader ? TYPE_HEADER : TYPE_VIDEO;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumb, mLogo;
        public final TextView mTitle, mAirTime, mDetail;
        public Video mItem;

        public VideoViewHolder(View view) {
            super(view);
            mView = view;
            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
            mLogo = (ImageView) view.findViewById(R.id.imageLogo);
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mAirTime = (TextView) view.findViewById(R.id.textAirTime);
            mDetail = (TextView) view.findViewById(R.id.textDetail);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDay, mDate;

        public HeaderViewHolder(View view) {
            super(view);
            mView = view;
            mDay = (TextView) view.findViewById(R.id.textDayOfWeek);
            mDate = (TextView) view.findViewById(R.id.textDayOfWeek);
        }
    }
}
