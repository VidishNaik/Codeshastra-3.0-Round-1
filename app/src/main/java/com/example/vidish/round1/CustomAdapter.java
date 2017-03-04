package com.example.vidish.round1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vidish on 29-01-2017.
 */
public class CustomAdapter extends ArrayAdapter {
    public CustomAdapter(Context context, List objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_new_tech, parent, false);
        }

        TextView textView = (TextView) listItemView.findViewById(R.id.textview);
//        SpannableString content = new SpannableString((String)getItem(position));
//        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText((String)getItem(position));
        return listItemView;
    }
}
