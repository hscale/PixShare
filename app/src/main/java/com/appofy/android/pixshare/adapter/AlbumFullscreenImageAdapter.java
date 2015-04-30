package com.appofy.android.pixshare.adapter;
/**
 * Created by Mihir on 4/25/2015.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appofy.android.pixshare.R;
import com.appofy.android.pixshare.helper.AlbumTouchImageView;

public class AlbumFullscreenImageAdapter extends PagerAdapter {
    private Activity mActivity;
    private ArrayList<String> mImagePaths;
    private ArrayList<Integer> mPhotoIds;
    private LayoutInflater mInflater;
    Bitmap bitmap;
    AlbumTouchImageView imgDisplay;
    // constructor
    public AlbumFullscreenImageAdapter(Activity activity,
                                  ArrayList<String> imagePaths, ArrayList<Integer> photoIds) {
        System.out.println("In AlbumFullscreenImageAdapter Constructor"+imagePaths.toString());
        this.mActivity = activity;
        this.mImagePaths = imagePaths;
        this.mPhotoIds = photoIds;
    }

    @Override
    public int getCount() {
        return this.mImagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        System.out.println("In instantiateItem");

        Button btnClose;

        mInflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = mInflater.inflate(R.layout.layout_fullscreen_image, container,
                false);
        System.out.println("Image Paths:"+mImagePaths.toString());
        imgDisplay = (AlbumTouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);

        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePaths.get(position), options);
        imgDisplay.setImageBitmap(bitmap);*/
        new LoadImage().execute(position);
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }


    private class LoadImage extends AsyncTask<Integer, String, Bitmap> {

        int position;
        protected Bitmap doInBackground(Integer... args) {
            try {
                position = args[0];
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(mImagePaths.get(args[0])).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                imgDisplay.setImageBitmap(image);


            }else{


                System.out.println("Image Does Not exist or Network Error");

            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
