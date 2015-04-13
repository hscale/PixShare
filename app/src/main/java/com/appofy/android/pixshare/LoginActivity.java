package com.appofy.android.pixshare;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.JoinAppGroupDialog;

import bolts.AppLinks;
//import com.facebook.share.widget.ShareDialog;

public class LoginActivity extends FragmentActivity {

    private ProfilePictureView profilePictureView;
    private TextView greeting;
    private CallbackManager callbackManager;
    //private ShareDialog shareDialog;
    private ProfileTracker profileTracker;
    private Button inviteFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        }

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        updateUI();
                    }

                    @Override
                    public void onCancel() {
                        updateUI();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        updateUI();
                    }

                });

        setContentView(R.layout.activity_login);
        profileTracker = new ProfileTracker() {

            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();
            }

        };

        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        greeting = (TextView) findViewById(R.id.greeting);
        inviteFriends = (Button) findViewById(R.id.invite);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appLinkUrl, previewImageUrl;
                AppInviteDialog inviteDialog = new AppInviteDialog(LoginActivity.this);
                appLinkUrl = "https://fb.me/1572691476322596";
                //previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";

                if (AppInviteDialog.canShow()) {
                    AppInviteContent content = new AppInviteContent.Builder()
                            .setApplinkUrl(appLinkUrl)
                            //.setPreviewImageUrl(previewImageUrl)
                            .build();
                    //AppInviteDialog.show(LoginActivity.this, content);
                    inviteDialog.registerCallback(callbackManager, new FacebookCallback<AppInviteDialog.Result>() {
                        @Override
                        public void onSuccess(AppInviteDialog.Result result) {
                            //Log.i("SUCCESS ", "MainACtivity, InviteCallback - SUCCESS!");
                            System.out.println("LoginACtivity, InviteCallback - SUCCESS!");
                        }

                        @Override
                        public void onCancel() {
                            //Log.i("CANCEL ", "MainACtivity, InviteCallback - CANCEL!");
                            System.out.println("LoginACtivity, InviteCallback - CANCEL!");
                        }

                        @Override
                        public void onError(FacebookException error) {
                            //Log.e("ERROR ", "MainACtivity, InviteCallback - ERROR! " + error.getMessage());
                            System.out.println("LoginACtivity, InviteCallback - ERROR!"+ error.getMessage());
                        }
                    });
                    inviteDialog.show(content);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null) {
            profilePictureView.setVisibility(View.VISIBLE);
            profilePictureView.setProfileId(profile.getId());
            greeting.setVisibility(View.VISIBLE);
            greeting.setText(getString(R.string.hello_user, profile.getFirstName()));
            inviteFriends.setVisibility(View.VISIBLE);
        } else {
            profilePictureView.setProfileId(null);
            profilePictureView.setVisibility(View.INVISIBLE);
            greeting.setText(null);
            greeting.setVisibility(View.INVISIBLE);
            inviteFriends.setVisibility(View.INVISIBLE);
        }
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
