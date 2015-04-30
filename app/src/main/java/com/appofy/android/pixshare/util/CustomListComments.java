package com.appofy.android.pixshare.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appofy.android.pixshare.R;

import java.util.ArrayList;

/**
 * Created by Mihir on 28-04-2015.
 */
public class CustomListComments extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> web;

    public CustomListComments(Activity context, ArrayList<String> web) {
        super(context, R.layout.comment_list_item, web);
        this.context = context;
        this.web = web;
    }

    /*getView(...) sets the view for circle list liked with the user's profile*/
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.comment_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.comment);
        txtTitle.setText(web.get(position));
        return rowView;
    }
}
