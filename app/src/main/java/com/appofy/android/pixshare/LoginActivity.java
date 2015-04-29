package com.appofy.android.pixshare;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import com.appofy.android.pixshare.util.SessionManager;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LoginActivity extends FragmentActivity {

    // Email, password edittext
    EditText txtUsername, txtPassword;
    String username, password;

    // login button
    Button btnLogin, btnRegister;
    LoginButton btnFbLogin;

    // Session Manager Class
    SessionManager session;

    private CallbackManager callbackManager;
    private AccessTokenTracker tracker;
    private AccessToken accessToken;

    private String jsonResponseString = null;
    private JSONObject fbFieldsInJsonObj = new JSONObject();

    //API URL
    public final static String initialURL = "http://10.0.2.2:8080/PixShareBusinessService/rest/pixshare/user/";

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                //TODO perform some action here
                //Toast.makeText(getApplicationContext(),"Welcome "+profile.getName(),Toast.LENGTH_LONG);
                System.out.println("in onSuccess!!");
                //TODO remove below line if not required
                //getFBUserInfo();
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        /*if(session.isLoggedIn()){
            LoginManager.getInstance().logOut();
        }*/
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // Login button click event (regular login, which is using username and password)
        btnLogin.setOnClickListener(new View.OnClickListener() {

            //by default socialMediaFlag is false, it can be set if user log in using social media
            String socialMediaFlag = "F";


            @Override
            public void onClick(View arg0) {

                // Email, Password input text
                txtUsername = (EditText) findViewById(R.id.txtUsername);
                txtPassword = (EditText) findViewById(R.id.txtPassword);

                // Get username, password from EditText
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();

                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    //authenticate user using REST API call
                    // Make RESTful webservice call using AsyncHttpClient object
                    AsyncHttpClient client = new AsyncHttpClient();

                    RequestParams chkParams = new RequestParams();
                    chkParams.put("userName", username);
                    chkParams.put("password", password);
                    client.get(initialURL + "email/authenticate", chkParams, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            try {
                                String socialMediaId = null;
                                JSONObject jobj = new JSONObject(new String(response));
                                if (jobj.getString("responseFlag").equals("success")) {
                                    if (jobj.getString("authenticated").equals("true")) {
                                        Toast.makeText(getApplicationContext(), "Welcome " + jobj.getString("firstName") + " !", Toast.LENGTH_LONG).show();
                                        //if authorized user then set the session with credentials
                                        if (jobj.getString("socialMediaFlag").equals("0")) {
                                            socialMediaFlag = "F";
                                        } else if (jobj.getString("socialMediaFlag").equals("1")) {
                                            socialMediaFlag = "T";
                                            socialMediaId = jobj.getString("socialMediaId");
                                        }
                                        session.createLoginSession(jobj.getString("userId"), password, socialMediaFlag,socialMediaId);
                                        System.out.println("*********** "+session.getUserDetails().get("userId"));
                                        // Staring LandingActivity
                                        Intent i = new Intent(getApplicationContext(), LandingActivity.class);
                                        // Closing all the Activities
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        // Add new Flag to start new Activity
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
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
                    //////////////

                } else {
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
                    Toast.makeText(getApplicationContext(), "Login failed, Please enter username and password", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Register button
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Staring RegisterActivity
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        //FB Login Button
        btnFbLogin = (LoginButton) findViewById(R.id.fb_login_button);
        btnFbLogin.setReadPermissions("user_friends");
        callbackManager = CallbackManager.Factory.create();
        /*tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                //TODO update session - sharedpref values and update backend (accesstoken API call - PUT method)
                System.out.println("in AccessTokenTracker!!");
                //remove below line...
                //getFBUserInfo();
            }
        };*/
        btnFbLogin.registerCallback(callbackManager, callback);
        //tracker.startTracking();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //tracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //get token and fill the info on screen again -- if sessionloggedin then goto homescreen
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        getFBUserInfo();
        System.out.println("after getFBUserInfo()..but this stmt is executed in async..so not reliable");
        //updateUI();
    }

    private void getFBUserInfo() {

        System.out.println(" getFBUserInfo -- accessToken: " + accessToken);
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        jsonResponseString = response.getJSONObject().toString();
                        System.out.println("Graph Response: " + jsonResponseString);
                        try {
                            fbFieldsInJsonObj.put("sourceName", "facebook");
                            fbFieldsInJsonObj.put("token", accessToken.getToken());
                            JSONObject j1 = response.getJSONObject();
                            Iterator<?> keys = j1.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                System.out.println(key);
                                if (key.contains("id")) {
                                    fbFieldsInJsonObj.put("socialUserId", j1.get("id"));
                                } else if (key.contains("email")) {
                                    fbFieldsInJsonObj.put("email", j1.get("email"));
                                } else if (key.contains("gender")) {
                                    fbFieldsInJsonObj.put("gender", j1.get("gender"));
                                } else if (key.contains("picture")) {
                                    JSONObject j2 = j1.getJSONObject("picture");
                                    JSONObject j3 = j2.getJSONObject("data");
                                    fbFieldsInJsonObj.put("profilePicURL", j3.get("url"));
                                } else if (key.contains("phone")) {
                                    fbFieldsInJsonObj.put("phone", j1.get("phone"));
                                } else if (key.contains("website")) {
                                    fbFieldsInJsonObj.put("websiteURL", j1.get("website"));
                                } else if (key.contains("bio")) {
                                    fbFieldsInJsonObj.put("bio", j1.get("bio"));
                                }
                            }
                            System.out.println("fbFieldsInJsonObj: " + fbFieldsInJsonObj);
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                        System.out.println("now logging out...");
                        updateUI();
                        //Below stmt can be used anywhere to logout from FB session
                        //LoginManager.getInstance().logOut();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,website,gender,picture,bio");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        final Profile profile = Profile.getCurrentProfile();

        if (enableButtons && profile != null) {
            //check if id is already recorded in DB, if not then record the entry
            // Make RESTful webservice call using AsyncHttpClient object
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams chkParams = new RequestParams();
            chkParams.put("socialUserId", profile.getId());
            chkParams.put("token", AccessToken.getCurrentAccessToken().getToken());
            client.get(initialURL + "social/authenticate", chkParams, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    try {
                        final String socialMediaFlag = "T";

                        JSONObject jobj = new JSONObject(new String(response));
                        if (jobj.getString("responseFlag").equals("success")) {
                            if (jobj.getString("present").equals("Y")) {
                                //Toast.makeText(getApplicationContext(), "Welcome " + jobj.getString("firstName") + " !", Toast.LENGTH_LONG).show();
                                Profile profile = Profile.getCurrentProfile();
                                //in social media login, the password is token
                                password = AccessToken.getCurrentAccessToken().getToken();
                                username = profile.getName();
                                //update token in backend, just to make sure token in backend is the updated one...
                                AsyncHttpClient client = new AsyncHttpClient();
                                RequestParams chkParams = new RequestParams();
                                chkParams.put("socialUserId", profile.getId());
                                chkParams.put("accessToken", AccessToken.getCurrentAccessToken().getToken());
                                client.put(initialURL + "social/accesstoken", chkParams, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                        try{
                                            String socialMediaId = null;
                                            JSONObject jobj = new JSONObject(new String(response));
                                            if (jobj.getString("responseFlag").equals("success")) {
                                                Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_LONG).show();
                                                String socialFlag="F";
                                                if(jobj.getString("socialMediaFlag").equals("1")){
                                                    socialFlag = "T";
                                                    socialMediaId =  jobj.getString("socialMediaId");
                                                }
                                                session.createLoginSession(jobj.getString("userId"), jobj.getString("token"),socialFlag,socialMediaId);
                                                //session.createLoginSession(jobj.getString("user_id"), password, socialMediaFlag,jobj.getString("socialMediaId"));
                                                // Staring MainActivity
                                                Intent i = new Intent(getApplicationContext(), LandingActivity.class);
                                                // Closing all the Activities
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                // Add new Flag to start new Activity
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                                finish();
                                            }else if(jobj.getString("responseFlag").equals("fail")){
                                                Toast.makeText(getApplicationContext(),"Something went wrong, please contact Admin.",Toast.LENGTH_LONG);
                                            }
                                        }catch(JSONException je){
                                            Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                                            je.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                        if (!session.isLoggedIn()) {
                                            LoginManager.getInstance().logOut();
                                        }
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
                                //

                            } else if (jobj.getString("present").equals("N")) {
                                // record the social user entry in the DB
                                Toast.makeText(getApplicationContext(), "Please hold on for a few moments, registering...", Toast.LENGTH_LONG).show();
                                RequestParams params = new RequestParams();
                                fbFieldsInJsonObj.put("firstName", profile.getFirstName());
                                fbFieldsInJsonObj.put("lastName", profile.getLastName());
                                fbFieldsInJsonObj.put("userName", profile.getName());
                                params.put("socialDetails", fbFieldsInJsonObj);
                                        /*params.put("lastName", profile.getLastName());
                                        params.put("userName", profile.getName());
                                        String email = null;//fetch facebook user email id
                                        params.put("email", email);
                                        params.put("socialUserId", profile.getId());
                                        params.put("password", AccessToken.getCurrentAccessToken().getToken());
                                        params.put("sourceName","facebook");*/
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.post(getApplicationContext(), initialURL + "social", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                        try {
                                            String socialMediaId = null;
                                            JSONObject jobj = new JSONObject(new String(response));
                                            if (jobj.getString("responseFlag").equals("success")) {
                                                Toast.makeText(getApplicationContext(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
                                                //session.createLoginSession(profile.getName(), AccessToken.getCurrentAccessToken().getToken(), socialMediaFlag);
                                                String socialFlag="F";
                                                if(jobj.getString("socialMediaFlag").equals("1")){
                                                    socialFlag = "T";
                                                    socialMediaId = jobj.getString("socialMediaId");
                                                }
                                                session.createLoginSession(jobj.getString("userId"), AccessToken.getCurrentAccessToken().getToken(),socialFlag,socialMediaId);
                                                // Staring LoginActivity
                                                Intent i = new Intent(getApplicationContext(), LandingActivity.class);
                                                // Closing all the Activities
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                // Add new Flag to start new Activity
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                                finish();
                                            } else {
                                                if (!session.isLoggedIn()) {
                                                    LoginManager.getInstance().logOut();
                                                }
                                                Toast.makeText(getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (Exception e) {
                                            if (!session.isLoggedIn()) {
                                                LoginManager.getInstance().logOut();
                                            }
                                            Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                        if (!session.isLoggedIn()) {
                                            LoginManager.getInstance().logOut();
                                        }
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
                        } else {
                            if (!session.isLoggedIn()) {
                                LoginManager.getInstance().logOut();
                            }
                            Toast.makeText(getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (!session.isLoggedIn()) {
                            LoginManager.getInstance().logOut();
                        }
                        Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    if (!session.isLoggedIn()) {
                        LoginManager.getInstance().logOut();
                    }
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
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
