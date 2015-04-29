package com.appofy.android.pixshare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.appofy.android.pixshare.AddFriendActivity;
import com.appofy.android.pixshare.AddGroupActivity;
import com.appofy.android.pixshare.InviteFriendsActivity;
import com.appofy.android.pixshare.ManageFriendsGroupsActivity;
import com.appofy.android.pixshare.MyProfileActivity;
import com.appofy.android.pixshare.R;
import com.appofy.android.pixshare.util.Constants;
import com.appofy.android.pixshare.util.CustomListGroups;
import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {

    ListView lv;
    EditText inputSearch;
    ArrayAdapter<String> adapter;
    ArrayList<String> groupIds;
    ArrayList<String> groupNames;
    ArrayList<String> groupOwnerIds;

    // Session Manager Class
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        lv = (ListView) rootView.findViewById(R.id.group_list_view);

        inputSearch = (EditText) rootView.findViewById(R.id.inputGroupSearch);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams chkParams = new RequestParams();
        session = new SessionManager(getActivity().getApplicationContext());
        chkParams.put("userId", session.getUserDetails().get("userId"));

        client.get(Constants.initialURL + "group", chkParams, new AsyncHttpResponseHandler() {
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
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonArray1 = new JSONArray(jsonArray.getString(i));
                            groupIds.add(String.valueOf(jsonArray1.get(0)));
                            groupNames.add(String.valueOf(jsonArray1.get(1)));
                            groupOwnerIds.add(String.valueOf(jsonArray1.get(2)));
                        }

                        adapter = new CustomListGroups(getActivity(), groupNames);

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
                                Intent i = new Intent(getActivity(), ManageFriendsGroupsActivity.class);
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
                        Toast.makeText(getActivity().getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getActivity().getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity().getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_manage_groups, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_group:
                Intent addGroupIntent = new Intent(getActivity().getApplicationContext(), AddGroupActivity.class);
                startActivity(addGroupIntent);
                return true;
            case R.id.invite_friends:
                Intent inviteFriendsIntent = new Intent(getActivity().getApplicationContext(), InviteFriendsActivity.class);
                startActivity(inviteFriendsIntent);
                return true;
            case R.id.my_profile:
                Intent myProfileIntent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(myProfileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
