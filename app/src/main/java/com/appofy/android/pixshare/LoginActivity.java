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

public class LoginActivity extends FragmentActivity {

    // Email, password edittext
    EditText txtUsername, txtPassword;

    // login button
    Button btnLogin;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                // Check if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0){
                     session.createLoginSession("Android Hive", "anroidhive@gmail.com");
                     // Staring MainActivity
                     Intent i = new Intent(getApplicationContext(), MainActivity.class);
                     // Closing all the Activities
                     i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     // Add new Flag to start new Activity
                     i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     startActivity(i);
                     finish();
                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
                    Toast.makeText(getApplicationContext(), "Login failed, Please enter username and password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //finish();
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
