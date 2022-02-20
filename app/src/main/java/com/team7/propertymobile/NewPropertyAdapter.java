package com.team7.propertymobile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class NewPropertyAdapter extends ArrayAdapter<Property> {

    protected List<NewProperty> propertyList;

    public NewPropertyAdapter(Context context, List<NewProperty> propertyList) {
        super(context, R.layout.item_new_property);
        this.propertyList = propertyList;

        addAll(new Property[propertyList.size()]);
    }

    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.item_new_property, parent, false);
        }

        TextView textView = view.findViewById(R.id.newPropertyNameTextView);
        textView.setText(propertyList.get(position).getPropertyName());

        TextView textView1 = view.findViewById(R.id.newPropertyDateTextView);
        textView1.setText(propertyList.get(position).getDate());

        Locale sg = new Locale("en", "SG");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sg);

        TextView textView2 = view.findViewById(R.id.landTransactionTextView);
        textView2.setText(String.format("Land Price/SqFt: %s", formatter.format(propertyList.get(position).getLandPrice())));

        TextView textView3 = view.findViewById(R.id.predictedPriceTextView);
        textView3.setText(String.format("Predicted Price/SqFt: %s", formatter.format(propertyList.get(position).getPredictedPrice())));

        return view;
    }
}
