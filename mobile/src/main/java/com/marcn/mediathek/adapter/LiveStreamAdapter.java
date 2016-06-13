package com.marcn.mediathek.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marcn.mediathek.R;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.model.zdf.ZdfLive;
import com.marcn.mediathek.utils.NavigationManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.marcn.mediathek.utils.NavigationManager.PlayerType.EXTERNAL;
import static com.marcn.mediathek.utils.NavigationManager.PlayerType.INTERNAL;

public class LiveStreamAdapter extends RecyclerView.Adapter<LiveStreamAdapter.ViewHolder> {

    private final ArrayList<Stream> mValues;
    private NavigationManager mNavigationManager;

    @Inject
    public LiveStreamAdapter(NavigationManager navigationManager) {
        mNavigationManager = navigationManager;
        mValues = new ArrayList<>();
    }

    public void addValues(List<Stream> streams) {
        mValues.addAll(streams);
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
        Context context = holder.mView.getContext();
        holder.setItem(mValues.get(position));

        String stationTitle = holder.getItem().getStationTitle()
                + context.getString(R.string.livestream_station_live_suffix);
        holder.mStation.setText(stationTitle);
        Glide.with(context)
                .load(holder.getItem().getThumbUrl())
                .placeholder(R.drawable.placeholder_stream2)
                .centerCrop()
                .into(holder.mThumb);
        holder.mTitle.setText(holder.getItem().getStreamTitle());

        if (holder.getItem() instanceof ZdfLive) {
            holder.mTime.setVisibility(View.GONE);
        } else {
            holder.mTime.setText(holder.getItem().getRemainingTime());
            holder.mTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public ArrayList<Stream> getList() {
        return mValues;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.imageThumbnail)
        ImageView mThumb;
        @BindView(R.id.textTitle)
        TextView mTitle;
        @BindView(R.id.textLiveTime)
        TextView mTime;
        @BindView(R.id.textStation)
        TextView mStation;

        private Stream mItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            mView = view;
            mView.setOnClickListener(v -> mNavigationManager.startLiveStream(mItem, INTERNAL));
            mView.setOnLongClickListener(v -> {
                mNavigationManager.startLiveStream(mItem, EXTERNAL);
                return true;
            });
        }

        public Stream getItem() {
            return mItem;
        }

        public void setItem(Stream item) {
            mItem = item;
        }
    }
}