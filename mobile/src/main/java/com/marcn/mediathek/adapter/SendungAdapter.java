package com.marcn.mediathek.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.Sendung;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.utils.DateFormat;
import com.squareup.picasso.Picasso;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;
import java.util.Calendar;

public class SendungAdapter extends RecyclerView.Adapter<SendungAdapter.SendungViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int LINEAR = 0;

    private final ArrayList<Sendung> mValues;
    private final OnVideoInteractionListener mListener;

    public SendungAdapter(ArrayList<Sendung> items, OnVideoInteractionListener listener) {
        if (items == null)
            mValues = new ArrayList<>();
        else {
            mValues = items;
        }
        mListener = listener;
        notifyDataSetChanged();
    }

    public void updateValues(Sendung video) {
        mValues.add(video);
        notifyItemChanged(mValues.size() - 1);
    }

    public void updateValues(ArrayList<Sendung> ls) {
        mValues.addAll(ls);
        notifyDataSetChanged();
    }

    @Override
    public SendungViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new SendungViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SendungViewHolder viewHolder, int position) {
        final Sendung item = mValues.get(position);
        final View itemView = viewHolder.itemView;

        viewHolder.bindItem(item);

        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(viewHolder.itemView.getLayoutParams());
        if (item.isHeader) {
            lp.headerDisplay = GridSLM.LayoutParams.HEADER_OVERLAY;
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;

            lp.headerEndMarginIsAuto = true;
            lp.headerStartMarginIsAuto = true;
        }

        lp.setSlm(LinearSLM.ID);
        lp.setColumnWidth(200);
        lp.setFirstPosition(getFirstSectionPosition(position));
        itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public int getFirstSectionPosition(int position) {
        for (int i = position-1; i >= 0; i--)
            if (mValues.get(i).isHeader)
                return i;
        return 0;
    }

    public class SendungViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumb;
        public final TextView mTitle;
        public Sendung mItem;
        public int sectionManager;

        public SendungViewHolder(View view) {
            super(view);
            mView = view;
            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
            mTitle = (TextView) view.findViewById(R.id.textTitle);
        }

        public void bindItem(Sendung sendung) {
            mTitle.setText(sendung.title);
        }
    }
}
