package com.marcn.mediathek.adapter.base;

import android.support.v7.widget.RecyclerView;

public interface SortableDrag {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState);

    void saveItemOrder();

    void sortItemList();

}
