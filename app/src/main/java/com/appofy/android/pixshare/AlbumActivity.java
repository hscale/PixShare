package com.appofy.android.pixshare;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;


public class AlbumActivity extends ActionBarActivity {

    String url = "http://52.8.53.106:8080/pixsharebusinessservice/rest";
    String sectionurl = "/photo";
    String suburl = "/album";
    String desturl = null;

    int albumId;

    String[] likeUserName;
    int[] likeUserId;

    String[] commentUserName;
    int[] commentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        // TODO: Need to fecth from the click event.
        albumId = 5;
        desturl = url + sectionurl + suburl;
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams chkParams = new RequestParams();
        chkParams.put("albumId", albumId);

        client.get(desturl, chkParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject albumsJSON = new JSONObject(new String(response));
                    if (albumsJSON.getString("responseFlag").equals("success")) {
                        JSONObject album = albumsJSON.getJSONObject("album");
                        System.out.println(album.getInt("albumId"));
                        System.out.println(album.getString("albumName"));
                        System.out.println(album.getString("albumsDescription"));
                        System.out.println(album.getDouble("latitude"));
                        System.out.println(album.getDouble("longitude"));
                        System.out.println(album.getString("dateCreated"));
                        System.out.println(album.getString("dateUpdated"));

                        JSONArray likes = album.getJSONArray("likes");

                        int count = likes.length();
                        likeUserId = new int[count];
                        likeUserName = new String[count];
                        for (int i = 0; i < count; i++) {
                            JSONObject like = likes.getJSONObject(i);
                            likeUserId[i] = like.getInt("userId");
                            likeUserName[i] = like.getString("userName");
                        }
                        System.out.println(likeUserId);
                        System.out.println(likeUserName);

                        JSONArray comments = album.getJSONArray("comments");

                        count = comments.length();
                        commentUserId = new int[count];
                        commentUserName = new String[count];
                        for (int i = 0; i < count; i++) {
                            JSONObject comment = comments.getJSONObject(i);
                            commentUserId[i] = comment.getInt("userId");
                            commentUserName[i] = comment.getString("userName");
                        }
                        System.out.println(commentUserId);
                        System.out.println(commentUserName);

                    } else {
                        //Toast.makeText(getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
