package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    List<Property> propertyList;

    private final String[] projects = {"Glades", "Chicken Rice", "Team 7"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView propertyListView = findViewById(R.id.propertyListView);
        propertyList = Arrays.asList(
                new Property("Glades"),
                new Property("Chicken Rice"),
                new Property("Team 7"),
                new Property("Test")
        );

        if (propertyListView != null)
        {
            PropertyAdapter adapter = new PropertyAdapter(this, propertyList);
            propertyListView.setAdapter(adapter);
            propertyListView.setOnItemClickListener(this);
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id)
    {
        TextView textView = view.findViewById(R.id.propertyTextView);
        Property selectedProperty = propertyList.get(position);
        String caption = selectedProperty.getPropertyName();

        Toast toast = Toast.makeText(this, caption, Toast.LENGTH_SHORT);
        toast.show();
    }
}