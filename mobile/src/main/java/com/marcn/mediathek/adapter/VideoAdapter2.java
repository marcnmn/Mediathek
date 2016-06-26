package com.marcn.mediathek.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marcn.mediathek.R;
import com.marcn.mediathek.model.zdf.ZdfEpisode;
import com.marcn.mediathek.utils.NavigationManager;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter2 extends RecyclerView.Adapter<VideoAdapter2.SendungViewHolder> {
    private Activity mContext;
    private NavigationManager mNavigationManager;

    private boolean mIsLoading;
    private final ArrayList<ZdfEpisode> mItems;

    @Inject
    public VideoAdapter2(Activity context, NavigationManager navigationManager) {
        mContext = context;
        mNavigationManager = navigationManager;
        mItems = new ArrayList<>();
    }

    public void updateValues(ArrayList<ZdfEpisode> ls) {
        mItems.addAll(ls);
        notifyDataSetChanged();
    }

    private void onItemClicked(int position) {
        mNavigationManager.gotToZdfVideo(mItems.get(position).getId());
    }

    @Override
    public SendungViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_content, parent, false);
        return new SendungViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SendungViewHolder viewHolder, int position) {
        viewHolder.setIsLoading(position >= mItems.size());

        if (position >= mItems.size()) {
            return;
        }

        ZdfEpisode episode = mItems.get(position);
        viewHolder.mTitle.setText(episode.getTitle());
        viewHolder.mVideoInfo.setText(episode.getAirtime());
        viewHolder.mChannel.setText(episode.getStationTitle());

        if (episode.getThumbUrl() != null)
            Glide.with(mContext)
                    .load(episode.getThumbUrl())
                    .placeholder(R.drawable.placeholder_stream)
                    .centerCrop()
                    .into(viewHolder.mThumbnail);
        else
            viewHolder.mThumbnail.setImageDrawable(null);
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    class SendungViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textTitle)
        TextView mTitle;

        @BindView(R.id.textVideoInfo)
        TextView mVideoInfo;

        @BindView(R.id.imageChannel)
        TextView mChannel;

        @BindView(R.id.imageThumbnail)
        ImageView mThumbnail;

        @BindView(R.id.item_episode_progress)
        ProgressBar mProgressBar;

        SendungViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(v -> onItemClicked(getAdapterPosition()));
        }

        void setIsLoading(boolean loading) {
            mTitle.setVisibility(loading ? View.GONE : View.VISIBLE);
            mVideoInfo.setVisibility(loading ? View.GONE : View.VISIBLE);
            mChannel.setVisibility(loading ? View.GONE : View.VISIBLE);
            mThumbnail.setVisibility(loading ? View.GONE : View.VISIBLE);
            mProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }
}
