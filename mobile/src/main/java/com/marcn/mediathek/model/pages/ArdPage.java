package com.marcn.mediathek.model.pages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marcn.mediathek.model.ard.ArdAsset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcneumann on 27.05.16.
 */
public class ArdPage {
    private static final String MOD_LIVE_STREAMS = "livestreams";
    private static final String MOD_STATIONS = "sender";
    private static final String MOD_AtoZ = "sendungAbisZ-mod";

    private String mTitle;
    private List<Section> mSections;

    public ArdPage() {
        mSections = new ArrayList<>();
    }

    public List<Section> getSections() {
        return mSections;
    }

    public void setSections(ArrayList<Section> sections) {
        mSections = sections;
    }

    public List<? extends ArdAsset> getStationList() {
        return findTeaserById(MOD_STATIONS);
    }

    public List<? extends ArdAsset> getLiveSreamList() {
        return findTeaserById(MOD_LIVE_STREAMS);
    }

    public List<? extends ArdAsset> getAtoZSeries() {
        return findTeaserById(MOD_AtoZ);
    }

    public ArrayList<? extends ArdAsset> findTeaserById(String id) {
        ArrayList<ArdAsset> list = new ArrayList<>();
        for (Section s : mSections) {
            for (Section.ModCon modCon : s.mModCons) {
                for (Section.ModCon.Mod mod : modCon.mMods) {
                    if (mod.mId.equals(id)) {
                        list.addAll(mod.mContents);
                    }
                }
            }
        }
        return list;
    }

    @JsonProperty("titel")
    public String getTitle() {
        return mTitle;
    }

    @JsonProperty("titel")
    public void setTitle(String title) {
        mTitle = title;
    }

    private static class Section {
        @JsonProperty("id")
        public String mId;
        @JsonProperty("modCons")
        public List<ModCon> mModCons = new ArrayList<>();

        private static class ModCon {
            @JsonProperty("mods")
            public List<Mod> mMods = new ArrayList<>();

            private static class Mod {
                @JsonProperty("id")
                String mId;

                @JsonProperty("inhalte")
                public List<ArdAsset> mContents = new ArrayList<>();

            }
        }
    }
}
