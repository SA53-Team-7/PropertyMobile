package com.team7.propertymobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

public class NewPropertyListActivity extends AppCompatActivity {
    List<NewProperty> propertyList;

    NewPropertyDataService newPropertyDataService = new NewPropertyDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_property_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.newpropertypredict_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ProgressBar progressBar = findViewById(R.id.newPropertyListProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        // set new launch property via REST API
        newPropertyDataService.callAllNewProjects(new NewPropertyDataService.NewProjectsResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<NewProperty> projects) {
                progressBar.setVisibility(View.GONE);

                ListView propertyListView = findViewById(R.id.newPropertyListView);
                propertyList = projects;

                if (propertyListView != null) {
                    NewPropertyAdapter adapter = new NewPropertyAdapter(NewPropertyListActivity.this, propertyList);
                    propertyListView.setAdapter(adapter);
                }
            }
        });
    }
}