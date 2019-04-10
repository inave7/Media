package com.belaku.media;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private static final int MY_P_REQ = 1;
    ViewPager viewPager;
    PagerAdapter adapter;
    String[] Title = new String[] { "Music", "Images", "Videos"};
    private boolean permission;
    private Cursor musicCursor, musicCursorAlbumArt;

    private Map<String, String> mapAlbumArtIDs = new HashMap();
    private ArrayList<String> songNames = new ArrayList<>();
    private ArrayList<AudioSong> mAudioSongs = new ArrayList<>();
    ArrayList<String> listOfAllImages = new ArrayList<String>();
    private int thum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        viewPager = (ViewPager) findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapter(MainActivity.this, Title);
        // Binds the Adapter to the ViewPager

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    makeToast("Audio");
                    {
                        MediaCheckPermission();

                        if (permission) {
                            if (songNames.size() == 0)
                                ReadAudio();

                            if (songNames.size() > 0)
                                ViewPagerAdapter.recyclerView.setAdapter(new MyRvAdapter(songNames));
                            else makeToast("NO songs :/");
                        }

                    }
                }
                else if (position == 1) {
                    makeToast("Images");
                    {
                        Uri uri;
                        Cursor cursor;
                        int column_index;
                        StringTokenizer st1;

                        String absolutePathOfImage = null;
                        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                        String[] projection = { MediaStore.MediaColumns.DATA };

                        cursor = getContentResolver().query(uri, projection, null,
                                null, null);

                        column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                        while (cursor.moveToNext()) {
                            absolutePathOfImage = cursor.getString(column_index);
                            listOfAllImages.add(absolutePathOfImage);
                        }

                        makeToast("No. of images in Device - " + listOfAllImages.size());

                    }
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
                    ViewPagerAdapter.recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    GalleryAdaptor galleryAdapter = new GalleryAdaptor(MainActivity.this, listOfAllImages);
                    ViewPagerAdapter.recyclerView.setAdapter(galleryAdapter);

                    ViewPagerAdapter.recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);

                }
                else if (position == 2)
                    makeToast("Videos");

                //fetching videos from Gallery
                Uri uri;
                Cursor cursor;
                int columnIndexData, columnIndexFolderName, columnId, thumbnail;

                String adsolutePathImg = null;
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                String[] projection = {MediaStore.MediaColumns.DATA,
                                        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                                        MediaStore.Video.Media._ID,
                                        MediaStore.Video.Thumbnails.DATA};

                String orderBy = MediaStore.Images.Media.DATE_TAKEN;

                cursor = getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

                columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

                while (cursor.moveToNext()) {
                    adsolutePathImg = cursor.getString(columnIndexData);

                    VideoModel videoModel = new VideoModel();



                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            viewPager.setAdapter(adapter);
        } else ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_P_REQ);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_P_REQ)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewPager.setAdapter(adapter);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void MediaCheckPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //    makeToast("Storage permission granted already");
            permission = true;

        } else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_P_REQ);
    }


    private ArrayList<AudioSong> ReadAudio() {
        Uri musicUrl;
        ContentResolver mContentResolver = getContentResolver();

        musicUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // Perform a query on the content resolver
        musicCursor = mContentResolver.query(musicUrl, null, MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        if (musicCursor == null) {
            // Query failed...
            makeToast("Failed to retrieve music: cursor is null :-(");
            return mAudioSongs;
        }
        if (!musicCursor.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            makeToast("Failed to move cursor to first row (no query results).");
            return mAudioSongs;
        }

        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

        do {
            try {
                AudioSong audioSong = new AudioSong(
                        musicCursor.getLong(idColumn),
                        null,
                        musicCursor.getString(pathColumn),
                        musicCursor.getString(artistColumn),
                        musicCursor.getString(titleColumn),
                        musicCursor.getString(albumColumn),
                        musicCursor.getLong(durationColumn),
                        false);
                mAudioSongs.add(audioSong);
            } catch (IllegalStateException ex) {
                //    makeToast("ILLEXP" + ex);
            }
        } while (musicCursor.moveToNext());

        ContentResolver musicResolve = getContentResolver();
        Uri smusicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;


        musicCursorAlbumArt = musicResolve.query(smusicUri, null         //should use where clause(_ID==albumid)
                , null, null, null);

        if (musicCursorAlbumArt == null) {
            // Query failed...
            makeToast("Failed to retrieve music: cursor is null :-(");
            return mAudioSongs;
        }
        if (!musicCursorAlbumArt.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            makeToast("Failed to move cursor to first row (no query results).");
            return mAudioSongs;
        }

        int albumArtColumn = musicCursorAlbumArt.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART);
        int albumIdColumn = musicCursorAlbumArt.getColumnIndex(MediaStore.Audio.Media.ALBUM);


        do {
            try {
                musicCursorAlbumArt.getString(albumArtColumn);
                mapAlbumArtIDs.put(musicCursorAlbumArt.getString(albumIdColumn), musicCursorAlbumArt.getString(albumArtColumn));
            } catch (IllegalStateException ex) {
                //   makeToast("ILLEXP???" + ex);
            }
        } while (musicCursorAlbumArt.moveToNext());

        ArrayList keyAlbumIdList = new ArrayList();
        keyAlbumIdList.addAll(mapAlbumArtIDs.keySet());

        for (int i = 0; i < mAudioSongs.size(); i++) {
            for (int u = 0; u < mapAlbumArtIDs.keySet().size(); u++) {
                if (mAudioSongs.get(i).getAlbum().equals(keyAlbumIdList.get(u))) {
                    if (mapAlbumArtIDs.get(mAudioSongs.get(i).getAlbum()) != null) {
                        mAudioSongs.get(i).setAlbumArt(mapAlbumArtIDs.get(mAudioSongs.get(i).getAlbum()));
                        Log.d("HERE??? - ", mapAlbumArtIDs.get(mAudioSongs.get(i).getAlbum()));
                    }
                }
            }
        }


        for (int i = 0; i < mAudioSongs.size(); i++) {
            if (!(songNames.contains(mAudioSongs.get(i).getTitle().toString())))
                songNames.add(mAudioSongs.get(i).getTitle().toString());
        }
        return mAudioSongs;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
