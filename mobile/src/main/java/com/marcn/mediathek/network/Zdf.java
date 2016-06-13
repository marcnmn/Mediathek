package com.marcn.mediathek.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.marcn.mediathek.model.zdf.ZdfAsset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Zdf {

    @SerializedName(value = "ergebnis")
    public Ergebnis ergebnis;

    @SerializedName("live")
    private Live live;

    @SerializedName("sender")
    private Sender sender;

    @SerializedName("themen")
    private Themen themen;

    @SerializedName("tipps")
    private Tipps tipps;

    @SerializedName("aktuell")
    private Aktuell aktuell;

    @SerializedName("meistgesehen")
    private Meistgesehen meistgesehen;

    public List<Category> getAllCategories() {
        Category[] c = {ergebnis, live, sender, themen, tipps, aktuell, meistgesehen};
        return Arrays.asList(c);
    }

    public List<ZdfAsset> getZdfAssets() {
        return ergebnis == null ? null : ergebnis.teaser;
    }

    private class Category {
        @SerializedName("teaser")
        @Expose
        public List<ZdfAsset> teaser = new ArrayList<>();
    }

    private class Ergebnis extends Category {
    }

    private class Live extends Category {
    }

    private class Sender extends Category {
    }

    private class Themen extends Category {
    }

    private class Tipps extends Category {
    }

    private class Meistgesehen extends Category {
    }

    private class Aktuell extends Category {
    }

    private class Teaser {

        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("titel")
        @Expose
        public String titel;
        @SerializedName("beschreibung")
        @Expose
        public String beschreibung;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("length")
        @Expose
        public Integer length;
        @SerializedName("airtime")
        @Expose
        public String airtime;
        @SerializedName("timetolive")
        @Expose
        public String timetolive;
        @SerializedName("channel")
        @Expose
        public String channel;
        @SerializedName("originChannelId")
        @Expose
        public Integer originChannelId;
        @SerializedName("originChannelTitle")
        @Expose
        public String originChannelTitle;
        @SerializedName("airtimeEnd")
        @Expose
        public String airtimeEnd;
    }
}
