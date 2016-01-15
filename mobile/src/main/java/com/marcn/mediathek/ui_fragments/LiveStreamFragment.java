package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.LiveStreamAdapter;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.LiveStreams;
import com.marcn.mediathek.utils.HTMLParser;
import com.marcn.mediathek.utils.NetworkTasks;
import com.marcn.mediathek.utils.XmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LiveStreamFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private LiveStreams mLiveStreams;
    private LiveStreamAdapter mLiveStreamAdapter;

    public LiveStreamFragment() {
    }

    public static LiveStreamFragment newInstance(int columnCount) {
        LiveStreamFragment fragment = new LiveStreamFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_livestream_list, container, false);
        if (!(view instanceof RecyclerView)) return view;

        Context context = view.getContext();

        RecyclerView recyclerView = (RecyclerView) view;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        mLiveStreams = new LiveStreams(getContext());
        mLiveStreamAdapter = new LiveStreamAdapter(mLiveStreams, mListener);
        recyclerView.setAdapter(mLiveStreamAdapter);

        downloadZdfData();
        downloadArteData();
        downloadArdData();
        return view;
    }

    private void downloadZdfData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<LiveStream> ls = XmlParser.getZDFLiveStreamData(getContext(), mLiveStreams.getGroup(LiveStream.ZDF_MAIN_GROUP));
                    if (getActivity() == null || ls == null) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStreamAdapter.updateValues(ls);
                        }
                    });
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    private void downloadArteData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final LiveStream l = HTMLParser.arteLiveStreamData(getContext(), mLiveStreams.getArteLiveStream());
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLiveStreamAdapter.updateValue(l);
                    }
                });
            }
        }).start();
    }

    private void downloadArdData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<LiveStream> ls = HTMLParser.ardLiveStreamsData(getContext(), mLiveStreams.getGroup(LiveStream.ARD_GROUP));
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLiveStreamAdapter.updateValues(ls);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(LiveStream item);
    }
}
