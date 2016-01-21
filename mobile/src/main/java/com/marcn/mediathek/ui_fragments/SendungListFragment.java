package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.SendungAdapter;
import com.marcn.mediathek.adapter.VideoAdapter;
import com.marcn.mediathek.base_objects.Sendung;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;
import java.util.Calendar;

public class SendungListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int INT_GRID_UPDATE_THRESHOLD = 15;
    private static final int INT_GRID_UPDATE_COUNT = 100;
    private int mColumnCount = 2;
    private int mLoadedItems = 0;
    private SendungAdapter mSendungAdapter;
    private GridLayoutManager mLayoutManager;

    private Calendar mDay, mLastDay;
    private boolean mIsLoading;
    private OnVideoInteractionListener mListener;

    public SendungListFragment() {
    }

    public static SendungListFragment newInstance(int columnCount) {
        SendungListFragment fragment = new SendungListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_livestream_list, container, false);
        if (!(view instanceof RecyclerView)) return view;

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;

        recyclerView.setLayoutManager(new LayoutManager(getActivity()));

        ArrayList<Sendung> list = new ArrayList<>();
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungHeader("A"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungHeader("B"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungHeader("C"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));
        list.add(Sendung.createSendungItem("asdfasdf"));

        mSendungAdapter = new SendungAdapter(list, null);
        recyclerView.setAdapter(mSendungAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void downloadSendungen(final int count, final String characterStart, final String characterEnd) {
        if (mIsLoading) return;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVideoInteractionListener) {
            mListener = (OnVideoInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
