package com.appofy.android.pixshare;
/**
 * Created by Mihir on 4/25/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.appofy.android.pixshare.adapter.AlbumFullscreenImageAdapter;
import com.appofy.android.pixshare.helper.AlbumGridViewHelper;


public class AlbumFullscreenActivity extends Activity {
    private AlbumGridViewHelper mAlbumGridViewHelper;
    private AlbumFullscreenImageAdapter mAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_fullscreen);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mAlbumGridViewHelper = new AlbumGridViewHelper(getApplicationContext());

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);

        mAdapter = new AlbumFullscreenImageAdapter(AlbumFullscreenActivity.this,
                mAlbumGridViewHelper.getFilePaths());

        mViewPager.setAdapter(mAdapter);

        // displaying selected image first
        mViewPager.setCurrentItem(position);
    }
}
