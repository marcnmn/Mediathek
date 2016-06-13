package com.marcn.mediathek;

import android.content.Context;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * Created by marcneumann on 24.05.16.
 */
public class ToastManager {
    private Context mContext;

    @Inject
    public ToastManager(Context context) {
        mContext = context;
    }

    public void showToast(String string) {
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }
}
