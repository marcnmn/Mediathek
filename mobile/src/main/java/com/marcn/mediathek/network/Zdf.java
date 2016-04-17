package com.marcn.mediathek.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.marcn.mediathek.model.asset.episode.ZdfAsset;

import java.util.ArrayList;
import java.util.List;

public class Zdf {
    @SerializedName("ergebnis")
    @Expose
    public Ergebnis ergebnis;
//    @SerializedName("tracking")
//    @Expose
//    public List<ZdfAsset> teaser = new ArrayList<ZdfAsset>();

    private class Ergebnis {
        @SerializedName("additionalTeaser")
        @Expose
        public Boolean additionalTeaser;
        @SerializedName("teaser")
        @Expose
        public List<ZdfAsset> teaser = new ArrayList<>();
    }
}
