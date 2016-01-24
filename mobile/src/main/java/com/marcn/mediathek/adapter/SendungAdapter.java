package com.marcn.mediathek.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.Sendung;
import com.squareup.picasso.Picasso;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.io.Console;
import java.util.ArrayList;

public class SendungAdapter extends RecyclerView.Adapter<SendungAdapter.SendungViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private static final int LINEAR = 0;
    private int sectionManager = 0;
    private Context mContext;
    private boolean mIsLoading;

    private final ArrayList<Sendung> mValues;
    private final OnVideoInteractionListener mListener;

    public SendungAdapter(ArrayList<Sendung> items, OnVideoInteractionListener onVideoInteractionListener) {
        if (items == null)
            mValues = new ArrayList<>();
        else
            mValues = items;

        mListener = onVideoInteractionListener;


        notifyDataSetChanged();
    }

    public void updateValues(ArrayList<Sendung> ls) {
        mValues.addAll(ls);
        notifyDataSetChanged();
    }

    public void setLoading(boolean b) {
        mIsLoading = b;
        notifyDataSetChanged();
    }

    public String getMember(int position) {
        return mValues.get(position).member;
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
                    .inflate(R.layout.item_sendung_header, parent, false);
        else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sendung_content, parent, false);
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

        final Sendung item = mValues.get(position);
        View itemView = viewHolder.mView;

        viewHolder.mItem = item;

        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
            if (item.thumb_url != null)
                Picasso.with(mContext)
                        .load(item.thumb_url)
                        .placeholder(R.drawable.placeholder_stream)
                        .config(Bitmap.Config.RGB_565)
                        .fit()
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
        viewHolder.mTitle.setText(item.title);
        lp.setFirstPosition(getFirstSectionPosition(position));
        itemView.setLayoutParams(lp);

        viewHolder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onSendungClicked(viewHolder.mItem, viewHolder.mThumbnail);
            }
        });

        if (viewHolder.mChannel != null)
            viewHolder.mChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onChannelClicked(viewHolder.mItem.channel, viewHolder.mChannel);
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
        public final TextView mTitle;
        public final ImageView mThumbnail, mChannel;
        public Sendung mItem;

        public SendungViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mThumbnail = (ImageView) view.findViewById(R.id.imageThumbnail);
            mChannel = (ImageView) view.findViewById(R.id.imageChannel);
        }
    }
}
