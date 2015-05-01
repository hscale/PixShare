package com.appofy.android.pixshare;
/**
 * Created by Mihir on 4/25/2015.
 */
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.appofy.android.pixshare.adapter.AlbumGridViewAdapter;
import com.appofy.android.pixshare.helper.AlbumGridViewHelper;
import com.appofy.android.pixshare.util.AlbumGridConstants;
import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class AlbumGridActivity extends ActionBarActivity {

    private AlbumGridViewHelper mAlbumGridViewHelper;
    private ArrayList<String> mImagePaths = new ArrayList<String>();
    private ArrayList<Integer> mPhotoIds = new ArrayList<Integer>();
    private AlbumGridViewAdapter mAdapter;
    private GridView mGridView;
    private int mColumnWidth;


    String url = "http://52.8.12.67:8080/pixsharebusinessservice/rest";
    String sectionurl = "/photo";
    String suburl = "/album/photos";
    String desturl = null;
    int albumId;
    ArrayList<String> imagePaths = new ArrayList<String>();
    ArrayList<Integer> photoIds = new ArrayList<Integer>();
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_grid);
        mGridView = (GridView) findViewById(R.id.album_grid_view);

        mAlbumGridViewHelper = new AlbumGridViewHelper(this);

        // Initilizing Grid View
        initilizeGridLayout();

        // loading all image paths from SD card
        //mImagePaths = mAlbumGridViewHelper.getFilePaths();
        //mPhotoIds = mAlbumGridViewHelper.getPhotoIds();
        //System.out.println(mImagePaths);
        //System.out.println(mPhotoIds);

        desturl = url + sectionurl + suburl;

        albumId = getIntent().getIntExtra("albumId",0);

        if(albumId != 0) {
            // Make RESTful webservice call using AsyncHttpClient object
            AsyncHttpClient client = new AsyncHttpClient();

            RequestParams chkParams = new RequestParams();
            chkParams.put("albumId", albumId);
            System.out.println("AlbumId:" + albumId);
            client.get(desturl, chkParams, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    try {
                        JSONObject photosJSON = new JSONObject(new String(response));
                        if (photosJSON.getString("responseFlag").equals("success")) {
                            JSONArray photos = photosJSON.getJSONArray("photos");
                            int count = photos.length();

                            for (int i = 0; i < count; i++) {
                                JSONObject photo = photos.getJSONObject(i);
                                photoIds.add(i, photo.getInt("photoId"));
                                imagePaths.add(i, photo.getString("imagePath"));
                            }

                            System.out.println(photoIds);
                            System.out.println(imagePaths);

                            mAlbumGridViewHelper.setFilePaths(imagePaths);
                            mAlbumGridViewHelper.setPhotoIds(photoIds);

                            // Gridview mAdapter
                            mAdapter = new AlbumGridViewAdapter(AlbumGridActivity.this, imagePaths, photoIds,
                                    mColumnWidth);

                            // setting grid view mAdapter
                            mGridView.setAdapter(mAdapter);
                        } else {
                            System.out.println("Something went wrong, please contact Admin");
                        }
                    } catch (Exception e) {
                        System.out.println("Error Occurred!");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // When Http response code is '404'
                    if (statusCode == 404) {
                        System.out.println("Requested resource not found");
                    }
                    // When Http response code is '500'
                    else if (statusCode == 500) {
                        System.out.println("Something went wrong at server end");
                    }
                    // When Http response code other than 404, 500
                    else {
                        System.out.println("Unexpected Error occurred, Check Internet Connection!");
                    }
                }
            });
        }

    }

    private void initilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AlbumGridConstants.GRID_PADDING, r.getDisplayMetrics());

        mColumnWidth = (int) ((mAlbumGridViewHelper.getScreenWidth() - ((AlbumGridConstants.NUM_OF_COLUMNS + 1) * padding)) / AlbumGridConstants.NUM_OF_COLUMNS);

        mGridView.setNumColumns(AlbumGridConstants.NUM_OF_COLUMNS);
        mGridView.setColumnWidth(mColumnWidth);
        mGridView.setStretchMode(GridView.NO_STRETCH);
        mGridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        mGridView.setHorizontalSpacing((int) padding);
        mGridView.setVerticalSpacing((int) padding);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        if(getIntent().getIntExtra("menuFlag",0) == 2)
            getMenuInflater().inflate(R.menu.menu_album_grid_2, menu);
        else
            getMenuInflater().inflate(R.menu.menu_album_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(getIntent().getIntExtra("menuFlag",0) == 1) {
            switch (item.getItemId()) {
                case R.id.add_photos_to_album:
                    Intent addAlbumIntent = new Intent(this, AddNewPhotoActivity.class);
                    addAlbumIntent.putExtra("albumId", albumId);
                    startActivity(addAlbumIntent);
                    return true;

                case R.id.share_this_album:
                    Intent shareAlbumIntent = new Intent(this, ShareAlbumActivity.class);
                    shareAlbumIntent.putExtra("albumId", albumId);
                    startActivity(shareAlbumIntent);
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
                    session = new SessionManager(getApplicationContext());
                    session.logoutUser();
                    return true;
                case R.id.my_profile:
                    Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
                    startActivity(myProfileIntent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        else
        {
            switch (item.getItemId()) {
                case R.id.invite_friends:
                    Intent inviteFriendsIntent = new Intent(this, InviteFriendsActivity.class);
                    startActivity(inviteFriendsIntent);
                    return true;
                case R.id.friend_request:
                    Intent pendingFriendRequestIntent = new Intent(this, PendingFriendRequestActivity.class);
                    startActivity(pendingFriendRequestIntent);
                    return true;
                case R.id.signout:
                    session = new SessionManager(getApplicationContext());
                    session.logoutUser();
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
}
