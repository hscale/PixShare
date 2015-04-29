package com.appofy.android.pixshare;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class FriendProfileActivity extends ActionBarActivity {

    String friendId, friendName;

    // Session Manager Class
    SessionManager session;

    protected Bitmap image;
    protected ImageView profilePic;
    protected TextView name, userName, website, bio, loggedInUsing, email, phone, gender;
    protected JSONArray jsonArray;

    private class FriendProfileTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            try {

                System.out.println("profilePicURL = :::::::: " + params[0]);
                InputStream in = new java.net.URL(params[0]).openStream();
                image = BitmapFactory.decodeStream(in);
                Bitmap resized = Bitmap.createScaledBitmap(image, 100, 100, true);
                image = getRoundedRectBitmap(resized, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);

            profilePic.setImageBitmap(image);
            try {
                if (!jsonArray.get(2).equals(null)) {
                    name.setText(jsonArray.getString(2));
                }
                if (!jsonArray.get(3).equals(null)) {
                    userName.setText(jsonArray.getString(3));
                }
                if (!jsonArray.get(4).equals(null)) {
                    website.setText(jsonArray.getString(4));
                }
                if (!jsonArray.get(5).equals(null)) {
                    bio.setText(jsonArray.getString(5));
                }
                if (!jsonArray.get(6).equals(null)) {
                    loggedInUsing.setText(jsonArray.getString(6));
                }else{
                    loggedInUsing.setText("Registered via Email");
                }
                if (!jsonArray.get(7).equals(null)) {
                    email.setText(jsonArray.getString(7));
                }
                if (!jsonArray.get(8).equals(null)) {
                    phone.setText(jsonArray.getString(8));
                }
                if (!jsonArray.get(9).equals(null)) {
                    gender.setText(jsonArray.getString(9));
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        /*getRoundedRectBitmap(...) converts the rectangular image into round image for UI*/
        public Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
            Bitmap result = null;
            try {
                result = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);

                int color = 0xff424242;
                Paint paint = new Paint();
                Rect rect = new Rect(0, 0, 100, 100);

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);
                canvas.drawCircle(50, 50, 50, paint);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            friendId = extras.getString("friendId");
            if(extras.containsKey("sendRequestFlag")){
                if(extras.getString("sendRequestFlag").equals("T")){
                    //TODO set one more menu item to send friend request
                }
            }
        }

        profilePic = (ImageView) findViewById(R.id.ivProfilePic);
        name = (TextView) findViewById(R.id.tvName);
        userName = (TextView) findViewById(R.id.tvUserName);
        website = (TextView) findViewById(R.id.tvWebsite);
        bio = (TextView) findViewById(R.id.tvBio);
        loggedInUsing = (TextView) findViewById(R.id.tvLoggedInUsing);
        email = (TextView) findViewById(R.id.tvEmail);
        phone = (TextView) findViewById(R.id.tvPhone);
        gender = (TextView) findViewById(R.id.tvGender);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams chkParams = new RequestParams();
        session = new SessionManager(getApplicationContext());
        chkParams.put("friendId", friendId);

        client.get(Constants.initialURL + "user/friend/detail", chkParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String socialMediaId = null;
                    JSONObject jobj = new JSONObject(new String(response));
                    if (jobj.getString("responseFlag").equals("success")) {
                        jsonArray = new JSONArray(jobj.getString("userDetails"));
                        String profilePicURL = null;
                        if (!jsonArray.get(1).equals(null)) {
                            profilePicURL = jsonArray.get(1).toString();
                        } else {
                            //if not present then show placeholder image
                            profilePicURL = "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSpG4D-f7xIFSeApGIWTeSR-Bep7DzTZrGVUGhT0dTS5svo7mpe8g";
                        }
                        new FriendProfileTask().execute(profilePicURL);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_profile, menu);
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
