package com.marcn.mediathek.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

public class PreferenceManager {
    private static final String PREF_FILE_KEY = "MediathekPreferences.pref";

    private Context mContext;

    @Inject
    public PreferenceManager(Context context) {
        mContext = context;
    }

    public <T> boolean saveObjectAsJSON(String preferenceName, T object) {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE);
        try {
            sharedpreferences.edit()
                    .putString(preferenceName, new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(object))
                    .apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> getStringArray(String preferenceKey) {
        String data = mContext.getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE).getString(preferenceKey, null);
        if (data == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
