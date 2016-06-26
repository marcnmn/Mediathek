package com.marcn.mediathek.views.bottom_bar;

import android.app.Activity;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.marcn.mediathek.R;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.subjects.Subject;

@Singleton
public class BottomBarManager {

    public static final int NO_VALUE = Integer.MIN_VALUE;
    public static final int DISABLED = -2;

    private Subject<BottomItem, BottomItem> mSubject;
    private Activity mContext;
    private ArrayList<BottomSheetDialog> mDialogs = new ArrayList<>();
    private ArrayList<BottomItem> mItems = new ArrayList<>();

    @Inject
    public BottomBarManager(Subject<BottomItem, BottomItem> subject) {
        mSubject = subject;
    }

    public BottomBarManager() {
    }

    public BottomBarManager withContext(Activity context) {
        mItems.clear();
        mContext = context;
        return this;
    }

    public BottomBarManager withQualitySetting(int resolution) {
        mItems.add(BottomItem.createBottomItem(R.string.player_setting_quality,
                R.drawable.ic_settings, resolution, PlayerItemType.TOP_LEVEL_QUALITY));
        return this;
    }

    public BottomBarManager withDownloadSetting() {
        mItems.add(BottomItem.createBottomItem(R.string.player_setting_download,
                R.drawable.ic_file_download_white_24dp, NO_VALUE, PlayerItemType.TOP_LEVEL_DOWNLOAD));
        return this;
    }

    public BottomBarManager withBackgroundSetting(boolean enabled) {
        mItems.add(BottomItem.createBottomItem(
                enabled ? R.string.player_setting_audio_background_disabled : R.string.player_setting_audio_background_enabled,
                enabled ? R.drawable.ic_pause_circle_outline_white_24dp : R.drawable.ic_play_circle_outline_white_24dp,
                NO_VALUE, PlayerItemType.TOP_LEVEL_AUDIO_BACKGROUND));
        return this;
    }

    public BottomBarManager withMuteAudioSetting(boolean muted) {
        mItems.add(BottomItem.createBottomItem(
                muted ? R.string.player_setting_mute_disable : R.string.player_setting_mute_enable,
                muted ? R.drawable.ic_volume_up_white_24dp : R.drawable.ic_volume_off_white_24dp,
                NO_VALUE, PlayerItemType.TOP_LEVEL_AUDIO_MUTE));
        return this;
    }

    public BottomBarManager withHeadphoneMode(boolean enabled) {
        mItems.add(BottomItem.createBottomItem(
                enabled ? R.string.player_setting_headphone_mode_disable : R.string.player_setting_headphone_mode_enable,
                enabled ? R.drawable.ic_phone_android_white_24dp : R.drawable.ic_headset_white_24dp,
                NO_VALUE, PlayerItemType.TOP_LEVEL_HEADPHONE_MODE));
        return this;
    }

    public BottomBarManager withEntryList(ArrayList<BottomItem> items) {
        mItems.addAll(items);
        return this;
    }

    public void show() {
        BottomAdapter settingsAdapter = new BottomAdapter(mContext);
        settingsAdapter.setEntries(mItems);
        settingsAdapter.setSubject(mSubject);
        showListBottomBar(settingsAdapter, mContext);
    }

    @SuppressWarnings("ConstantConditions")
    private void showListBottomBar(BottomAdapter adapter, Activity activity) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        mDialogs.add(bottomSheetDialog);

        RecyclerView recyclerView = (RecyclerView) activity.getLayoutInflater().inflate(R.layout.content_recycler, null);
        bottomSheetDialog.setContentView(recyclerView);

        // FIXME: 25.06.16 hack to show the dialog fully expanded
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from
                (bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        bottomSheetDialog.show();
    }

    public void clearTop() {
        if (!mDialogs.isEmpty()) {
            mDialogs.get(mDialogs.size() - 1).dismiss();
        }
    }

    public void clearAll() {
        for (BottomSheetDialog dialog : mDialogs) {
            dialog.dismiss();
        }
    }

    public enum PlayerItemType {
        SHARE,
        TOP_LEVEL_QUALITY,
        TOP_LEVEL_DOWNLOAD,
        TOP_LEVEL_AUDIO_BACKGROUND,
        TOP_LEVEL_AUDIO_MUTE,
        TOP_LEVEL_HEADPHONE_MODE,
        SELECTED_QUALITY
    }
}
