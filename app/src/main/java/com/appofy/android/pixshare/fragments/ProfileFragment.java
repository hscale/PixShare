package com.appofy.android.pixshare.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appofy.android.pixshare.R;
import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class ProfileFragment extends Fragment {

    //API URL
    public final static String initialURL = "http://10.0.2.2:8080/PixShareBusinessService/rest/pixshare/user/";

    // Session Manager Class
    SessionManager session;

    protected Bitmap image;
    protected ImageView profilePic;
    protected TextView name, userName, website, bio, loggedInUsing, email, phone, gender;
    protected JSONArray jsonArray;
    protected Button btnLogout;

    private class ProfileTask extends AsyncTask<String, Void, Bitmap> {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = (ImageView) rootView.findViewById(R.id.ivProfilePic);
        name = (TextView) rootView.findViewById(R.id.tvName);
        userName = (TextView) rootView.findViewById(R.id.tvUserName);
        website = (TextView) rootView.findViewById(R.id.tvWebsite);
        bio = (TextView) rootView.findViewById(R.id.tvBio);
        loggedInUsing = (TextView) rootView.findViewById(R.id.tvLoggedInUsing);
        email = (TextView) rootView.findViewById(R.id.tvEmail);
        phone = (TextView) rootView.findViewById(R.id.tvPhone);
        gender = (TextView) rootView.findViewById(R.id.tvGender);

        btnLogout = (Button) rootView.findViewById(R.id.btnSignOut);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams chkParams = new RequestParams();
        session = new SessionManager(getActivity().getApplicationContext());
        chkParams.put("userId", session.getUserDetails().get("userId"));

        client.get(initialURL + "profile", chkParams, new AsyncHttpResponseHandler() {
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
                        new ProfileTask().execute(profilePicURL);
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error Occurred!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getActivity(), "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * Logout button click event
         * */
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Clear the session data
                // This will clear all session data and
                // redirect user to LoginActivity
                session.logoutUser();
            }
        });



        return rootView;
    }

}
