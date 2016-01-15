package com.marcn.mediathek.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.ui_fragments.LiveStreamFragment.OnListFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class LiveStreamAdapter extends RecyclerView.Adapter<LiveStreamAdapter.ViewHolder> {

    private final List<LiveStream> mValues;
    private final OnListFragmentInteractionListener mListener;

    public LiveStreamAdapter(List<LiveStream> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void updateValue(LiveStream ls) {
        mValues.add(ls);
        notifyDataSetChanged();
    }

    public void updateValues(ArrayList<LiveStream> ls) {
        mValues.addAll(ls);
        notifyDataSetChanged();
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

        holder.mTitle.setText(holder.mItem.title);

        if (holder.mItem.thumb_url == null || holder.mItem.thumb_url.isEmpty())
            holder.mThumb.setImageResource(R.drawable.placeholder_stream);
        else
            Picasso.with(holder.mThumb.getContext())
                    .load(holder.mItem.thumb_url)
                    .placeholder(R.drawable.placeholder_stream)
                    .config(Bitmap.Config.RGB_565)
                    .into(holder.mThumb);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumb;
        public final TextView mTitle;
        public LiveStream mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
            mTitle = (TextView) view.findViewById(R.id.textTitle);
        }
    }
}
