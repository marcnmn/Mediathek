package com.marcn.mediathek.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.base.SortableDragAdapter;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.utils.Anims;
import com.marcn.mediathek.utils.NavigationManager;
import com.marcn.mediathek.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.marcn.mediathek.utils.NavigationManager.PlayerType.INTERNAL;

public class LiveStreamAdapter extends SortableDragAdapter<Stream, LiveStreamAdapter.ViewHolder> {
    private static final String PREF_LIVESTREAM_ORDER = "livestream-order";

    private PreferenceManager mPreferenceManager;
    private NavigationManager mNavigationManager;
    private View mLastDraggedView;

    @Inject
    public LiveStreamAdapter(NavigationManager navigationManager, PreferenceManager preferenceManager) {
        mNavigationManager = navigationManager;
        mPreferenceManager = preferenceManager;
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
        holder.setItem(mItems.get(position));

        String stationTitle = holder.getItem().getStationTitle() + context.getString(R.string.livestream_station_live_suffix);
        holder.mStation.setText(stationTitle);
        Glide.with(context)
                .load(holder.getItem().getThumbUrl())
                .placeholder(R.drawable.placeholder_stream2)
                .centerCrop()
                .into(holder.mThumb);
        holder.mTitle.setText(holder.getItem().getStreamTitle());

        if (holder.getItem().getRemainingTime() == null) {
            holder.mTime.setVisibility(View.GONE);
        } else {
            holder.mTime.setText(holder.getItem().getRemainingTime());
            holder.mTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void saveItemOrder() {
        mKeyList = new ArrayList<>();
        for (Stream stream : mItems) {
            mKeyList.add(stream.getStationTitle());
        }
        mPreferenceManager.saveObjectAsJSON(PREF_LIVESTREAM_ORDER, mKeyList);
    }

    @Override
    public void sortItemList() {
        mKeyList = mPreferenceManager.getStringArray(PREF_LIVESTREAM_ORDER);
        if (mKeyList == null) {
            return;
        }
        Collections.sort(mItems, (a, b) -> Integer
                .compare(mKeyList.indexOf(a.getStationTitle()), mKeyList.indexOf(b.getStationTitle())));
        notifyDataSetChanged();
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        switch (actionState) {
            case ItemTouchHelper.ACTION_STATE_DRAG:
                mLastDraggedView = viewHolder != null ? viewHolder.itemView : null;
                Anims.elevateUp(mLastDraggedView);
                break;
            default:
                if (mLastDraggedView != null) {
                    Anims.elevateDown(mLastDraggedView);
                }
                break;
        }
    }

    public ArrayList<Stream> getList() {
        return mItems;
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
        }

        public Stream getItem() {
            return mItem;
        }

        public void setItem(Stream item) {
            mItem = item;
        }
    }
}