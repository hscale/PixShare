package com.appofy.android.pixshare;

import com.appofy.android.pixshare.adapter.SwipeTabsAdapter;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class LandingActivity extends ActionBarActivity {

    private ViewPager viewPager;
    private SwipeTabsAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;
    // Tab titles
    //private String[] tabs = { "My Albums", "Shared Albums", "Friends","Groups" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new SwipeTabsAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);


    }

}