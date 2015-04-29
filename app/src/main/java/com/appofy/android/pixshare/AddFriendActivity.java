package com.appofy.android.pixshare;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.appofy.android.pixshare.adapter.SwipeTabsAdapter;
import com.appofy.android.pixshare.util.Constants;
import com.appofy.android.pixshare.util.CustomList;
import com.appofy.android.pixshare.util.SessionManager;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AddFriendActivity extends ActionBarActivity {

    ListView lv;
    Button mSearchBtn;
    EditText mUsernameEText;
    ArrayList<String> friendNames;
    ArrayList<String> friendIds;
    ArrayAdapter<String> adapter;
    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mUsernameEText = (EditText) findViewById(R.id.inputFriendSearchByUsername);
        mSearchBtn = (Button) findViewById(R.id.inputFriendSearchBtn);
        lv =(ListView)findViewById(R.id.list);

        // Session Manager
        session = new SessionManager(getApplicationContext());
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),mUsernameEText.getText(),Toast.LENGTH_LONG).show();

                if(mUsernameEText.getText().toString().trim().length() > 0){
                    RequestParams params = new RequestParams();
                    params.put("userName", mUsernameEText.getText().toString());
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(getApplicationContext(), Constants.initialURL + "user", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            try {
                                friendIds = new ArrayList<String>();
                                friendNames = new ArrayList<String>();
                                JSONObject jobj = new JSONObject(new String(response));
                                if (jobj.getString("responseFlag").equals("success")) {
                                    JSONArray jsonArray = new JSONArray(jobj.getString("userListByUserName"));
                                    JSONArray jsonArray1;
                                    if(jsonArray.length()>0){
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            jsonArray1 = new JSONArray(jsonArray.getString(i));
                                            System.out.println("jsonArray1::::::::::: "+jsonArray1);
                                            friendIds.add(jsonArray1.getString(0));
                                            friendNames.add(String.valueOf(jsonArray1.get(2))+" "+String.valueOf(jsonArray1.get(3)));
                                        }

                                        //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, friendNames);
                                        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.add_friend_list_layout, R.id.listTextView, friendNames);

                                        lv.setAdapter(adapter);
                                        /**
                                         * Enabling onCLickListener Filter
                                         * */
                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                                                    long arg3) {
                                                String value = (String) adapter.getItemAtPosition(position);
                                                //Toast.makeText(getBaseContext(), value, Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(getApplicationContext(), FriendProfileActivity.class);
                                                i.putExtra("friendId", friendIds.get(position));
                                                i.putExtra("sendRequestFlag", "T");
                                                startActivity(i);
                                            }
                                        });


                                    }else{
                                        Toast.makeText(getApplicationContext(), "No such records found!", Toast.LENGTH_LONG).show();
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
                }else{
                    Toast.makeText(getBaseContext(),"Please enter user name",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
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
