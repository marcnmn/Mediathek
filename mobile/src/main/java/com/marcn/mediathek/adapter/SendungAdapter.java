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
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.utils.DataUtils;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;

public class SendungAdapter extends RecyclerView.Adapter<SendungAdapter.SendungViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private Context mContext;
    private boolean mIsLoading;
    private final ArrayList<Series> shownValues = new ArrayList<>();

    private ArrayList<Series> mValues;
    private OnVideoInteractionListener mListener;

    public SendungAdapter() {
    }

    public SendungAdapter(ArrayList<Series> items, OnVideoInteractionListener onVideoInteractionListener) {
        if (items == null)
            mValues = new ArrayList<>();
        else
            mValues = items;

        shownValues.clear();
        shownValues.addAll(mValues);
        mListener = onVideoInteractionListener;
        notifyDataSetChanged();
    }

    public void updateValues(ArrayList<Series> ls) {
        mValues.addAll(ls);
        shownValues.clear();
        shownValues.addAll(mValues);
        notifyDataSetChanged();
    }

    public void setLoading(boolean b) {
        mIsLoading = b;
        notifyDataSetChanged();
    }

    @Nullable
    public String getMember(int position) {
        if (position < 0 || position >= shownValues.size())
            return null;
        return shownValues.get(position).member;
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
        return shownValues.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(final SendungViewHolder viewHolder, int position) {
        if (mIsLoading && position == getItemCount() - 1) {
            GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(viewHolder.mView.getLayoutParams());
            lp.setFirstPosition(getFirstSectionPosition(position));
            viewHolder.mView.setLayoutParams(lp);
            return;
        }

        final Series item = shownValues.get(position);
        View itemView = viewHolder.mView;
        if (item.isHidden())
            viewHolder.mView.setVisibility(View.GONE);
        else
            viewHolder.mView.setVisibility(View.VISIBLE);

        viewHolder.mItem = item;

        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
            if (item.thumb_url_low != null)
                Glide.with(mContext)
                        .load(item.thumb_url_low)
                        .placeholder(R.drawable.placeholder_stream)
                        .centerCrop()
                        .into(viewHolder.mThumbnail);
            else
                viewHolder.mThumbnail.setImageDrawable(null);

            if (viewHolder.mChannel != null)
                viewHolder.mChannel.setText(item.station.title);

//            if (item.station != null)
//                viewHolder.mChannel.setImageResource(item.station.getLogoResId());
//            else
//                viewHolder.mChannel.setImageDrawable(null);
        }

        GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
        lp.setSlm(LinearSLM.ID);
        viewHolder.mTitle.setText(item.title);
        lp.setFirstPosition(getFirstSectionPosition(position));
        itemView.setLayoutParams(lp);

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onSendungClicked(viewHolder.mItem, viewHolder.mThumbnail, viewHolder.mChannel);
            }
        });

        if (viewHolder.mChannel != null)
            viewHolder.mChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onChannelClicked(viewHolder.mItem.station, viewHolder.mChannel);
                }
            });
    }

    @Override
    public int getItemCount() {
        return mIsLoading ? shownValues.size() + 1 : shownValues.size();
    }

    private int getFirstSectionPosition(int position) {
        if (mIsLoading && getItemViewType(position) == VIEW_TYPE_LOADING) return 0;
        for (int i = position; i >= 0; i--)
            if (shownValues.get(i).isHeader)
                return i;
        return 0;
    }

    public void searchAndFilter(String query) {
        shownValues.clear();
        shownValues.addAll(mValues);
        DataUtils.searchSeriesList(shownValues, query);
        DataUtils.filterByHidden(shownValues);
        notifyDataSetChanged();
    }

    public void forceUpdateAll(ArrayList<Series> sendungen) {
        mValues.clear();
        updateValues(sendungen);
    }

    public class SendungViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle, mChannel;
        public final ImageView mThumbnail;
        public Series mItem;

        public SendungViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mThumbnail = (ImageView) view.findViewById(R.id.imageThumbnail);
            mChannel = (TextView) view.findViewById(R.id.imageChannel);
        }
    }
}
