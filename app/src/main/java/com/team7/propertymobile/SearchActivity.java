package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    List<Property> propertyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        EditText searchText = findViewById(R.id.searchEditText);
        String searchInput = searchText.getText().toString();

        if (id == R.id.searchButton)
        {
            Intent intent = new Intent(this, PropertyListActivity.class);
            intent.putExtra("Search", searchInput);
            startActivity(intent);
        }
    }
}