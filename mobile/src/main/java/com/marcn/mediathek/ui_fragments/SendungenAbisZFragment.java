package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marcn.mediathek.BaseActivity;
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.SendungAdapter;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.stations.Station;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.DataUtils;
import com.marcn.mediathek.utils.LayoutTasks;
import com.marcn.mediathek.utils.Storage;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

public class SendungenAbisZFragment extends Fragment {

    private static int mCharacter = ("A").charAt(0);
    private static final int mLastCharValue = ("Z").charAt(0);

    private static final String SERIES_STORAGE_KEY = "series-storage-key.txt";
    private static final int INT_UPDATE_THRESHOLD = 20;
    private static final int INT_CHARACTER_UPDATE_COUNT = 4;
    private SendungAdapter mSendungAdapter;
    private LayoutManager mLayoutManager;

    private boolean mIsLoading;
    private OnVideoInteractionListener mListener;
    private RelativeLayout.LayoutParams mScrollLayoutParams;
    private int mWindowHeight;
    private TextView mIndicator;
    private FloatingActionButton fabSearch;
    private EditText editSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCharacter = ("A").charAt(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sendungen_abisz, container, false);
        Context context = view.getContext();

        if ((getActivity()) != null)
            ((BaseActivity) getActivity()).setActionBarResource(R.string.action_title_sendungen_abisz);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        View scrollArea = view.findViewById(R.id.fastScrollArea);
        mWindowHeight = LayoutTasks.getWindowHeight(context);
        mIndicator = (TextView) view.findViewById(R.id.indicator);
        mScrollLayoutParams = (RelativeLayout.LayoutParams) mIndicator.getLayoutParams();

        mLayoutManager = new LayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mSendungAdapter = new SendungAdapter(new ArrayList<Series>(), mListener);
        recyclerView.setAdapter(mSendungAdapter);

        fabSearch = (FloatingActionButton) getActivity().findViewById(R.id.fabSearch);
        if (fabSearch != null) {
            fabSearch.setVisibility(View.VISIBLE);
            fabSearch.setOnClickListener(fabOnClickListener);
        }

        editSearch = (EditText) getActivity().findViewById(R.id.editSearch);
        if (editSearch != null) {
            editSearch.addTextChangedListener(editSearchWatcher);
            editSearch.setOnFocusChangeListener(editSearchFocusListener);
        }

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
//                if (lastVisibleItem >= mSendungAdapter.getItemCount() - INT_UPDATE_THRESHOLD
//                        && mCharacter <= mLastCharValue)
//                    downloadSendungen();
//            }
//        });
        ArrayList<Series> series = Storage.getSeriesFromFile(context, SERIES_STORAGE_KEY);
        if (series != null)
            mSendungAdapter.updateValues(series);

        scrollArea.setOnTouchListener(fastScrollListener);
        downloadSendungen();
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
                Station station = Station.createStation(Constants.TITLE_CHANNEL_ZDF);
                final ArrayList<Series> sendungen = station.fetchAllSeries(mCharacter, mLastCharValue);

                station = Station.createStation(Constants.TITLE_CHANNEL_ARD);
                ArrayList<Series> sendungen2 = station.fetchAllSeries(mCharacter, mLastCharValue);
                sendungen.addAll(sendungen2);

                DataUtils.sortSeriesByName(sendungen);
                DataUtils.addNameHeaders(sendungen);

                Storage.saveSeriesOnDisk(getActivity(), sendungen, SERIES_STORAGE_KEY);

                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSendungAdapter.setLoading(false);
                        mSendungAdapter.forceUpdateAll(sendungen);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fabSearch != null)
            fabSearch.setVisibility(View.GONE);
        if (editSearch != null)
            editSearch.setVisibility(View.GONE);
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

    private View.OnTouchListener fastScrollListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float normalizedPosition = 1.7f * (event.getY() - mWindowHeight / 2) + mWindowHeight / 2;
            int fastScrollPosition = (int) (mSendungAdapter.getItemCount() * normalizedPosition / mWindowHeight);
            fastScrollPosition = Math.max(0, Math.min(fastScrollPosition, mSendungAdapter.getItemCount() - 1));
            mLayoutManager.scrollToPosition(fastScrollPosition);

            // Indicator
            mScrollLayoutParams.topMargin = (int) event.getY() - mScrollLayoutParams.height;
            mIndicator.setLayoutParams(mScrollLayoutParams);

            int firstVisible = mLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (mSendungAdapter.getMember(firstVisible) != null) {
                mIndicator.setText(mSendungAdapter.getMember(firstVisible));

            }

            if (event.getAction() == MotionEvent.ACTION_DOWN && firstVisible >= 1)
                mIndicator.setVisibility(View.VISIBLE);
            if (event.getAction() == MotionEvent.ACTION_UP)
                mIndicator.setVisibility(View.INVISIBLE);

            return true;
        }
    };

    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        boolean searchMode = true;
        @Override
        public void onClick(View v) {
            if (editSearch == null) return;

            if (searchMode) {
                editSearch.setVisibility(View.VISIBLE);
                searchMode = false;
                fabSearch.setImageResource(R.drawable.ic_done_white);
            } else {
                editSearch.setVisibility(View.GONE);
                searchMode = true;
                fabSearch.setImageResource(R.drawable.ic_search_white);
            }
        }
    };

    private TextWatcher editSearchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String query = String.valueOf(s);
            if (query != null)
                mSendungAdapter.searchAndFilter(query);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnFocusChangeListener editSearchFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

        }
    };
}
