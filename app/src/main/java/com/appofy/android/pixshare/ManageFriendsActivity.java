package com.appofy.android.pixshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.appofy.android.pixshare.util.CustomList;
import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class ManageFriendsActivity extends ActionBarActivity {

    ListView lv;
    EditText inputSearch;
    ArrayAdapter<String> adapter;
    ArrayList<String> friendNames;
    ArrayList<String> friendIds;
    ArrayList<Bitmap> friendImages;
    protected Bitmap image;

    //API URL
    public final static String initialURL = "http://10.0.2.2:8080/PixShareBusinessService/rest/pixshare/user/";

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);
        lv =(ListView)findViewById(R.id.friend_list_view);

        inputSearch = (EditText)findViewById(R.id.inputFriendSearch);

        new FriendTask().execute();

    }

    private class FriendTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            SyncHttpClient client = new SyncHttpClient();
            RequestParams chkParams = new RequestParams();
            session = new SessionManager(getApplicationContext());
            chkParams.put("userId", session.getUserDetails().get("userId"));

            client.get(initialURL + "friend", chkParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    try {
                        friendIds = new ArrayList<String>();
                        friendNames = new ArrayList<String>();
                        friendImages = new ArrayList<Bitmap>();
                        JSONObject jobj = new JSONObject(new String(response));
                        if (jobj.getString("responseFlag").equals("success")) {
                            JSONArray jsonArray = new JSONArray(jobj.getString("friendList"));
                            JSONArray jsonArray1;
                            for(int i=0;i<jsonArray.length();i++){
                                jobj = new JSONObject(jsonArray.getString(i));
                                jsonArray1 = new JSONArray(jobj.getString("friendDetails"));

                                friendIds.add(String.valueOf(jsonArray1.get(0)));
                                friendNames.add(String.valueOf(jsonArray1.get(2)));
                                try {
                                    String picURL;
                                    if(!jsonArray1.get(3).equals(null)){
                                        picURL = jsonArray1.getString(3);
                                    }else{
                                        picURL = "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSpG4D-f7xIFSeApGIWTeSR-Bep7DzTZrGVUGhT0dTS5svo7mpe8g";
                                    }

                                    InputStream in = new java.net.URL(picURL).openStream();
                                    image = BitmapFactory.decodeStream(in);
                                    Bitmap resized = Bitmap.createScaledBitmap(image, 60, 60, true);
                                    image = getRoundedRectBitmap(resized, 30);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                friendImages.add(image);
                            }
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

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);

            adapter = new CustomList(ManageFriendsActivity.this, friendNames, friendImages);
            lv.setAdapter(adapter);
            /**
             * Enabling onCLickListener Filter
             * */
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long arg3) {
                    String value = (String) adapter.getItemAtPosition(position);
                    Toast.makeText(getBaseContext(), value, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ManageFriendsActivity.this, FriendProfileActivity.class);
                    i.putExtra("friendId", friendIds.get(position));
                    startActivity(i);
                }
            });

            /**
             * Enabling Search Filter
             * */
            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    adapter.getFilter().filter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });
        }

        /*getRoundedRectBitmap(...) converts the rectangular image into round image for UI*/
        public Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
            Bitmap result = null;
            try {
                result = Bitmap.createBitmap(60, 60, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);

                int color = 0xff424242;
                Paint paint = new Paint();
                Rect rect = new Rect(0, 0, 60, 60);

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);
                canvas.drawCircle(30, 30, 30, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError o) {
                o.printStackTrace();
            }
            return result;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_friends, menu);
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
