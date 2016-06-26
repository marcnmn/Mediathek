package com.marcn.mediathek.views.bottom_bar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcn.mediathek.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subjects.Subject;

class BottomAdapter extends RecyclerView.Adapter<BottomAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<BottomItem> mEntries;
    private Subject<BottomItem, BottomItem> mSubject;

    BottomAdapter(Context context) {
        mContext = context;
    }

    void setEntries(ArrayList<BottomItem> entries) {
        mEntries = entries;
    }

    public void setSubject(Subject<BottomItem, BottomItem> subject) {
        mSubject = subject;
    }

    @Override
    public BottomAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_player_bottom, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BottomAdapter.ViewHolder viewHolder, int i) {
        BottomItem item = mEntries.get(i);

        if (item.getValue() == BottomBarManager.NO_VALUE) {
            viewHolder.mTextView.setText(item.getTitle());
        } else if (item.getValue() == BottomBarManager.DISABLED) {
            viewHolder.mTextView.setText(mContext.getString(item.getTitle(), mContext.getString(R.string.player_setting_video_track_disabled)));
        } else {
            viewHolder.mTextView.setText(mContext.getString(item.getTitle(), heightToString(item)));
        }

        if (item.getIcon() != BottomBarManager.NO_VALUE) {
            viewHolder.mImageView.setImageResource(item.getIcon());
        }
    }

    private String heightToString(BottomItem item) {
        return item.getValue() < 0 ? "Automatisch" : item.getValue() + "p";
    }

    @Override
    public int getItemCount() {
        return mEntries == null ? 0 : mEntries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.player_bottom_item_text)
        TextView mTextView;

        @BindView(R.id.player_bottom_item_icon)
        ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener((v) -> {
                mSubject.onNext(mEntries.get(getAdapterPosition()));
            });
        }
    }
}
