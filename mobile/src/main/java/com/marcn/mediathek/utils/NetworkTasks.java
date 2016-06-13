package com.marcn.mediathek.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.marcn.mediathek.model.base.Asset;
import com.marcn.mediathek.network.Zdf;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkTasks {

    public static List<Asset> listEpisodes() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://heute-api.live.cellular.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String resp = "{\n" +
                "\t\"ergebnis\": {\n" +
                "\t\t\"additionalTeaser\": true,\n" +
                "\t\t\"teaser\": [{\n" +
                "\t\t\t\"type\": \"video\",\n" +
                "\t\t\t\"contextLink\": \"http:\\/\\/zdfneo.de\\/\",\n" +
                "\t\t\t\"teaserBild\": {\n" +
                "\t\t\t\t\"75\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg75x52blob\\/11465034\",\n" +
                "\t\t\t\t\t\"width\": 75,\n" +
                "\t\t\t\t\t\"height\": 52,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"476\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg476x176blob\\/11465033\",\n" +
                "\t\t\t\t\t\"width\": 476,\n" +
                "\t\t\t\t\t\"height\": 176,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"298\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg298x168blob\\/11465036\",\n" +
                "\t\t\t\t\t\"width\": 298,\n" +
                "\t\t\t\t\t\"height\": 168,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"404\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg404x227blob\\/11465041\",\n" +
                "\t\t\t\t\t\"width\": 404,\n" +
                "\t\t\t\t\t\"height\": 227,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"276\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg276x155blob\\/11465029\",\n" +
                "\t\t\t\t\t\"width\": 276,\n" +
                "\t\t\t\t\t\"height\": 155,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"644\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg644x363blob\\/11465038\",\n" +
                "\t\t\t\t\t\"width\": 644,\n" +
                "\t\t\t\t\t\"height\": 363,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"144\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg144x81blob\\/11465040\",\n" +
                "\t\t\t\t\t\"width\": 144,\n" +
                "\t\t\t\t\t\"height\": 81,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"485\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg485x273blob\\/11465028\",\n" +
                "\t\t\t\t\t\"width\": 485,\n" +
                "\t\t\t\t\t\"height\": 273,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"94\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg94x65blob\\/11465026\",\n" +
                "\t\t\t\t\t\"width\": 94,\n" +
                "\t\t\t\t\t\"height\": 65,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"72\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg72x54blob\\/11465032\",\n" +
                "\t\t\t\t\t\"width\": 72,\n" +
                "\t\t\t\t\t\"height\": 54,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"173\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg173x120blob\\/11465027\",\n" +
                "\t\t\t\t\t\"width\": 173,\n" +
                "\t\t\t\t\t\"height\": 120,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"236\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg236x133blob\\/11465037\",\n" +
                "\t\t\t\t\t\"width\": 236,\n" +
                "\t\t\t\t\t\"height\": 133,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"116\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg116x54blob\\/11465030\",\n" +
                "\t\t\t\t\t\"width\": 116,\n" +
                "\t\t\t\t\t\"height\": 54,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"672\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg672x378blob\\/11465039\",\n" +
                "\t\t\t\t\t\"width\": 672,\n" +
                "\t\t\t\t\t\"height\": 378,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"946\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402140\\/timg946x532blob\\/11465042\",\n" +
                "\t\t\t\t\t\"width\": 946,\n" +
                "\t\t\t\t\t\"height\": 532,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Die untergehende Sonne.\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t\"titel\": \"Paradiese und Piraten - Folge 3\",\n" +
                "\t\t\t\"beschreibung\": \"Von Kenia an das Horn von Afrika: Entlang der traumhaften K\\u00fcste Kenias zieht es den waghalsigen Journalisten Simon Reeve nach Mogadishu - der vom Krieg zerr\\u00fctteten Hauptstadt Somalias.\",\n" +
                "\t\t\t\"id\": \"2402140\",\n" +
                "\t\t\t\"length\": 2580,\n" +
                "\t\t\t\"airtime\": \"09.04.2016 06:10\",\n" +
                "\t\t\t\"timetolive\": \"20.04.2016 23:59\",\n" +
                "\t\t\t\"channel\": \"ZDFneo\",\n" +
                "\t\t\t\"originChannelId\": 180,\n" +
                "\t\t\t\"originChannelTitle\": \"Dokumentation\",\n" +
                "\t\t\t\"otherChannels\": [2298110, 2298126, 857392],\n" +
                "\t\t\t\"member\": \"morgens\"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"type\": \"video\",\n" +
                "\t\t\t\"contextLink\": \"http:\\/\\/zdfneo.de\\/\",\n" +
                "\t\t\t\"teaserBild\": {\n" +
                "\t\t\t\t\"75\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg75x52blob\\/12895020\",\n" +
                "\t\t\t\t\t\"width\": 75,\n" +
                "\t\t\t\t\t\"height\": 52,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"476\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg476x176blob\\/12895019\",\n" +
                "\t\t\t\t\t\"width\": 476,\n" +
                "\t\t\t\t\t\"height\": 176,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"298\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg298x168blob\\/12895022\",\n" +
                "\t\t\t\t\t\"width\": 298,\n" +
                "\t\t\t\t\t\"height\": 168,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"404\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg404x227blob\\/12895027\",\n" +
                "\t\t\t\t\t\"width\": 404,\n" +
                "\t\t\t\t\t\"height\": 227,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"276\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg276x155blob\\/12895015\",\n" +
                "\t\t\t\t\t\"width\": 276,\n" +
                "\t\t\t\t\t\"height\": 155,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"644\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg644x363blob\\/12895024\",\n" +
                "\t\t\t\t\t\"width\": 644,\n" +
                "\t\t\t\t\t\"height\": 363,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"144\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg144x81blob\\/12895026\",\n" +
                "\t\t\t\t\t\"width\": 144,\n" +
                "\t\t\t\t\t\"height\": 81,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"485\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg485x273blob\\/12895014\",\n" +
                "\t\t\t\t\t\"width\": 485,\n" +
                "\t\t\t\t\t\"height\": 273,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"94\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg94x65blob\\/12895012\",\n" +
                "\t\t\t\t\t\"width\": 94,\n" +
                "\t\t\t\t\t\"height\": 65,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"72\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg72x54blob\\/12895018\",\n" +
                "\t\t\t\t\t\"width\": 72,\n" +
                "\t\t\t\t\t\"height\": 54,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"173\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg173x120blob\\/12895013\",\n" +
                "\t\t\t\t\t\"width\": 173,\n" +
                "\t\t\t\t\t\"height\": 120,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"236\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg236x133blob\\/12895023\",\n" +
                "\t\t\t\t\t\"width\": 236,\n" +
                "\t\t\t\t\t\"height\": 133,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"116\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg116x54blob\\/12895016\",\n" +
                "\t\t\t\t\t\"width\": 116,\n" +
                "\t\t\t\t\t\"height\": 54,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"672\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg672x378blob\\/12895025\",\n" +
                "\t\t\t\t\t\"width\": 672,\n" +
                "\t\t\t\t\t\"height\": 378,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"946\": {\n" +
                "\t\t\t\t\t\"url\": \"http:\\/\\/www.zdf.de\\/ZDFmediathek\\/contentblob\\/2402144\\/timg946x532blob\\/12895028\",\n" +
                "\t\t\t\t\t\"width\": 946,\n" +
                "\t\t\t\t\t\"height\": 532,\n" +
                "\t\t\t\t\t\"tooltipText\": \"Fischer im Meer.\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t\"titel\": \"Paradiese und Piraten - Folge 4\",\n" +
                "\t\t\t\"beschreibung\": \"Neben der sagenhaften Sch\\u00f6nheit und dem wahnsinnigen Get\\u00fcmmel entdeckt Simon auch eine Schmuggelroute zwischen Oman und Iran. Welchen Einfluss hat die explodierende Bev\\u00f6lkerungsdichte Indiens?\",\n" +
                "\t\t\t\"id\": \"2402144\",\n" +
                "\t\t\t\"length\": 2580,\n" +
                "\t\t\t\"airtime\": \"09.04.2016 06:55\",\n" +
                "\t\t\t\"timetolive\": \"20.04.2016 23:59\",\n" +
                "\t\t\t\"channel\": \"ZDFneo\",\n" +
                "\t\t\t\"originChannelId\": 180,\n" +
                "\t\t\t\"originChannelTitle\": \"Dokumentation\",\n" +
                "\t\t\t\"otherChannels\": [2298110, 2298126, 857392],\n" +
                "\t\t\t\"member\": \"morgens\"\n" +
                "\t\t}]\n" +
                "\t},\n" +
                "\t\"tracking\": {\n" +
                "\t\t\"cp\": \"ZDFde\\/0\\/\",\n" +
                "\t\t\"co\": \"SendungVerpasst\\/SendungVerpasst\\/MSendungVerpasst\"\n" +
                "\t}\n" +
                "}";

//        String resp2 = ""

        try {
            Gson gson = new Gson();
            Zdf ergebnis = gson.fromJson(resp, Zdf.class);
            JSONObject jsonObject = new JSONObject(resp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Api service = retrofit.create(Api.class);
//        Call<Zdf> repos = service.zdfListMissedEpisodes("2016-04-09");
//        try {
//            Response<Zdf> response = repos.execute();
//            Zdf episodes = response.body();
//            String ea = "";
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return null;
    }

    public static Bitmap downloadBitmap(String myurl) {
        InputStream is;
        Bitmap bitmap = null;
        URL url;
        try {
            url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static JSONObject downloadJSONData(String myurl) {
        InputStream is = null;
        String result;
        JSONObject jObject = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn;
            try {
                conn = (HttpsURLConnection) url.openConnection();
            } catch (ClassCastException e) {
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            result = sb.toString();
            jObject = new JSONObject(result);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jObject;
    }

    public static String downloadStringData(String myurl) {
        InputStream is = null;
        String result;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            result = sb.toString();
            return result;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static InputStream downloadStringDataAsInputStream(String myurl) {
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
