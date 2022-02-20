package com.team7.propertymobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class FavouritesListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
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

        // set the toolbar as the app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.favourites_toolbar);
        setSupportActionBar(myToolbar);

        // can click the icon (at the left of the activity title) to go back to previous page
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // use shared preferences to store and pass user id
        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(ID_KEY, -1);

        refreshFavouritesList();

    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshFavouritesList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id) {

        Property selectedProperty = propertyList.get(position);
        Intent intent = new Intent(this, PropertyDetailsActivity.class);
        intent.putExtra("Property", selectedProperty);

        startActivity(intent);
    }

    // set shortlist info via REST API
    private void refreshFavouritesList() {
        ProgressBar progressBar = findViewById(R.id.favouritesListProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        favouritesDataService.callAllProjects(userId, new FavouritesDataService.CallMyListResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<Property> projects) {
                progressBar.setVisibility(View.INVISIBLE);

                if (projects.size() == 0) {
                    TextView textView = findViewById(R.id.noFavouritesFound);
                    textView.setVisibility(View.VISIBLE);
                }

                ListView favouritesListView = findViewById(R.id.favouritesListView);
                propertyList = projects;

                if (favouritesListView != null) {
                    FavouritesAdapter adapter = new FavouritesAdapter(FavouritesListActivity.this, propertyList);
                    favouritesListView.setAdapter(adapter);
                    favouritesListView.setOnItemClickListener(FavouritesListActivity.this);
                }
            }
        });
    }
}