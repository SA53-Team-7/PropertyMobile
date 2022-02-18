package com.team7.propertymobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecommendationListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    List<Property> propertyList;

    RecommendDataService recommendDataService = new RecommendDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_list);

        ProgressBar progressBar = findViewById(R.id.RecommendationListProgressBar);
        progressBar.setVisibility(View.VISIBLE);



        recommendDataService.callRecommendProjects("03", new RecommendDataService.RecommendResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<Property> projects) {
                progressBar.setVisibility(View.INVISIBLE);
                ListView RecommendationListView = findViewById(R.id.RecommendationListView);
                propertyList = projects;

                if (RecommendationListView != null)
                {
                    RecommendationAdapter adapter = new RecommendationAdapter(RecommendationListActivity.this, propertyList);
                    RecommendationListView.setAdapter(adapter);
                    RecommendationListView.setOnItemClickListener(RecommendationListActivity.this);
                }

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id) {

        Property recommendProperty = propertyList.get(position);
        Intent intent = new Intent(this, PropertyDetailsActivity.class);
        intent.putExtra("Property", recommendProperty);

        startActivity(intent);
    }

}
