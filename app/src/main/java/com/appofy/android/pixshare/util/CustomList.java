package com.appofy.android.pixshare.util;

/**
 * Created by user on 27-04-2015.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appofy.android.pixshare.R;

import java.util.ArrayList;

public class CustomList extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> web;
    private final ArrayList<Bitmap> imageId;

    public CustomList(Activity context,
                      ArrayList<String> web, ArrayList<Bitmap> imageId) {
        super(context, R.layout.friend_list_item, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    /*getView(...) sets the view for circle list liked with the user's profile*/
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.friend_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.friend_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web.get(position));
        imageView.setImageBitmap(imageId.get(position));
        return rowView;
    }

}