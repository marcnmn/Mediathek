package com.marcn.mediathek.pages.atoz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.R;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.utils.PreferenceManager;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LetterSelectFragment extends Fragment implements Injector<ActivityComponent> {

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    @Inject
    LetterAdapter mAdapter;

    @Inject
    PreferenceManager mPreferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectHelper.setupPage(this);
    }

    @Override
    public void injectWith(ActivityComponent component) {
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sendungen_abisz, container, false);
        ButterKnife.bind(this, view);

        setUpRecycler();
        return view;
    }

    private void setUpRecycler() {
        String[] letters = getResources().getStringArray(R.array.letters);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItems(Arrays.asList(letters));
    }
}
