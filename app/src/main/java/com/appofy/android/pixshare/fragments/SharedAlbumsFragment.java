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

public class SharedAlbumsFragment extends Fragment {

    ListView lv;

    EditText inputSearch;
    // Listview Adapter
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_shared_albums, container, false);
        lv =(ListView)rootView.findViewById(R.id.shared_album_list_view);
        inputSearch = (EditText)rootView.findViewById(R.id.inputSharedAlbumSearch);

        String[] values = new String[] { "Shared Album 1", "Shared Album 2", "Shared Album 3" };

        adapter = new ArrayAdapter<String>(getActivity(),

                R.layout.shared_album_list_item,R.id.shared_album_name, values);

        lv.setAdapter(adapter);



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
