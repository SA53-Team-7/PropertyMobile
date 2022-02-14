package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    public static final String USER_CREDENTIALS = "user_credentials";
    public static final String TOKEN_KEY = "token_key";
    private String token;

    List<Property> propertyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        Button calculatorButton = findViewById(R.id.toCalculatorButton);
        calculatorButton.setOnClickListener(this);

        Button loginButton = findViewById(R.id.toLoginButton);
        loginButton.setOnClickListener(this);

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
        Button loginButton = findViewById(R.id.toLoginButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        token = sharedPreferences.getString(TOKEN_KEY, null);

        if (token == null) {
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);
        }
        else {
            loginButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        }
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

        if (id == R.id.toCalculatorButton)
        {
            Intent intent = new Intent(this, LoanCalculatorActivity.class);
            startActivity(intent);
        }

        if (id == R.id.toLoginButton)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        if (id == R.id.logoutButton)
        {
            sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.clear();
            editor.apply();

            Button logoutButton = findViewById(R.id.logoutButton);
            logoutButton.setVisibility(View.INVISIBLE);

            Button loginButton = findViewById(R.id.toLoginButton);
            loginButton.setVisibility(View.VISIBLE);
        }
    }
}