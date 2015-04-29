package com.appofy.android.pixshare;
/**
 * Created by Mihir on 4/25/2015.
 */
import android.os.Bundle;
import java.util.ArrayList;
import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.GridView;

import com.appofy.android.pixshare.adapter.AlbumGridViewAdapter;
import com.appofy.android.pixshare.helper.AlbumGridViewHelper;
import com.appofy.android.pixshare.util.AlbumGridConstants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class AlbumGridActivity extends Activity {

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

        // TODO: Need to fetch from session.
        albumId = 5;

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams chkParams = new RequestParams();
        chkParams.put("albumId", albumId);

        client.get(desturl, chkParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject photosJSON = new JSONObject(new String(response));
                    if (photosJSON.getString("responseFlag").equals("success")) {
                        JSONArray photos = photosJSON.getJSONArray("photos");
                        int count = photos.length();

                        for(int i=0 ; i< count; i++){
                            JSONObject photo = photos.getJSONObject(i);
                            photoIds.add(i,photo.getInt("photoId"));
                            imagePaths.add(i, photo.getString("imagePath"));
                        }

                        System.out.println(photoIds);
                        System.out.println(imagePaths);


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
}
