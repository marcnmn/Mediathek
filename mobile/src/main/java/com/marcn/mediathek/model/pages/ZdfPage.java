package com.marcn.mediathek.model.pages;

import android.support.annotation.Nullable;

import com.marcn.mediathek.model.zdf.ZdfAsset;
import com.marcn.mediathek.model.zdf.ZdfEpisode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ZdfPage {
    public static final String CATEGORY_TYPE_LIVE = "category-live";
    public static final String CATEGORY_TYPE_THEMES = "category-themes";
    public static final String CATEGORY_TYPE_TIPPS = "category-tipps";
    public static final String CATEGORY_TYPE_RUBRICS = "category-rubrics";
    public static final String CATEGORY_TYPE_NEW = "category-new";
    public static final String CATEGORY_TYPE_MOST_WATCHED = "category-most-watched";
    public static final String CATEGORY_TYPE_RESUlT = "category-result";

    private HashMap<String, Category> mCategories;

    public ZdfPage() {
        mCategories = new LinkedHashMap<>();
    }

    public HashMap<String, Category> getCategories() {
        return mCategories;
    }

    public void setLive(Category live) {
        mCategories.put(CATEGORY_TYPE_LIVE, live);
    }

    public void setThemen(Category themen) {
        mCategories.put(CATEGORY_TYPE_THEMES, themen);
    }

    public void setTipps(Category tipps) {
        mCategories.put(CATEGORY_TYPE_TIPPS, tipps);
    }

    public void setRubriken(Category rubriken) {
        mCategories.put(CATEGORY_TYPE_RUBRICS, rubriken);
    }

    public void setAktuell(Category aktuell) {
        mCategories.put(CATEGORY_TYPE_NEW, aktuell);
    }

    public void setMeistGesehen(Category meistGesehen) {
        mCategories.put(CATEGORY_TYPE_MOST_WATCHED, meistGesehen);
    }

    public void setErgebnis(Category ergebnis) {
        mCategories.put(CATEGORY_TYPE_RESUlT, ergebnis);
    }

    @SuppressWarnings("Convert2streamapi")
    @Nullable
    public ArrayList<ZdfEpisode> getResultList() {
        ArrayList<ZdfEpisode> episodes = new ArrayList<>();
        for (ZdfAsset a : getTeaserList(CATEGORY_TYPE_RESUlT)) {
            episodes.add((ZdfEpisode) a);
        }
        return episodes;
    }

    public List<ZdfAsset> getTeaserList(String type) {
        if (mCategories.containsKey(type)) {
            return mCategories.get(type).teaser;
        }
        return null;
    }

    private static class Category {
        public List<ZdfAsset> teaser = new ArrayList<>();
    }
}
