package com.appofy.android.pixshare.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.appofy.android.pixshare.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class SharedAlbumsFragment extends Fragment {

    ListView lv;

    EditText inputSearch;
    // Listview Adapter
    ArrayAdapter<String> adapter;

    String url = "http://52.8.12.67:8080/pixsharebusinessservice/rest";
    String sectionurl = "/photo";
    String suburl = "/user/albums";
    String desturl = null;

    String[] albumNames;
    int[] albumIds;
    int userId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_shared_albums, container, false);
        lv =(ListView)rootView.findViewById(R.id.shared_album_list_view);
        inputSearch = (EditText)rootView.findViewById(R.id.inputSharedAlbumSearch);
        desturl = url + sectionurl + suburl;
        //String[] values = new String[] { "Shared Album 1", "Shared Album 2", "Shared Album 3" };
        // TODO: Need to change it with session value.
        userId = 15;
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

                                R.layout.shared_album_list_item,R.id.shared_album_name, albumNames);

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
                Toast.makeText(getActivity().getBaseContext(),value,Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }
}
