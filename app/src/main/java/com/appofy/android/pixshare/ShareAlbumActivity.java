package com.appofy.android.pixshare;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.appofy.android.pixshare.adapter.ShareAlbumSwipeTabsAdapter;
import com.appofy.android.pixshare.adapter.SwipeTabsAdapter;


public class ShareAlbumActivity extends ActionBarActivity {

    private ViewPager viewPager;
    private ShareAlbumSwipeTabsAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_album);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.shareAlbumPager);
        actionBar = getSupportActionBar();
        mAdapter = new ShareAlbumSwipeTabsAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);


    }
}
