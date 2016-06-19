package com.marcn.mediathek.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marcn.mediathek.R;

public class BottomQualityAdapter extends RecyclerView.Adapter<BottomQualityAdapter.ViewHolder> {
    private String[] countries;
    private OnQualityClicked mOnQualityClicked;


    public interface OnQualityClicked {
        void setQuality(int item);
    }

    public BottomQualityAdapter(String[] countries) {
        this.countries = countries;
    }

    public void setOnQualityClicked(OnQualityClicked onQualityClicked) {
        mOnQualityClicked = onQualityClicked;
    }

    private void onViewholderClicked(ViewHolder viewHolder) {
        if (mOnQualityClicked != null) {
            mOnQualityClicked.setQuality(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public BottomQualityAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_quality, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BottomQualityAdapter.ViewHolder viewHolder, int i) {
        viewHolder.tv_country.setText(countries[i]);
    }

    @Override
    public int getItemCount() {
        return countries.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_country;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(view1 -> onViewholderClicked(ViewHolder.this));
            tv_country = (TextView) view.findViewById(R.id.text_player_quality_item);
        }
    }
}
