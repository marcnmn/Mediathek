package com.marcn.mediathek.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.google.gson.Gson;
import com.marcn.mediathek.base_objects.Series;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Storage {
    public static void downloadFile(Activity activity, String url, String title) {
        if (!getWritePermission(activity)) return;
        String filename = title + url.substring(url.lastIndexOf("."));

        Uri uri = Uri.parse(url);
        DownloadManager.Request r = new DownloadManager.Request(uri);
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager dm = (DownloadManager) activity.getSystemService(Activity.DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }

    public static boolean getWritePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else
            return true;
    }

    public static String saveBitmapOnDisk(Context context, Bitmap bitmap) {
        String fileName = "thumbnail";  //no .png or .jpg needed
        return saveBitmapOnDisk(context, bitmap, fileName);
    }

    public static String saveBitmapOnDisk(Context context, Bitmap bitmap, String fileName) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bytes);
            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public static String saveSeriesOnDisk(Context context, ArrayList<Series> data, String filename) {
        Gson gson = new Gson();
        String s = gson.toJson(data);

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(s.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filename;
    }

    public static ArrayList<Series> getSeriesFromFile(Context context, String filename) {
        try {
            FileInputStream fis = context.openFileInput(filename);

            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            Gson gson = new Gson();
            Series[] sArray = gson.fromJson(json, Series[].class);
            ArrayList<Series> series = new ArrayList<>(Arrays.asList(sArray));
            return series;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
