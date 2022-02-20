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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PropertyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    List<Property> propertyList;

    PropertyDataService propertyDataService = new PropertyDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);

        // set the toolbar as the app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.properties_toolbar);
        setSupportActionBar(myToolbar);

        // can click the icon (at the left of the activity title) to go back to previous page
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String searchInput = intent.getStringExtra("Search");


        ProgressBar progressBar = findViewById(R.id.propertyProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        // search properties based on the keyword input via REST API
        propertyDataService.searchProjects(searchInput, new PropertyDataService.ProjectsResponseListener() {
            @Override
            public void onError(String message) {
                Toast toast = Toast.makeText(PropertyListActivity.this, "something wrong", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onResponse(List<Property> projects) {
                progressBar.setVisibility(View.GONE);

                if (projects.size() == 0) {
                    TextView textView = findViewById(R.id.noPropertyFound);
                    textView.setVisibility(View.VISIBLE);
                }

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

    }
}