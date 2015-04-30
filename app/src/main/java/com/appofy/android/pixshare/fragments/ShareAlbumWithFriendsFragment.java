package com.appofy.android.pixshare.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.appofy.android.pixshare.AlbumGridActivity;
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
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ShareAlbumWithFriendsFragment extends Fragment {

    ListView lv;
    ArrayList<Friend> friendList;
    ShareAlbumFriendAdapter plAdapter;
    // Session Manager Class
    SessionManager session;
    Button mSubmitSelectedFriendsBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_sharealbum_friends, container, false);
        lv = (ListView) rootView.findViewById(R.id.sharealbum_friend_list);
        mSubmitSelectedFriendsBtn = (Button) rootView.findViewById(R.id.submitSelectFriendsBtn);
        mSubmitSelectedFriendsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");
                JSONObject shareUser = new JSONObject();
                JSONArray shareUserArray = new JSONArray();
                for(int i=0;i<friendList.size();i++){
                    Friend friend = friendList.get(i);
                    if(friend.isSelected()){
                        JSONObject shareUserJson = new JSONObject();
                        try {
                            shareUserJson.put("shareUserId",friend.getFriendId());
                            shareUserArray.put(shareUserJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                try {
                    shareUser.put("shareUserIds",shareUserArray);
                    //shareUser.put("albumId",getActivity().getIntent().getIntExtra("albumId",0));
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams chkParams = new RequestParams();
                    friendList = new ArrayList<Friend>();
                    session = new SessionManager(getActivity().getApplicationContext());
                    chkParams.put("albumId",getActivity().getIntent().getIntExtra("albumId",0) );
                    chkParams.put("shareUserIds",shareUser.toString());
                    client.put(Constants.initialURL + "/photo/album/users", chkParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            try {

                                JSONObject jobj = new JSONObject(new String(response));
                                if (jobj.getString("responseFlag").equals("success")) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Shared with friends successfully.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity(), AlbumGridActivity.class);
                                    intent.putExtra("albumId",getActivity().getIntent().getIntExtra("albumId",0));
                                    startActivity(intent);

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


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        displayFriendList();
        return rootView;
    }

    private void displayFriendList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams chkParams = new RequestParams();
        friendList = new ArrayList<Friend>();
        session = new SessionManager(getActivity().getApplicationContext());
        chkParams.put("userId", session.getUserDetails().get("userId"));

        client.get(Constants.initialURL + "/pixshare/user/friend", chkParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {

                    JSONObject jobj = new JSONObject(new String(response));
                    if (jobj.getString("responseFlag").equals("success")) {
                        JSONArray jsonArray = new JSONArray(jobj.getString("friendList"));
                        JSONArray jsonArray1;
                        for(int i=0;i<jsonArray.length();i++){
                            jobj = new JSONObject(jsonArray.getString(i));
                            jsonArray1 = new JSONArray(jobj.getString("friendDetails"));
                            Friend friend = new Friend(String.valueOf(jsonArray1.get(2)),String.valueOf(jsonArray1.get(0)));
                            friendList.add(friend);

                        }
                        plAdapter = new ShareAlbumFriendAdapter(friendList,getActivity());
                        lv.setAdapter(plAdapter);
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

     class Friend {

        String friendName;
        boolean selected = false;
         String friendId;
         public String getFriendId() {
             return friendId;
         }

         public void setFriendId(String friendId) {
             this.friendId = friendId;
         }


        public Friend(String name, String id) {
            super();
            this.friendName = name;
            this.friendId = id;
        }

        public String getName() {
            return friendName;
        }

        public void setName(String name) {
            this.friendName = name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

     class ShareAlbumFriendAdapter extends ArrayAdapter<Friend> {

        private List<Friend> friendList;
        private Context context;

        public ShareAlbumFriendAdapter(List<Friend> friendList, Context context) {
            super(context, R.layout.sharealbum_friends_listcheckbox_item, friendList);
            this.friendList = friendList;
            this.context = context;
        }

        private class FriendHolder {
            public TextView frienNameView;
            public CheckBox chkBox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            FriendHolder holder = new FriendHolder();

            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.sharealbum_friends_listcheckbox_item, null);

                holder.frienNameView = (TextView) v.findViewById(R.id.sharealbum_friend_name);
                holder.chkBox = (CheckBox) v.findViewById(R.id.sharealbum_friend_chk_box);

                holder.chkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Friend friend = (Friend) cb.getTag();
                        System.out.println("Clicked on Checkbox: " + cb.getText() +
                                " is " + cb.isChecked());
                        friend.setSelected(cb.isChecked());
                    }
                });

            } else {
                holder = (FriendHolder) v.getTag();
            }

            Friend p = friendList.get(position);
            holder.frienNameView.setText("" + p.getName());
            holder.chkBox.setChecked(p.isSelected());
            holder.chkBox.setTag(p);
            return v;
        }
    }
}
