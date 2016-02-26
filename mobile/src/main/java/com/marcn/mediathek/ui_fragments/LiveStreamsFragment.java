package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.BaseActivity;
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.LiveStreamAdapter;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.base_objects.LiveStreams;
import com.marcn.mediathek.base_objects.Stations.Arte;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.EpgUtils;
import com.marcn.mediathek.utils.XmlParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.RunnableFuture;

public class LiveStreamsFragment extends Fragment {
    public static String FRAGMENT_TAG = "livestream-fragment";

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnVideoInteractionListener mListener;
    private LiveStreams mLiveStreams;
    private LiveStreamAdapter mLiveStreamAdapter;

    public LiveStreamsFragment() {
    }

    public static LiveStreamsFragment newInstance(int columnCount) {
        LiveStreamsFragment fragment = new LiveStreamsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_livestream_list, container, false);
        if (!(view instanceof RecyclerView)) return view;

        Context context = view.getContext();
        RecyclerView mRecyclerView = (RecyclerView) view;

        if ((getActivity()) != null)
            ((BaseActivity) getActivity()).setActionBarResource(R.string.action_title_live_streams);

        GridLayoutManager mLayoutManager = new GridLayoutManager(context, mColumnCount);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                LiveStream l = mLiveStreamAdapter.getItem(position);
                if (l == null) return 1;
                if (l.channel.equals(Constants.TITLE_CHANNEL_ZDF)
                        || l.channel.equals(Constants.TITLE_CHANNEL_ARTE)
                        || l.channel.equals(Constants.TITLE_CHANNEL_ARD)
                        || l.channel.equals(Constants.TITLE_CHANNEL_3SAT)
                        || l.channel.equals(Constants.TITLE_CHANNEL_KIKA))
                    return mColumnCount;
                else return 1;
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (mLiveStreamAdapter == null) {
            mLiveStreams = new LiveStreams(getContext());
            mLiveStreamAdapter = new LiveStreamAdapter(mLiveStreams, mListener);
            mRecyclerView.setAdapter(mLiveStreamAdapter);
        } else {
            mRecyclerView.setAdapter(mLiveStreamAdapter);
        }
        downloadArteData2();

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
                    final ArrayList<LiveStream> ls = XmlParser.getZDFLiveStreamData2(getContext(), mLiveStreams.getGroup(LiveStream.ZDF_MAIN_GROUP));
                    EpgUtils.getLiveStreamEpgNow(ls);
                    if (getActivity() == null || ls == null) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStreamAdapter.updateZdfValues(ls);
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
                final LiveStream l = XmlParser.arteLiveStreamData(getContext(), mLiveStreams.getArteLiveStream());
                EpgUtils.getLiveStreamEpgNow(l);
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLiveStreamAdapter.updateArteValue(l);
                    }
                });
            }
        }).start();
    }

    private void downloadArteData2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Arte arte = new Arte();
                final LiveStreamM3U8 l = arte.getLiveStream();
                final Episode episode = arte.getCurrentEpisode();
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }).start();
    }

    private void downloadArdData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<LiveStream> ls = XmlParser.ardLiveStreamsData(getContext(), mLiveStreams.getGroup(LiveStream.ARD_GROUP));
                EpgUtils.getLiveStreamEpgNow(ls);
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLiveStreamAdapter.updateArdValues(ls);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        if (mLiveStreamAdapter != null && mListener != null) {
            mLiveStreamAdapter.setVideoClickListener(mListener);
        }
        super.onResume();
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
