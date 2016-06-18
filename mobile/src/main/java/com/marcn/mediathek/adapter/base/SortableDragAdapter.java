package com.marcn.mediathek.adapter.base;

import android.support.v7.widget.RecyclerView;

import com.marcn.mediathek.model.base.Asset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SortableDragAdapter<I extends Asset, A extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<A> implements SortableDrag {

    protected ArrayList<I> mItems = new ArrayList<>();
    protected ArrayList<String> mKeyList = new ArrayList<>();

    public void addItems(List<I> items) {
        mItems.addAll(items);
        sortItemList();
        notifyDataSetChanged();
    }

    public void addItem(I item) {
        mItems.add(item);
        sortItemList();
        notifyItemInserted(mItems.indexOf(item));
    }

    @Override
    public void saveItemOrder() {
        // nop
    }

    @Override
    public void sortItemList() {
        // nop
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // nop
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }
}