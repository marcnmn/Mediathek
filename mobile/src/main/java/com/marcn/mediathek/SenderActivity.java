package com.marcn.mediathek;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.base_objects.Channel;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Sendung;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.ui_fragments.VideoWidgetFragment;
import com.marcn.mediathek.utils.XmlParser;
import com.marcn.mediathek.utils.ZdfMediathekData;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeMap;

public class SenderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnVideoInteractionListener, AppBarLayout.OnOffsetChangedListener {

    public static final String INTENT_SENDER_JSON = "channel-json";
    public static final String INTENT_SENDER_ID = "channel-id";

    private Channel mChannel;
    private LiveStream mLiveStream;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private boolean mToolbarIsShown = false;
    private int mToolbarScrollRange = -1;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendung);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = toolbar.getContext();

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
            String json = intent.getStringExtra(INTENT_SENDER_JSON);
            Gson gson = new Gson();
            mChannel = gson.fromJson(json, Channel.class);
        }

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

//        getIntentThumbnail();
        downloadLiveStreamData();
    }

    private void downloadLiveStreamData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLiveStream = XmlParser.getLivestreamFromChannel(mContext, mChannel);
                if (mLiveStream == null) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupHeaderView(mLiveStream);
                    }
                });
            }
        }).start();
    }

    private void loadWidgets() {
        loadWidget(VideoWidgetFragment.WIDGET_TYPE_SENDUNG_LAST, R.id.widgetLast);
        loadWidget(VideoWidgetFragment.WIDGET_TYPE_SENDUNG_MOST_POPULAR, R.id.widgetMost);
        loadWidget(VideoWidgetFragment.WIDGET_TYPE_SENDUNG_FURTHER, R.id.widgetFurther);
    }

    private void setupHeaderView(LiveStream liveStream) {
        if (liveStream == null) return;
//        if (findViewById(R.id.imageChannel) != null)
//            ((ImageView) findViewById(R.id.imageChannel)).setImageResource(sendung.channel.getLogoResId());
        if (findViewById(R.id.textTitle) != null)
            ((TextView) findViewById(R.id.textTitle)).setText(liveStream.getTitle());

//        if (findViewById(R.id.textDetail) != null)
//            ((TextView) findViewById(R.id.textDetail)).setText(channel.detail);

        ImageView thumbnail = (ImageView) findViewById(R.id.imageThumbnail);
        if (thumbnail != null && liveStream.thumb_url != null)
            Picasso.with(this)
                    .load(liveStream.thumb_url)
                    .config(Bitmap.Config.RGB_565)
                    .into(thumbnail);
    }

    private void getIntentThumbnail() {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(openFileInput("thumbnail"));
            ((ImageView) findViewById(R.id.imageThumbnail)).setImageBitmap(bitmap);
            Palette p = Palette.from(bitmap).generate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int themeColor = p.getDarkVibrantColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                //getWindow().setStatusBarColor(themeColor);
                if (findViewById(R.id.toolbar_layout) != null) {
                    findViewById(R.id.toolbar_layout).setBackgroundColor(themeColor);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_LIVE_DRAWER_ITEM, id);
        startActivity(intent);
    }

    private void loadWidget(int Type, int resId) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(resId, VideoWidgetFragment.newInstance(mSendung, Type));
//        transaction.commit();
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

    @Override
    public void onSendungClicked(Sendung sendung, View thumbnail, View logo) {
        if (sendung == null) return;
        Toast.makeText(this, sendung.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChannelClicked(Channel channel, View thumbnail) {
        if (channel == null) return;
        Toast.makeText(this, channel.title, Toast.LENGTH_SHORT).show();
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
            getWindow().setExitTransition(new Fade());

            view.setTransitionName("thumb");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(view, "thumb"));
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
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

    private boolean getWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mToolbarScrollRange == -1) {
            mToolbarScrollRange = appBarLayout.getTotalScrollRange();
        }
        if (mToolbarScrollRange + verticalOffset == 0) {
            if (mLiveStream != null)
                mCollapsingToolbarLayout.setTitle(mLiveStream.title);
            mToolbarIsShown = true;
        } else if (mToolbarIsShown) {
            mCollapsingToolbarLayout.setTitle("");
            mToolbarIsShown = false;
        }
    }
}
