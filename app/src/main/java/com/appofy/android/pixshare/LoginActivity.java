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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

public class LoginActivity extends FragmentActivity {

    // Email, password edittext
    EditText txtUsername, txtPassword;
    String username, password;

    // login button
    Button btnLogin,btnRegister;

    // Session Manager Class
    SessionManager session;

    //API URL
    public final static String initialURL = "http://10.0.2.2:8080/PixShareBusinessService/rest/pixshare/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // Login button click event (regular login, which is using username and password)
        btnLogin.setOnClickListener(new View.OnClickListener() {

            //by default socialMediaFlag is false, it can be set if user log in using social media
            String socialMediaFlag="F";

            @Override
            public void onClick(View arg0) {

                // Email, Password input text
                txtUsername = (EditText) findViewById(R.id.txtUsername);
                txtPassword = (EditText) findViewById(R.id.txtPassword);

                // Get username, password from EditText
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();

                // Check if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0) {
                    //authenticate user using REST API call
                    // Make RESTful webservice call using AsyncHttpClient object
                    AsyncHttpClient client = new AsyncHttpClient();

                    RequestParams chkParams = new RequestParams();
                    chkParams.put("userName", username);
                    chkParams.put("password", password);
                    client.get(initialURL+"authenticate/email",chkParams,new AsyncHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response){
                            try {
                                JSONObject jobj=new JSONObject(new String(response));
                                if(jobj.getString("responseFlag").equals("success")){
                                    if(jobj.getString("authenticated").equals("true")){
                                        Toast.makeText(getApplicationContext(), "Welcome "+jobj.getString("firstName")+" !", Toast.LENGTH_LONG).show();
                                        //if authorized user then set the session with credentials
                                        if(jobj.getString("socialMediaFlag").equals("0")){
                                            socialMediaFlag = "F";
                                        }else if(jobj.getString("socialMediaFlag").equals("1")){
                                            socialMediaFlag = "T";
                                        }
                                        session.createLoginSession(username, password, socialMediaFlag);
                                        // Staring MainActivity
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        // Closing all the Activities
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        // Add new Flag to start new Activity
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e){
                            // When Http response code is '404'
                            if(statusCode == 404){
                                Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                            }
                            // When Http response code is '500'
                            else if(statusCode == 500){
                                Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                            }
                            // When Http response code other than 404, 500
                            else{
                                Toast.makeText(getApplicationContext(), "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    //////////////

                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
                    Toast.makeText(getApplicationContext(), "Login failed, Please enter username and password", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Register button
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // Staring RegisterActivity
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
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
