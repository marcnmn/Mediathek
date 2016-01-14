package com.marcn.mediathek.utils;

import android.util.Xml;
import com.marcn.mediathek.base_objects.LiveStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class XmlParser {

    public static ArrayList<LiveStream> parseLiveStreams(InputStream in) throws IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } catch (Exception ignored) {
        } finally {
            in.close();
        }
        return null;
    }

    private static ArrayList<LiveStream> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<LiveStream> liveStreams = new ArrayList<>();
        String title, id = "", thumbnail = "";

        parser.require(XmlPullParser.START_TAG, null, "response");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("teaser") && !parser.getAttributeValue(null, "member").equals("onAir"))
                skip(parser);
            else if (name.equals("teaserimage")) {
                if (parser.getAttributeValue(null, "key").equals("946x532")) {
                    thumbnail = readText(parser);
                }
            } else if (name.equals("originChannelId")) {
                id = readText(parser);
            } else if (name.equals("channel")) {
                title = readText(parser);
                LiveStream ls = new LiveStream(id, title, thumbnail);
                if (liveStreams.indexOf(ls) < 0)
                    liveStreams.add(ls);
            }
        }
        return liveStreams;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
