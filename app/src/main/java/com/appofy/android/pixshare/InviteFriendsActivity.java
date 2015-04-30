package com.appofy.android.pixshare;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appofy.android.pixshare.util.Constants;
import com.appofy.android.pixshare.util.SessionManager;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class InviteFriendsActivity extends ActionBarActivity {

    EditText txtEmailIds;
    Button btnSendInvite, btnSendFBInvite;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        txtEmailIds = (EditText) findViewById(R.id.inputFriendSendEmail);
        btnSendInvite = (Button) findViewById(R.id.inputFriendSendEmailBtn);
        btnSendFBInvite = (Button) findViewById(R.id.inputFriendFBInviteBtn);

        // Session Manager
        session = new SessionManager(getApplicationContext());
        if(session.isLoggedIn()){
            if(session.getUserDetails().get("socialMediaFlag").equals("T") &&
                    session.getUserDetails().get("socialMediaId").equals("1")){
                btnSendFBInvite.setVisibility(View.VISIBLE);
            }
        }

        btnSendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtEmailIds.getText().toString().trim().length() > 0) {
                    RequestParams params = new RequestParams();
                    params.put("userId", session.getUserDetails().get("userId"));
                    try {
                        params.put("inviteeList", new JSONArray(txtEmailIds.getText().toString().split(";")));
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(getApplicationContext(), Constants.initialURL + "/pixshare/user/invite/email", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            try {
                                JSONObject jobj = new JSONObject(new String(response));
                                if (jobj.getString("responseFlag").equals("success")) {
                                    Toast.makeText(getApplicationContext(), "Email request/s sent successfully!", Toast.LENGTH_LONG).show();
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
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter email id", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnSendFBInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appLinkUrl, previewImageUrl;

                appLinkUrl = "https://fb.me/1579608072297603";
                previewImageUrl = "https://scontent-2.2914.fna.fbcdn.net/hphotos-xaf1/t39.2081-0/11057101_1572801749644902_140055244_n.jpg";

                if (AppInviteDialog.canShow()) {
                    AppInviteContent content = new AppInviteContent.Builder()
                            .setApplinkUrl(appLinkUrl)
                            .setPreviewImageUrl(previewImageUrl)
                            .build();
                    AppInviteDialog.show(InviteFriendsActivity.this, content);
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite_friends, menu);
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
