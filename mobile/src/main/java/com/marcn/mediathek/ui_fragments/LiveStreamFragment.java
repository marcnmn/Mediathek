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
import com.marcn.mediathek.utils.NetworkTasks;
import com.marcn.mediathek.utils.XmlParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LiveStreamFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<LiveStream> mLiveStreams = new ArrayList<>();
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
        mLiveStreamAdapter = new LiveStreamAdapter(mLiveStreams, mListener);
        recyclerView.setAdapter(mLiveStreamAdapter);

        downloadZdfData();
        return view;
    }

    private void downloadZdfData() {
        final String url = "http://www.zdf.de/ZDFmediathek/xmlservice/web/live?maxLength=30";
        new Thread(new Runnable() {
            @Override
            public void run() {
                final InputStream response = NetworkTasks.downloadStringDataAsInputStream(url);
                try {
                    final ArrayList<LiveStream> ls = XmlParser.parseLiveStreams(response);
                    if (getActivity() == null || ls == null) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStreamAdapter.updateValues(ls);
                            downloadArteData();
                        }
                    });
                } catch (IOException ignored) {}
            }
        }).start();
    }

    private void downloadArteData() {
        final String url = "http://www.arte.tv/guide/de/live";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(url).get();
                    final String thumbnail = doc.select("div.video-block.LIVE.has-play > img").attr("src");
                    if (getActivity() == null || thumbnail == null) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStreamAdapter.updateValue(new LiveStream("6", "ARTE", thumbnail));
                            downloadArdData();
                        }
                    });
                } catch (IOException ignored) {}
            }
        }).start();
    }

    private void downloadArdData() {
        final String url = "http://www.ardmediathek.de/tv/live";
        final String image_url = "http://www.ardmediathek.de";
        final ArrayList<LiveStream> ls = new ArrayList<>();
        ls.add(new LiveStream("5868", "ARD-alpha", ""));
        ls.add(new LiveStream("21518950", "Bayerisches-Fernsehen-S%C3%BCd", ""));
        ls.add(new LiveStream("208", "Das-Erste", ""));
        ls.add(new LiveStream("1386804", "MDR-SACHSEN", ""));
        ls.add(new LiveStream("21518352", "NDR-Niedersachsen", ""));
        ls.add(new LiveStream("21518358", "rbb-Berlin", ""));
        ls.add(new LiveStream("5870", "SR-Fernsehen", ""));
        ls.add(new LiveStream("5904", "SWR-Baden-W%C3%BCrttemberg", ""));
        ls.add(new LiveStream("5902", "WDR-Fernsehen", ""));
        ls.add(new LiveStream("5878", "tagesschau24", ""));
        ls.add(new LiveStream("5900", "3sat", ""));
        ls.add(new LiveStream("5886", "KiKA", ""));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(url).get();
                    for (int i = 0; i < ls.size(); i++) {
                        LiveStream l = ls.get(i);
                        String json = doc.select("a[href=/tv/"+l.title+"/live?kanal="+l.id+"].medialink").select("img.img.hideOnNoScript").attr("data-ctrl-image");
                        JSONObject j = new JSONObject(json);
                        String thumb = j.getString("urlScheme");
                        thumb = thumb.substring(0, thumb.indexOf("#"));
                        ls.set(i, new LiveStream(l.id, l.title, image_url + thumb + "384"));
                    }
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStreamAdapter.updateValues(ls);
                        }
                    });
                } catch (IOException | JSONException ignored) {}
            }
        }).start();
    }

    private void addOtherChannels() {
        ArrayList<LiveStream> ls = new ArrayList<>();
        String[] channels = getActivity().getResources().getStringArray(R.array.channels);
        for (int i = 7; i < channels.length; i++)
            ls.add(new LiveStream("" + i, channels[i], ""));
        mLiveStreamAdapter.updateValues(ls);
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
