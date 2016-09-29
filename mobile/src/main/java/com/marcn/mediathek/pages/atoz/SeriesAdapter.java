package com.marcn.mediathek.pages.atoz;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.marcn.mediathek.R;
import com.marcn.mediathek.model.zdf.ZdfSeries;
import com.marcn.mediathek.utils.NavigationManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SendungViewHolder> {

    private Activity mContext;
    private NavigationManager mNavigationManager;

    private List<ZdfSeries> mValues = new ArrayList<>();

    @Inject
    SeriesAdapter(Activity context, NavigationManager navigationManager) {
        mContext = context;
        mNavigationManager = navigationManager;
    }

    void updateValues(List<ZdfSeries> series) {
        mValues.addAll(series);
        notifyDataSetChanged();
    }

    @Override
    public SendungViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sendung_content, parent, false);
        return new SendungViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SendungViewHolder holder, int position) {
        ZdfSeries series = mValues.get(position);

        holder.mTitle.setText(series.getTitle());
        Glide.with(mContext)
                .load(series.getThumbUrl())
                .asBitmap()
                .into(new BitmapImageViewTarget(holder.mThumbnail) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        new Palette.Builder(bitmap).generate(palette ->
                                holder.mTitle.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY)));
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void onSeriesClicked(int position) {
        ZdfSeries series = mValues.get(position);
        Snackbar.make(mContext.findViewById(android.R.id.content), series.getTitle(), Snackbar.LENGTH_LONG).show();
    }

    class SendungViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageThumbnail)
        ImageView mThumbnail;

        @BindView(R.id.textTitle)
        TextView mTitle;

        SendungViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> onSeriesClicked(getAdapterPosition()));
        }

    }
}
