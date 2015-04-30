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


    int albumId;
    ArrayList<String> imagePaths = new ArrayList<String>();
    ArrayList<Integer> photoIds = new ArrayList<Integer>();

    // constructor
    public AlbumGridViewHelper(Context context) {
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



        return imagePaths;
    }

    // Reading file paths from memory
    public void setFilePaths(ArrayList<String> imagePaths) {



        this.imagePaths = imagePaths;
    }

    // Reading file paths from memory
    public void setPhotoIds(ArrayList<Integer> photoIds) {



        this.photoIds = photoIds;
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
