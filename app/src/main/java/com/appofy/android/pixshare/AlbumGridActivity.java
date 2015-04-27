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

public class AlbumGridActivity extends Activity {

    private AlbumGridViewHelper mAlbumGridViewHelper;
    private ArrayList<String> mImagePaths = new ArrayList<String>();
    private AlbumGridViewAdapter mAdapter;
    private GridView mGridView;
    private int mColumnWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_grid);

        mGridView = (GridView) findViewById(R.id.album_grid_view);

        mAlbumGridViewHelper = new AlbumGridViewHelper(this);

        // Initilizing Grid View
        initilizeGridLayout();

        // loading all image paths from SD card
        mImagePaths = mAlbumGridViewHelper.getFilePaths();

        // Gridview mAdapter
        mAdapter = new AlbumGridViewAdapter(AlbumGridActivity.this, mImagePaths,
                mColumnWidth);

        // setting grid view mAdapter
        mGridView.setAdapter(mAdapter);
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
