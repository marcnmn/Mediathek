package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.SendungAdapter;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.StationUtils.ZdfUtils;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

public class SendungFragment extends Fragment {

    private static int mCharacter = ("A").charAt(0);
    private static final int mLastCharValue = ("Z").charAt(0);

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int INT_UPDATE_THRESHOLD = 20;
    private static final int INT_CHARACTER_UPDATE_COUNT = 4;
    private SendungAdapter mSendungAdapter;
    private LayoutManager mLayoutManager;

    private boolean mIsLoading;
    private OnVideoInteractionListener mListener;

    public SendungFragment() {
    }

    public static SendungFragment newInstance(int columnCount) {
        SendungFragment fragment = new SendungFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_livestream_list, container, false);
        if (!(view instanceof RecyclerView)) return view;

        RecyclerView recyclerView = (RecyclerView) view;

         return view;
    }

    private void downloadSendungen() {
        if (mIsLoading) return;
        mIsLoading = true;
        mSendungAdapter.setLoading(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int endCaracter = Math.min(mCharacter + INT_CHARACTER_UPDATE_COUNT, mLastCharValue);
                final ArrayList<Series> sendungen = ZdfUtils.getAllShows(getActivity(), mCharacter, endCaracter);
                mCharacter += INT_CHARACTER_UPDATE_COUNT;
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSendungAdapter.setLoading(false);
                        mSendungAdapter.updateValues(sendungen);
                        mIsLoading = false;
                    }
                });
            }
        }).start();
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
