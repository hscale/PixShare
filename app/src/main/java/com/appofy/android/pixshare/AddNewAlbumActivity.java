package com.appofy.android.pixshare;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appofy.android.pixshare.util.Constants;
import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class AddNewAlbumActivity extends ActionBarActivity {
    private static int RESULT_LOAD_IMG = 1;
    private Button mCreateAlbumBtn;
    String url = Constants.initialURL;
    String sectionurl = "/photo";
    String suburl = "/album";
    String desturl = null;
    int userId;
    SessionManager session;
    EditText mAlbumName;
    EditText mAlbumDesc;


    private ArrayList<String> imagesPathList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_album);
        session = new SessionManager(getApplicationContext());
        mAlbumName = (EditText) findViewById(R.id.albumname);
        mAlbumDesc = (EditText) findViewById(R.id.createAlbumDesc);
        mCreateAlbumBtn = (Button) findViewById(R.id.createAlbumButton);
        mCreateAlbumBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                desturl = url + sectionurl + suburl;
                //String[] values = new String[] { "Album 1", "Album 2", "Album 3" };

                // TODO: Need to fetch from session.
                userId = Integer.parseInt(session.getUserDetails().get("userId"));
                System.out.println("In AlbumsFragment UserID:"+userId);
                // Make RESTful webservice call using AsyncHttpClient object
                AsyncHttpClient client = new AsyncHttpClient();

                RequestParams chkParams = new RequestParams();
                chkParams.put("userId", userId);
                chkParams.put("albumName",mAlbumName.getText());
                chkParams.put("albumDescription",mAlbumDesc.getText());
                chkParams.put("latitude",0);
                chkParams.put("longitude",0);
                System.out.println("DestUrl1:"+desturl);
                client.post(desturl, chkParams, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        try {
                            JSONObject albumJSON = new JSONObject(new String(response));
                            if (albumJSON.getString("responseFlag").equals("success")) {
                                Intent i = new Intent(getApplicationContext(),LandingActivity.class);
                                startActivity(i);

                            } else {
                                Toast.makeText(getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
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
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_album, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite_friends:
                Intent inviteFriendsIntent = new Intent(this, InviteFriendsActivity.class);
                startActivity(inviteFriendsIntent);
                return true;

            case R.id.signout:
                session = new SessionManager(this);
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
