package com.marcn.mediathek.network;

import java.util.ArrayList;

/**
 * Created by marcneumann on 31.05.16.
 */
public class Ard {
    private ArrayList<Section> mSections;

    public Ard() {
        mSections = new ArrayList<>();
    }

    public ArrayList<Section> getSections() {
        return mSections;
    }

    public void setSections(ArrayList<Section> sections) {
        mSections = sections;
    }

    private static class Section {
        private int mId;

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }
    }
}
