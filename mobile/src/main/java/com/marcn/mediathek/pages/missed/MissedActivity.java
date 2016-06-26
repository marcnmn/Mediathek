package com.marcn.mediathek.pages.missed;

import android.os.Bundle;

import com.marcn.mediathek.R;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.pages.CoordinatorActivity;

import butterknife.ButterKnife;

public class MissedActivity extends CoordinatorActivity implements Injector<ActivityComponent> {

    @Override
    protected int getContentResource() {
        return R.layout.content_missed_fragment;
    }

    @Override
    protected void setUpActivity(Bundle savedInstanceState) {
        InjectHelper.setupPage(this);
        ButterKnife.bind(this);

//        ZdfMissedVideoFragment missedVideoFragment = (ZdfMissedVideoFragment)
//                getSupportFragmentManager().findFragmentById(R.id.content_missed_fragment);
    }

    @Override
    public void injectWith(ActivityComponent component) {
        component.inject(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
