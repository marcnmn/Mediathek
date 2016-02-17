package com.marcn.mediathek.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ParserUtils {

    public static String getString(JSONObject j, String tag) {
        try {
            return j.getString(tag);
        } catch (JSONException | NullPointerException e) {
            return "";
        }
    }

    public static int getInt(JSONObject j, String tag, int defaultValue) {
        try {
            return j.getInt(tag);
        } catch (JSONException | NullPointerException e) {
            return defaultValue;
        }
    }

    public static boolean getString(JSONObject j, String tag, boolean defaultValue) {
        try {
            return j.getBoolean(tag);
        } catch (JSONException | NullPointerException e) {
            return defaultValue;
        }
    }
}
