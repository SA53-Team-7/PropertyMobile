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

public class TransactionAdapter extends ArrayAdapter<Transaction> {
    protected List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        super(context, R.layout.item_transaction);
        this.transactionList = transactionList;

        addAll(new Transaction[transactionList.size()]);
    }

    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.item_transaction, parent, false);
        }


        TextView priceTextView = view.findViewById(R.id.priceTextView);
        Locale sg = new Locale("en", "SG");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sg);
        priceTextView.setText(String.format("Price: %s", formatter.format(transactionList.get(position).getPrice())));

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        dateTextView.setText(String.format("Date: %s", transactionList.get(position).getContractDate()));

        TextView floorRangeTextView = view.findViewById(R.id.floorRangeTextView);
        floorRangeTextView.setText(String.format("Floor Range: %s", transactionList.get(position).getFloorRange()));

        TextView floorAreaTextView = view.findViewById(R.id.floorAreaTextView);
        floorAreaTextView.setText(String.format("Floor Area: %s sq m", transactionList.get(position).getFloorArea()));

        return view;
    }
}
