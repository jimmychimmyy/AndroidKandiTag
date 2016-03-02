package com.plur.kanditag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jim on 3/1/16.
 * This is an array adapter to display friends found using proximity
 */
public class FindFriendsAdapter extends ArrayAdapter<Person> {

    private final Context context;
    private final ArrayList<Person> personsArrayList;

    public FindFriendsAdapter(Context context, ArrayList<Person> personsArrayList) {
        super(context, R.layout.row_item_find_friends);
        this.context = context;
        this.personsArrayList = personsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // get rowview from inflater
        View rowView = inflater.inflate(R.layout.row_item_find_friends, parent, false);

        // init the xml from the rowView
        TextView screenname = (TextView) rowView.findViewById(R.id.screenname_holder);
        CircleImageView profileImage = (CircleImageView) rowView.findViewById(R.id.profileimage_holder);
        ImageView hasConnected = (ImageView) rowView.findViewById(R.id.hasConnectedIndicator);

        // set values
        screenname.setText(personsArrayList.get(position).getFirstName() + " " + personsArrayList.get(position).getLastName());

        // TODO need to get profile images from facebook
        //profileImage.setImageBitmap();

        // TODO if hasConnected is true, then show a red heart, if false then keep as gray heart

        return rowView;



    }
}
