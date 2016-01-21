package com.marcn.mediathek;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.transition.Explode;
import android.util.Pair;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.ui_fragments.LiveStreamsFragment;
import com.marcn.mediathek.ui_fragments.SendungListFragment;
import com.marcn.mediathek.ui_fragments.VideoListFragment;
import com.marcn.mediathek.utils.ZdfMediathekData;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnVideoInteractionListener {

    public static final String INTENT_LIVE_DRAWER_ITEM = "player-drawer-item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            int navId = intent.getIntExtra(INTENT_LIVE_DRAWER_ITEM, -1);
            navigationIdReceived(navId);
        } else {
            loadCleanFragment(LiveStreamsFragment.newInstance(1), false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        navigationIdReceived(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void navigationIdReceived(int id) {
        if (id == R.id.nav_live) {
            loadCleanFragment(new LiveStreamsFragment());
        } else if (id == R.id.nav_gallery) {
            loadCleanFragment(new VideoListFragment());
        } else if (id == R.id.nav_zdf_mediathek) {
            loadCleanFragment(new SendungListFragment());
        } else if (id == R.id.nav_arte_mediathek) {
        } else if (id == R.id.nav_ard_mediathek) {
        } else if (id == R.id.nav_manage) {

        }
    }

    private void loadCleanFragment(Fragment fragment) {
        loadCleanFragment(fragment, true);
    }

    private void loadCleanFragment(Fragment fragment, boolean backstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.content_main, fragment, "0");
        if (backstack)
            transaction.addToBackStack("0");
        transaction.commit();
    }

    public void setActionBarTitle(int resId) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(resId);
    }

    @Override
    public void onLiveStreamClicked(final LiveStream liveStream, final View view, final int videoAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playByUrl(liveStream.getLiveM3U8(view.getContext()), view, videoAction, liveStream.channel);
            }
        });
    }

    @Override
    public void onVideoClicked(final Video video, final View view, final int videoAction) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TreeMap<Integer, String> s = ZdfMediathekData.getVideoUrl(view.getContext(), video.assetId);
                    if (s != null && !s.isEmpty())
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playByUrl(s.get(0), view, videoAction, video.title);
                            }
                        });
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    public void playByUrl(final String url, final View view, final int internalPlayer, final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (internalPlayer) {
                    case Video.ACTION_INTERNAL_PLAYER:
                        startInternalPlayer(url, view);
                        break;
                    case Video.ACTION_EXTERNAL_PLAYER_DIALOG:
                        startExternalPlayerDialog(url, title);
                        break;
                    case Video.ACTION_DEFAULT_EXTERNAL_PLAYER:
                        startDefaultExternalPlayer(url);
                        break;
                    case Video.ACTION_DOWNLOAD:
                        downloadFile(url, title);
                        break;
                }
            }
        });
    }

    public void startInternalPlayer(String url, View view) {
        if (url == null) return;
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.INTENT_LIVE_STREAM_URL, url);

        ImageView imageView = (ImageView) view;
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        createImageFromBitmap(bmp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());

            view.setTransitionName("thumb");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(view, "thumb"));
            startActivity(intent, options.toBundle());
        } else
            startActivity(intent);
    }

    public void startExternalPlayerDialog(String url, String title) {
        if (url == null) return;
        Intent stream = new Intent(Intent.ACTION_VIEW);
        stream.setDataAndType(Uri.parse(url), "video/*");
        startActivity(Intent.createChooser(stream, getResources().getText(R.string.send_to_intent) + " für " + title));
    }

    public void startDefaultExternalPlayer(String url) {
        if (url == null) return;
        Intent stream = new Intent(Intent.ACTION_VIEW);
        stream.setDataAndType(Uri.parse(url), "video/*");
        startActivity(stream);
    }

    public void downloadFile(String url, String title) {
        if (!getWritePermission()) return;
        String filename = title + url.substring(url.lastIndexOf("."));

        Uri uri = Uri.parse(url);
        DownloadManager.Request r = new DownloadManager.Request(uri);
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }

//    public void showChooser(final String url, final String channel) {
//        RelativeLayout rl = (RelativeLayout)findViewById(R.id.contentContainer);
//        TransitionManager.beginDelayedTransition(rl, new Slide());
//        findViewById(R.id.player_chooser).setVisibility(View.VISIBLE);
//
//        findViewById(R.id.player_choose).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startExternalPlayerDialog(url, channel);
//                findViewById(R.id.player_chooser).setVisibility(View.GONE);
//            }
//        });
//        findViewById(R.id.player_default).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startDefaultExternalPlayer(url);
//                findViewById(R.id.player_chooser).setVisibility(View.GONE);
//            }
//        });
//        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                downloadFile(url, channel);
//                findViewById(R.id.player_chooser).setVisibility(View.GONE);
//            }
//        });
//        findViewById(R.id.yatse).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareYatse(url);
//                findViewById(R.id.player_chooser).setVisibility(View.GONE);
//            }
//        });
//    }

    public void shareYatse(String url) {
        Intent sharingIntent = new Intent(Intent.ACTION_VIEW);
        sharingIntent.setDataAndType(Uri.parse(url), "video/*");
        sharingIntent.setClassName("org.leetzone.android.yatsewidgetfree", "org.leetzone.android.yatsewidget.ui.SendToActivity");
        startActivity(sharingIntent);
    }

    private boolean getWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else
            return true;
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "thumbnail";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

}
