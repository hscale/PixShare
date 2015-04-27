package com.appofy.android.pixshare;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.appofy.android.pixshare.util.SessionManager;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity {

    // Session Manager Class
    SessionManager session;

    // Button Logout
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        TextView lblName = (TextView) findViewById(R.id.lblName);
        TextView lblFriendList = (TextView) findViewById(R.id.lblFriendsList);
        //TextView lblEmail = (TextView) findViewById(R.id.lblEmail);

        // Button logout
        btnLogout = (Button) findViewById(R.id.btnLogout);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        if (!session.isLoggedIn()) {
            /**
             * Call this function whenever you want to check user login
             * This will redirect user to LoginActivity is he is not
             * logged in
             * */
            session.checkLogin();
        }

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String userId = user.get(SessionManager.KEY_USER_ID);

        // email
        //String email = user.get(SessionManager.KEY_EMAIL);

        // displaying user data
        lblName.setText(Html.fromHtml("Name: <b>" + userId + "</b>"));
        //lblEmail.setText(Html.fromHtml("Email: <b>" + email + "</b>"));


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
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(), "You are on Home Page..." , Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
