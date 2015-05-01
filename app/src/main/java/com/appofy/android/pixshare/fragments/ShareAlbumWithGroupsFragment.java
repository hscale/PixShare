package com.appofy.android.pixshare.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.appofy.android.pixshare.AddNewAlbumActivity;
import com.appofy.android.pixshare.AlbumGridActivity;
import com.appofy.android.pixshare.InviteFriendsActivity;
import com.appofy.android.pixshare.MyProfileActivity;
import com.appofy.android.pixshare.PendingFriendRequestActivity;
import com.appofy.android.pixshare.util.Constants;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appofy.android.pixshare.R;

import com.appofy.android.pixshare.fragments.dummy.DummyContent;
import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class ShareAlbumWithGroupsFragment extends Fragment {

    ListView lv;
    ArrayList<Group> groupList;
    ShareAlbumGroupAdapter sharedAlbumGroupAdapter;
    // Session Manager Class
    SessionManager session;
    Button mSubmitSelectedGroupsBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_share_album_with_groups, container, false);
        lv = (ListView) rootView.findViewById(R.id.sharealbum_group_list);
        mSubmitSelectedGroupsBtn = (Button) rootView.findViewById(R.id.submitSelectGroupsBtn);
        mSubmitSelectedGroupsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");
                JSONObject shareGroup = new JSONObject();
                JSONArray shareGroupArray = new JSONArray();
                for(int i=0;i<groupList.size();i++){
                    Group group = groupList.get(i);
                    if(group.isSelected()){
                        JSONObject shareGroupJson = new JSONObject();
                        try {
                            shareGroupJson.put("shareGroupId",group.getGroupId());
                            shareGroupArray.put(shareGroupJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                try {
                    shareGroup.put("shareGroupIds",shareGroupArray);
                    //shareGroup.put("albumId",getActivity().getIntent().getIntExtra("albumId",0));
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams chkParams = new RequestParams();
                    groupList = new ArrayList<Group>();
                    session = new SessionManager(getActivity().getApplicationContext());
                    chkParams.put("albumId",getActivity().getIntent().getIntExtra("albumId",0) );
                    chkParams.put("shareGroupIds",shareGroup.toString());
                    client.put(Constants.initialURL + "/photo/album/groups", chkParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            try {

                                JSONObject jobj = new JSONObject(new String(response));
                                if (jobj.getString("responseFlag").equals("success")) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Shared with groups successfully.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity(), AlbumGridActivity.class);
                                    intent.putExtra("albumId",getActivity().getIntent().getIntExtra("albumId",0));
                                    startActivity(intent);

                                } else {
                                    //Toast.makeText(getActivity().getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
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


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        displayGroupList();
        return rootView;
    }

    private void displayGroupList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams chkParams = new RequestParams();
        groupList = new ArrayList<Group>();
        session = new SessionManager(getActivity().getApplicationContext());
        chkParams.put("userId", session.getUserDetails().get("userId"));

        client.get(Constants.initialURL + "/pixshare/group", chkParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {

                    JSONObject jobj = new JSONObject(new String(response));
                    if (jobj.getString("responseFlag").equals("success")) {
                        JSONArray jsonArray = new JSONArray(jobj.getString("userGroups"));
                        JSONArray jsonArray1;
                        for(int i=0;i<jsonArray.length();i++){
                            jsonArray1 = new JSONArray(jsonArray.getString(i));
                            Group group = new Group(String.valueOf(jsonArray1.get(1)),String.valueOf(jsonArray1.get(0)));
                            groupList.add(group);

                        }
                        sharedAlbumGroupAdapter = new ShareAlbumGroupAdapter(groupList,getActivity());
                        lv.setAdapter(sharedAlbumGroupAdapter);
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

    }

    class Group {

        String groupName;
        boolean selected = false;
        String groupId;
        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }


        public Group(String name, String id) {
            super();
            this.groupName = name;
            this.groupId = id;
        }

        public String getName() {
            return groupName;
        }

        public void setName(String name) {
            this.groupName = name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    class ShareAlbumGroupAdapter extends ArrayAdapter<Group> {

        private List<Group> groupList;
        private Context context;

        public ShareAlbumGroupAdapter(List<Group> groupList, Context context) {
            super(context, R.layout.sharealbum_groups_listcheckbox_item, groupList);
            this.groupList = groupList;
            this.context = context;
        }

        private class GroupHolder {
            public TextView frienNameView;
            public CheckBox chkBox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            GroupHolder holder = new GroupHolder();

            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.sharealbum_groups_listcheckbox_item, null);

                holder.frienNameView = (TextView) v.findViewById(R.id.sharealbum_group_name);
                holder.chkBox = (CheckBox) v.findViewById(R.id.sharealbum_group_chk_box);

                holder.chkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Group group = (Group) cb.getTag();
                        System.out.println("Clicked on Checkbox: " + cb.getText() +
                                " is " + cb.isChecked());
                        group.setSelected(cb.isChecked());
                    }
                });

            } else {
                holder = (GroupHolder) v.getTag();
            }

            Group p = groupList.get(position);
            holder.frienNameView.setText("" + p.getName());
            holder.chkBox.setChecked(p.isSelected());
            holder.chkBox.setTag(p);
            return v;
        }
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share_with_groups, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite_friends:
                Intent inviteFriendsIntent = new Intent(getActivity().getApplicationContext(), InviteFriendsActivity.class);
                startActivity(inviteFriendsIntent);
                return true;
            case R.id.signout:
                session = new SessionManager(getActivity().getApplicationContext());
                session.logoutUser();
                return true;
            case R.id.my_profile:
                Intent myProfileIntent = new Intent(getActivity().getApplicationContext(), MyProfileActivity.class);
                startActivity(myProfileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
