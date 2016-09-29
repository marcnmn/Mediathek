package com.marcn.mediathek.pages.atoz;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marcn.mediathek.R;
import com.marcn.mediathek.utils.NavigationManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

class LetterAdapter extends RecyclerView.Adapter<LetterAdapter.LetterViewHolder> {

    private NavigationManager mNavigationManager;
    private List<String> mValues = new ArrayList<>();

    @Inject
    LetterAdapter(NavigationManager navigationManager) {
        mNavigationManager = navigationManager;
    }

    void setItems(List<String> letters) {
        mValues.clear();
        mValues.addAll(letters);
        notifyDataSetChanged();
    }

    @Override
    public LetterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_letter_select, parent, false);
        return new LetterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LetterViewHolder holder, int position) {
        holder.mTitle.setText(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void onLetterClicked(int position) {
        mNavigationManager.gotToAtoZFragment(mValues.get(position));
//        ZdfSeries series = mValues.get(position);
//        Snackbar.make(mContext.findViewById(android.R.id.content), series.getTitle(), Snackbar.LENGTH_LONG).show();
    }

    class LetterViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;

        LetterViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView;
            itemView.setOnClickListener(view -> onLetterClicked(getAdapterPosition()));
        }

    }
}
