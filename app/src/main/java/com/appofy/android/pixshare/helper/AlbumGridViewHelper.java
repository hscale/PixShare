package com.appofy.android.pixshare.helper;
/**
 * Created by Mihir on 4/25/2015.
*/

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.provider.MediaStore;

import com.appofy.android.pixshare.R;
import com.appofy.android.pixshare.util.AlbumGridConstants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class AlbumGridViewHelper {

    private Context mContext;

    String url = "http://52.8.12.67:8080/pixsharebusinessservice/rest";
    String sectionurl = "/photo";
    String suburl = "/album/photos";
    String desturl = null;
    int albumId;
    ArrayList<String> imagePaths = new ArrayList<String>();
    ArrayList<Integer> photoIds = new ArrayList<Integer>();

    // constructor
    public AlbumGridViewHelper(Context context) {
        desturl = url + sectionurl + suburl;
        this.mContext = context;
    }


    /*
         * getting screen width
    */

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }


    // Reading file paths from memory
    public ArrayList<String> getFilePaths() {

        new AlbumGridViewHelperTask().execute();

        return imagePaths;
    }


    private class AlbumGridViewHelperTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String ... params){

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
            return "";
        }

        /*@Override
        protected void onPostExecute(Map<String, String> profileInfo) {
            super.onPostExecute(profileInfo);
            adapter = new
                    CustomList(CircleActivity.this, friendNames, friendImages);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent i = new Intent(CircleActivity.this, FriendProfileActivity.class);
                    i.putExtra("token", token);
                    i.putExtra("circleId", circleId);
                    i.putExtra("circleName", circleName);
                    i.putExtra("friendId", friendIds.get(position));
                    i.putExtra("friendName", friendNames.get(position));
                    startActivity(i);
                }
            });
        }*/
    }


    public ArrayList<Integer> getPhotoIds() {
        return photoIds;
    }

    // Check supported file extensions
    private boolean isSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        if (AlbumGridConstants.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;

    }
}
