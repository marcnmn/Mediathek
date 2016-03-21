package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.marcn.mediathek.StationUtils.ZdfUtils;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

public class SendungenAbisZFragment extends Fragment {

    private static int mCharacter = ("A").charAt(0);
    private static final int mLastCharValue = ("Z").charAt(0);

    private static final int INT_UPDATE_THRESHOLD = 20;
    private static final int INT_CHARACTER_UPDATE_COUNT = 4;
    private SendungAdapter mSendungAdapter;
    private LayoutManager mLayoutManager;

    private boolean mIsLoading;
    private OnVideoInteractionListener mListener;
    private RelativeLayout.LayoutParams mScrollLayoutParams;
    private int mWindowHeight;
    private TextView mIndicator;

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

                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSendungAdapter.setLoading(false);
                        mSendungAdapter.updateValues(sendungen);
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
}
