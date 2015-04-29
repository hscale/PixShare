package com.appofy.android.pixshare;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.appofy.android.pixshare.util.CustomListGroups;
import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ManageGroupsActivity extends ActionBarActivity {

    ListView lv;
    EditText inputSearch;
    ArrayAdapter<String> adapter;
    ArrayList<String> groupIds;
    ArrayList<String> groupNames;
    ArrayList<String> groupOwnerIds;

    //API URL
    public final static String initialURL = "http://52.8.12.67:8080/pixsharebusinessservice/rest/pixshare/";

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_groups);
        lv =(ListView)findViewById(R.id.group_list_view);

        inputSearch = (EditText)findViewById(R.id.inputGroupSearch);
        //new GroupTask().execute();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams chkParams = new RequestParams();
        session = new SessionManager(getApplicationContext());
        chkParams.put("userId", session.getUserDetails().get("userId"));

        client.get(initialURL + "group", chkParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    groupIds = new ArrayList<String>();
                    groupNames = new ArrayList<String>();
                    groupOwnerIds = new ArrayList<String>();
                    JSONObject jobj = new JSONObject(new String(response));
                    if (jobj.getString("responseFlag").equals("success")) {
                        JSONArray jsonArray = new JSONArray(jobj.getString("userGroups"));
                        JSONArray jsonArray1;
                        for(int i=0;i<jsonArray.length();i++){
                            jsonArray1 = new JSONArray(jsonArray.getString(i));
                            groupIds.add(String.valueOf(jsonArray1.get(0)));
                            groupNames.add(String.valueOf(jsonArray1.get(1)));
                            groupOwnerIds.add(String.valueOf(jsonArray1.get(2)));
                        }


                        adapter = new CustomListGroups(ManageGroupsActivity.this, groupNames);

                        lv.setAdapter(adapter);
                        /**
                         * Enabling onCLickListener Filter
                         * */
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                                    long arg3) {
                                String value = (String) adapter.getItemAtPosition(position);
                                Toast.makeText(getBaseContext(), value, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(ManageGroupsActivity.this, ManageFriendsGroupsActivity.class);
                                i.putExtra("groupId", groupIds.get(position));
                                startActivity(i);
                            }
                        });

                        /**
                         * Enabling Search Filter
                         * */
                        inputSearch.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                // When user changed the Text
                                adapter.getFilter().filter(cs);
                            }

                            @Override
                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                          int arg3) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void afterTextChanged(Editable arg0) {
                                // TODO Auto-generated method stub
                            }
                        });


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
    }

    /*private class GroupTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            ///////

            return "";
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            //
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_groups, menu);
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
