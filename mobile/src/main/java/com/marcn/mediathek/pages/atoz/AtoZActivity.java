package com.marcn.mediathek.pages.atoz;

import android.os.Bundle;

import com.marcn.mediathek.R;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.pages.FragmentActivity;

import butterknife.ButterKnife;

public class AtoZActivity extends FragmentActivity {

    @Override
    protected void setUpActivity(Bundle savedInstanceState) {
        InjectHelper.setupPage(this);
        ButterKnife.bind(this);
        setTitle(R.string.action_title_sendungen_abisz);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LetterSelectFragment())
                .commit();
    }
}
