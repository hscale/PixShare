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

import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends ActionBarActivity {

    // FirstName, LastName, UserName, Email, Password, ConfirmPassword edittext
    EditText txtUsername, txtPassword, txtFirstName, txtLastName, txtConfirmPassword, txtEmail;

    // Submit button
    Button btnSubmit;

    // Session Manager Class
    SessionManager session;

    //API URL
    public final static String initialURL = "http://10.0.2.2:8080/PixShareBusinessService/rest/pixshare/user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Submit button
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // FirstName, LastName, UserName, Email, Password, ConfirmPassword input text
                txtFirstName = (EditText) findViewById(R.id.txtFirstName);
                txtLastName = (EditText) findViewById(R.id.txtLastName);
                txtUsername = (EditText) findViewById(R.id.txtRegUsername);
                txtEmail = (EditText) findViewById(R.id.txtRegEmail);
                txtPassword = (EditText) findViewById(R.id.txtRegPassword);
                txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

                final String firstName = txtFirstName.getText().toString();
                final String lastName = txtLastName.getText().toString();
                final String userName = txtUsername.getText().toString();
                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();
                final String confirmPassword = txtConfirmPassword.getText().toString();

                // Check if all fields are filled
                if(firstName.trim().length() > 0 && lastName.trim().length() > 0 &&
                        userName.trim().length() > 0 && email.trim().length() > 0 &&
                        password.trim().length() > 0 && confirmPassword.trim().length() > 0){
                    if(!password.trim().equals(confirmPassword.trim())){
                        Toast.makeText(getApplicationContext(), "Password and Confirm Password are not matching", Toast.LENGTH_LONG).show();
                    }else{

                        // Make RESTful webservice call using AsyncHttpClient object
                        AsyncHttpClient client = new AsyncHttpClient();

                        //check if userName is available
                        RequestParams chkParams = new RequestParams();
                        chkParams.put("userName", userName);
                        client.get(initialURL+"email/availability",chkParams,new AsyncHttpResponseHandler(){

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] response){
                                try {
                                    JSONObject obj = new JSONObject(new String(response));
                                    if(obj.getString("available").equals("A")){
                                        Toast.makeText(getApplicationContext(), "Username available, now registering...", Toast.LENGTH_LONG).show();
                                        // register user if userName is available
                                        //code to register to backend start
                                        String apiURL = initialURL + "email";
                                        RequestParams params = new RequestParams();
                                        params.put("firstName", firstName);
                                        params.put("lastName", lastName);
                                        params.put("userName", userName);
                                        params.put("email", email);
                                        params.put("password", password);
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        client.post(getApplicationContext(),apiURL,params,new AsyncHttpResponseHandler(){

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] response){
                                                try {
                                                    JSONObject jobj=new JSONObject(new String(response));
                                                    if(jobj.getString("responseFlag").equals("success")){
                                                        Toast.makeText(getApplicationContext(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
                                                        //session.createLoginSession("Android Hive", "anroidhive@gmail.com");
                                                        // Staring LoginActivity
                                                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                                        // Closing all the Activities
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        // Add new Flag to start new Activity
                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(i);
                                                        finish();
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
                                        //end code to register to backend
                                    }else if(obj.getString("available").equals("N")){
                                        Toast.makeText(getApplicationContext(), "Username not available, please try different Username", Toast.LENGTH_LONG).show();
                                    }else if(obj.getString("available").equals("W")){
                                        Toast.makeText(getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
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
                    }
                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
                    Toast.makeText(getApplicationContext(), "Please enter all the fields", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

   /* @Override
    public void onBackPressed() {
        // Staring LoginActivity
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
