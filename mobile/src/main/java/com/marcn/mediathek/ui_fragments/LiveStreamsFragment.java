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
import com.marcn.mediathek.adapter.LiveStreamAdapter;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.utils.NavigationManager;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class LiveStreamsFragment extends Fragment implements Injector<ActivityComponent> {
    public static String FRAGMENT_TAG = "livestream-fragment";

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnVideoInteractionListener mListener;
    private LiveStreamAdapter mAdapter;
    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Inject
    ArdInteractor mArdInteractor;

    @Inject
    ZdfInteractor mZdfInteractor;

    @Inject
    NavigationManager mNavigationManager;

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
        InjectHelper.setupPage(this);
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

        mColumnCount = context.getResources().getInteger(R.integer.live_streams);

        GridLayoutManager mLayoutManager = new GridLayoutManager(context, mColumnCount);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);

        loadLiveStreams();
        return view;
    }

    private void loadLiveStreams() {
        mSubscription.add(
                Observable.concat(mZdfInteractor.loadZdfStreams(), mArdInteractor.loadLivestreams())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onStreamSuccess, this::onError)
        );
    }

    private void onStreamSuccess(List<Stream> streams) {
        mAdapter.addItems(streams);
    }

    private void onError(Throwable throwable) {

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

    @Override
    public void injectWith(ActivityComponent component) {
        component.inject(this);
    }

//    @Override
//    public void onStreamClicked(Stream stream) {
//        if (stream instanceof ArdLive) {
//            String id = ((ArdLive) stream).getId();
//            mSubscription.add(
//                    mArdInteractor.loadVideo(id)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(a -> mNavigationManager.startInternalPlayer(a), this::onError)
//            );
//        } else if (stream instanceof ZdfLive) {
//
//        }
//    }
//
//    @Override
//    public void onStreamLongClicked(Stream stream) {
//        if (stream instanceof ArdLive) {
//            String id = ((ArdLive) stream).getId();
//            mSubscription.add(
//                    mArdInteractor.loadVideo(id)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(a -> mNavigationManager.startExternalPlayer(a), this::onError)
//            );
//        } else if (stream instanceof ZdfLive) {
//
//        }
//    }
}
