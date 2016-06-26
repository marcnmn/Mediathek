package com.marcn.mediathek.views.bottom_bar;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class BottomItem {

    @StringRes
    private int mTitle;

    @DrawableRes
    private int mIcon;

    private int mValue;

    private BottomBarManager.PlayerItemType mType;

    private BottomItem() {
    }

    public static BottomItem createBottomItem(@StringRes int titleRes, @DrawableRes int iconRes,
                                              int value, BottomBarManager.PlayerItemType type) {
        BottomItem bottomItem = new BottomItem();
        bottomItem.setTitle(titleRes);
        bottomItem.setIcon(iconRes);
        bottomItem.setValue(value);
        bottomItem.setType(type);
        return bottomItem;
    }

    public int getTitle() {
        return mTitle;
    }

    public void setTitle(int title) {
        mTitle = title;
    }

    public int getIcon() {
        return mIcon;
    }

    private void setIcon(int icon) {
        mIcon = icon;
    }

    public int getValue() {
        return mValue;
    }

    private void setValue(int value) {
        mValue = value;
    }

    public BottomBarManager.PlayerItemType getType() {
        return mType;
    }

    private void setType(BottomBarManager.PlayerItemType type) {
        mType = type;
    }
}
