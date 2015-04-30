package com.appofy.android.pixshare.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.appofy.android.pixshare.AddNewAlbumActivity;
import com.appofy.android.pixshare.AlbumGridActivity;
import com.appofy.android.pixshare.InviteFriendsActivity;
import com.appofy.android.pixshare.MyProfileActivity;
import com.appofy.android.pixshare.PendingFriendRequestActivity;
import com.appofy.android.pixshare.R;
import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlbumsFragment extends Fragment {

    ListView lv;
    EditText inputSearch;
    ArrayAdapter<String> adapter;

    String url = "http://52.8.12.67:8080/pixsharebusinessservice/rest";
    String sectionurl = "/photo";
    String suburl = "/albums";
    String desturl = null;

    String[] albumNames;
    int[] albumIds;
    int userId;
    SessionManager session;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_albums, container,false);

        lv =(ListView)rootView.findViewById(R.id.album_list_view);
        session = new SessionManager(getActivity());
        inputSearch = (EditText)rootView.findViewById(R.id.inputAlbumSearch);
        desturl = url + sectionurl + suburl;
        //String[] values = new String[] { "Album 1", "Album 2", "Album 3" };

        // TODO: Need to fetch from session.
        userId = Integer.parseInt(session.getUserDetails().get("userId"));
        System.out.println("In AlbumsFragment UserID:"+userId);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams chkParams = new RequestParams();
        chkParams.put("userId", userId);

        client.get(desturl, chkParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject albumsJSON = new JSONObject(new String(response));
                    if (albumsJSON.getString("responseFlag").equals("success")) {
                        JSONArray albums = albumsJSON.getJSONArray("albums");
                        int count = albums.length();
                        albumIds = new int[count];
                        albumNames = new String[count];
                        for(int i=0 ; i< count; i++){
                            JSONObject album = albums.getJSONObject(i);
                            albumIds[i]= album.getInt("albumId");
                            albumNames[i]  = album.getString("albumName");
                        }

                        adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.album_list_item,R.id.album_name, albumNames);
                        lv.setAdapter(adapter);
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error Occurred!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getActivity(), "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
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

        /**
         * Enabling onCLickListener Filter
         * */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                String value = (String) adapter.getItemAtPosition(position);
                Toast.makeText(getActivity().getBaseContext(), value, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(), AlbumGridActivity.class);

                i.putExtra("albumId",albumIds[position]);
                startActivity(i);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_album_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_album:
                Intent addAlbumIntent = new Intent(getActivity().getApplicationContext(), AddNewAlbumActivity.class);
                startActivity(addAlbumIntent);
                return true;
            case R.id.invite_friends:
                Intent inviteFriendsIntent = new Intent(getActivity().getApplicationContext(), InviteFriendsActivity.class);
                startActivity(inviteFriendsIntent);
                return true;
            case R.id.friend_request:
                Intent pendingFriendRequestIntent = new Intent(getActivity().getApplicationContext(), PendingFriendRequestActivity.class);
                startActivity(pendingFriendRequestIntent);
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
