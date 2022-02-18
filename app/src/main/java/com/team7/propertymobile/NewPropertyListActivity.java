package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

public class NewPropertyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    List<NewProperty> propertyList;

    NewPropertyDataService newPropertyDataService = new NewPropertyDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_property_list);

        ProgressBar progressBar = findViewById(R.id.newPropertyListProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        newPropertyDataService.callAllNewProjects(new NewPropertyDataService.NewProjectsResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<NewProperty> projects) {
                progressBar.setVisibility(View.GONE);

                ListView propertyListView = findViewById(R.id.newPropertyListView);
                propertyList = projects;

                if (propertyListView != null)
                {
                    NewPropertyAdapter adapter = new NewPropertyAdapter(NewPropertyListActivity.this, propertyList);
                    propertyListView.setAdapter(adapter);
                    propertyListView.setOnItemClickListener(NewPropertyListActivity.this);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id) {

//        Property selectedProperty = propertyList.get(position);
//        Intent intent = new Intent(this, PropertyDetailsActivity.class);
//        intent.putExtra("Property", selectedProperty);
//
//        startActivity(intent);
    }
}