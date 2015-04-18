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


public class RegisterActivity extends ActionBarActivity {

    // FirstName, LastName, UserName, Email, Password, ConfirmPassword edittext
    EditText txtUsername, txtPassword, txtFirstName, txtLastName, txtConfirmPassword, txtEmail;

    // Submit button
    Button btnSubmit;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // FirstName, LastName, UserName, Email, Password, ConfirmPassword input text
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtUsername = (EditText) findViewById(R.id.txtRegUsername);
        txtEmail = (EditText) findViewById(R.id.txtRegEmail);
        txtPassword = (EditText) findViewById(R.id.txtRegPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        // Submit button
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = txtFirstName.getText().toString();
                String lastName = txtLastName.getText().toString();
                String userName = txtUsername.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                String confirmPassword = txtConfirmPassword.getText().toString();

                // Check if username, password is filled
                if(firstName.trim().length() > 0 && lastName.trim().length() > 0 &&
                        userName.trim().length() > 0 && email.trim().length() > 0 &&
                        password.trim().length() > 0 && confirmPassword.trim().length() > 0){
                    if(!password.trim().equals(confirmPassword.trim())){
                        Toast.makeText(getApplicationContext(), "Password and Confirm Password are not matching", Toast.LENGTH_LONG).show();
                    }else{
                        //code to register to backend start

                        //end
                        session.createLoginSession("Android Hive", "anroidhive@gmail.com");
                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        // Closing all the Activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // Add new Flag to start new Activity
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
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
