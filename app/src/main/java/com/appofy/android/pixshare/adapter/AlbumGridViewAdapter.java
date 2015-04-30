package com.appofy.android.pixshare.adapter;

/**
 * Created by Mihir on 4/25/2015.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.appofy.android.pixshare.AlbumFullscreenActivity;
import com.appofy.android.pixshare.AlbumGridActivity;

public class AlbumGridViewAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<String> mFilePaths = new ArrayList<String>();
    private ArrayList<Integer> mPhotoIds = new ArrayList<Integer>();
    private int mImageWidth;
    Bitmap bitmap;

    public AlbumGridViewAdapter(Activity activity, ArrayList<String> filePaths, ArrayList<Integer> photoIds,
                                int mImageWidth) {
        this.mActivity = activity;
        this.mFilePaths = filePaths;
        this.mPhotoIds = photoIds;
        this.mImageWidth = mImageWidth;
    }

    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    class OnImageClickListener implements OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(mActivity, AlbumFullscreenActivity.class);
            i.putExtra("position", _postion);
            i.putExtra("filePaths",mFilePaths);
            i.putExtra("photoIds",mPhotoIds);
            mActivity.startActivity(i);
        }
    }

    @Override
    public int getCount() {
        return this.mFilePaths.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mActivity);
        } else {
            imageView = (ImageView) convertView;
        }

        // get screen dimensions
        /*Bitmap image = decodeFile(mFilePaths.get(position), mImageWidth,
                mImageWidth);*/

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(mImageWidth,
                mImageWidth));

        System.out.println("Image URI:" + mFilePaths.get(position));

        //URL url = new URL(mFilePaths.get(position));

        class LoadImage extends AsyncTask<Integer, String, Bitmap> {

            int position;
            protected Bitmap doInBackground(Integer... args) {
                try {
                    position = args[0];
                    bitmap = BitmapFactory.decodeStream((InputStream)new URL(mFilePaths.get(args[0])).getContent());


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;

            }

            protected void onPostExecute(Bitmap image) {

                if(image != null){
                imageView.setImageBitmap(image);
                // image view click listener
                imageView.setOnClickListener(new OnImageClickListener(position));

                }else{
                    System.out.println("Image Does Not exist or Network Error");

                }
            }
        }

        new LoadImage().execute(position);




        //imageView.setImageBitmap(imageView);
        // image view click listener
        //imageView.setOnClickListener(new OnImageClickListener(position));


        return imageView;
    }

    @Override
    public Object getItem(int position) {
        return this.mFilePaths.get(position);
    }

    /*private class LoadImage extends AsyncTask<Integer, String, Bitmap[]> {

        int position;
        protected Bitmap[] doInBackground(Integer... args) {
            try {
                //position = args[0];
                //bitmap = BitmapFactory.decodeStream((InputStream)new URL(mFilePaths.get(args[0])).getContent());
                for(int i = 0 ; i<mFilePaths.size();i++) {
                    bitmaps[i] = BitmapFactory.decodeStream((InputStream)new URL(mFilePaths.get(i)).getContent());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //return bitmap;
            return bitmaps;
        }

        protected void onPostExecute(Bitmap[] image) {

            if(image != null){
                /*imageView.setImageBitmap(image);
                // image view click listener
                imageView.setOnClickListener(new OnImageClickListener(position));
*/
       /*     }else{


               System.out.println("Image Does Not exist or Network Error");

            }
        }
    }*/

}
