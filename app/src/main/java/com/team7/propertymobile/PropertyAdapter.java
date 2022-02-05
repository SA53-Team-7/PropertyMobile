package com.team7.propertymobile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class PropertyAdapter extends ArrayAdapter<Property> {

    protected List<Property> propertyList;

//    protected String[] projectName;


    public PropertyAdapter(Context context, List<Property> propertyList)
    {
        super(context, R.layout.item_property);
        this.propertyList = propertyList;

        addAll(new Property[propertyList.size()]);
    }

    public View getView(int position, View view, @NonNull ViewGroup parent)
    {
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.item_property, parent, false);
        }

        TextView textView = view.findViewById(R.id.propertyTextView);
        textView.setText(propertyList.get(position).getPropertyName());

        TextView textView1 = view.findViewById(R.id.streetTextView);
        textView1.setText(propertyList.get(position).getStreet());

        return view;
    }
}
