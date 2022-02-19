package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

public class FavouritesListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private SharedPreferences sharedPreferences;
    public static final String USER_CREDENTIALS = "user_credentials";
    public static final String ID_KEY = "id_key";
    private int userId = -1;

    List<Property> propertyList;

    FavouritesDataService favouritesDataService = new FavouritesDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_list);

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(ID_KEY, -1);

        ProgressBar progressBar = findViewById(R.id.favouritesListProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        favouritesDataService.callAllProjects(userId, new FavouritesDataService.CallMyListResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<Property> projects) {
                progressBar.setVisibility(View.INVISIBLE);
                ListView favouritesListView = findViewById(R.id.favouritesListView);
                propertyList = projects;

                if (favouritesListView != null)
                {
                    FavouritesAdapter adapter = new FavouritesAdapter(FavouritesListActivity.this, propertyList);
                    favouritesListView.setAdapter(adapter);
                    favouritesListView.setOnItemClickListener(FavouritesListActivity.this);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id) {

        Property selectedProperty = propertyList.get(position);
        Intent intent = new Intent(this, PropertyDetailsActivity.class);
        intent.putExtra("Property", selectedProperty);

        startActivity(intent);
    }
}