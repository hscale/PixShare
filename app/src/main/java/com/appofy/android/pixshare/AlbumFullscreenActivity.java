package com.appofy.android.pixshare;
/**
 * Created by Mihir on 4/25/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.appofy.android.pixshare.adapter.AlbumFullscreenImageAdapter;
import com.appofy.android.pixshare.helper.AlbumGridViewHelper;
import com.appofy.android.pixshare.util.SessionManager;

import java.util.ArrayList;


public class AlbumFullscreenActivity extends Activity {
    private AlbumGridViewHelper mAlbumGridViewHelper;
    private AlbumFullscreenImageAdapter mAdapter;
    private ViewPager mViewPager;
    ArrayList<Integer> mPhotoIds;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_fullscreen);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        //mAlbumGridViewHelper = new AlbumGridViewHelper(getApplicationContext());

        Intent i = getIntent();
        position = i.getIntExtra("position", 0);

        mPhotoIds = i.getIntegerArrayListExtra("photoIds");
        System.out.println("Position:" + position);
        mAdapter = new AlbumFullscreenImageAdapter(AlbumFullscreenActivity.this,
                i.getStringArrayListExtra("filePaths"),i.getIntegerArrayListExtra("photoIds"));

        mViewPager.setAdapter(mAdapter);

        // displaying selected image first
        mViewPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_fullscreen_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewcomments:
                Intent i = new Intent(this, ViewCommentsActivity.class);
                i.putExtra("photoId",mPhotoIds.get(mViewPager.getCurrentItem()));
                System.out.println("mViewPager.getCurrentItem():"+mViewPager.getCurrentItem());
                startActivity(i);
                return true;
            case R.id.invite_friends:
                Intent inviteFriendsIntent = new Intent(this, InviteFriendsActivity.class);
                startActivity(inviteFriendsIntent);
                return true;
            case R.id.friend_request:
                Intent pendingFriendRequestIntent = new Intent(this, PendingFriendRequestActivity.class);
                startActivity(pendingFriendRequestIntent);
                return true;
            case R.id.signout:

                return true;
            case R.id.my_profile:
                Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
                startActivity(myProfileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
