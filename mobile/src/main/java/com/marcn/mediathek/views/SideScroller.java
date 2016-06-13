package com.marcn.mediathek.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marcn.mediathek.R;
import com.marcn.mediathek.model.base.Episode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SideScroller extends RelativeLayout {

    @BindView(R.id.buttonMore)
    TextView mMore;

    @BindView(R.id.textHeader)
    TextView mTitle;

    @BindView(R.id.recyclerViewVideos)
    RecyclerView mRecyclerView;


    public SideScroller(Context context) {
        super(context);
        init();
    }

    public SideScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SideScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SideScroller(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.include_sidescroller, this);
        ButterKnife.bind(this);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setItems(List<Episode> episodes) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        SideScrollerAdapter sideScrollerAdapter = new SideScrollerAdapter(episodes, getContext());
        mRecyclerView.setAdapter(sideScrollerAdapter);
    }
}
