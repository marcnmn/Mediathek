package com.marcn.mediathek.pages.missed;

import android.os.Bundle;

import com.marcn.mediathek.R;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.pages.CoordinatorActivity;

import butterknife.ButterKnife;

public class MissedActivity extends CoordinatorActivity {

    @Override
    protected int getContentResource() {
        return R.layout.content_missed_fragment;
    }

    @Override
    protected void setUpActivity(Bundle savedInstanceState) {
        InjectHelper.setupPage(this);
        ButterKnife.bind(this);
        setTitle(R.string.action_title_sendung_verpasst);
    }
}
