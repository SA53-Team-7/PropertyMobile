package com.team7.propertymobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class PropertyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    List<Property> propertyList;

    PropertyDataService propertyDataService = new PropertyDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.properties_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String searchInput = intent.getStringExtra("Search");

//        ListView propertyListView = findViewById(R.id.propertyListView);
//        propertyList = Arrays.asList(
//                new Property("London", "test"),
//                new Property("Singapore", "test"),
//                new Property("Kuala Lumpur", "test"),
//                new Property("Paris", "test")
//        );

        ProgressBar progressBar = findViewById(R.id.propertyProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        propertyDataService.searchProjects(searchInput, new PropertyDataService.ProjectsResponseListener() {
            @Override
            public void onError(String message) {
                Toast toast = Toast.makeText(PropertyListActivity.this, "something wrong", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onResponse(List<Property> projects) {
                progressBar.setVisibility(View.GONE);

                ListView propertyListView = findViewById(R.id.propertyListView);
                propertyList = projects;

                if (propertyListView != null)
                {
                    PropertyAdapter adapter = new PropertyAdapter(PropertyListActivity.this, propertyList);
                    propertyListView.setAdapter(adapter);
                    propertyListView.setOnItemClickListener(PropertyListActivity.this);
                }
            }
        });


    }


    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id)
    {

        Property selectedProperty = propertyList.get(position);
        Intent intent = new Intent(this, PropertyDetailsActivity.class);
        intent.putExtra("Property", selectedProperty);

        startActivity(intent);


//        TextView textView = view.findViewById(R.id.propertyTextView);
//        String caption = selectedProperty.getPropertyName();

//        Toast toast = Toast.makeText(this, caption, Toast.LENGTH_SHORT);
//        toast.show();

//        propertyDataService.getCityID(caption, new PropertyDataService.VolleyResponseListener() {
//            @Override
//            public void onError(String message) {
//                Toast toast = Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//
//            @Override
//            public void onResponse(String cityID) {
//                Toast toast = Toast.makeText(MainActivity.this, "Returned an ID of " + cityID, Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        });


    }
}