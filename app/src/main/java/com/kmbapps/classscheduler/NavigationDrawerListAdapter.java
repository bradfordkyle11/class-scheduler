package com.kmbapps.classscheduler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Kyle on 3/4/2015.
 */
public class NavigationDrawerListAdapter extends ArrayAdapter<String> {
    private final int SCHEDULE = 0;
    private final int SCHEDULE_DESIGNER = 1;
    private final int MY_CLASSES = 2;
    private final int MY_CALENDAR = 3;

    private final Context context;
    private final String[] titles;
    NavigationDrawerListAdapter(Context context, int resource, String[] titles){
        super(context, resource, titles);
        this.context = context;
        this.titles = titles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_nav_drawer, parent, false);
        ImageView icon = (ImageView) rowView.findViewById(R.id.icon);

        //set icons for the nav drawer
        switch(position){
            case SCHEDULE:
                icon.setBackgroundResource(R.drawable.ic_view_week_white_24dp);
                break;
            case SCHEDULE_DESIGNER:
                icon.setBackgroundResource(R.drawable.ic_create_white_24dp);
                break;
            case MY_CLASSES:
                icon.setBackgroundResource(R.drawable.ic_class_white_24dp);
                break;
            case MY_CALENDAR:
                icon.setBackgroundResource(R.drawable.ic_today_white_24dp);
                break;
        }

        TextView textView = (TextView) rowView.findViewById(R.id.text1);
        textView.setText(titles[position]);
        return rowView;
    }
}
