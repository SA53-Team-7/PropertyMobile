package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    public static final String USER_CREDENTIALS = "user_credentials";
    public static final String USER_KEY = "user_key";
    public static final String NAME_KEY = "name_key";
//    public static final String ID_KEY = "id_key";
    private String token;

    List<Property> propertyList;

    LoginRegisterDataService loginRegisterDataService = new LoginRegisterDataService(this);


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

        Button testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(this);

        Button myListButton = findViewById(R.id.myListButton);
        myListButton.setOnClickListener(this);

        Button newPropertyListButton = findViewById(R.id.toNewPropertyListButton);
        newPropertyListButton.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
        Button loginButton = findViewById(R.id.toLoginButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button myListButton = findViewById(R.id.myListButton);
//        ProgressBar progressBar = findViewById(R.id.nameLoadProgressBar);
        TextView welcomeBack = findViewById(R.id.welcomeBackTextView);

        token = sharedPreferences.getString(USER_KEY, null);
        JSONObject user = new JSONObject();
        try {
            user.put("email", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (token == null) {
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);
        }
        else {
//            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            myListButton.setVisibility(View.VISIBLE);
            String name = sharedPreferences.getString(NAME_KEY, null);
//            int id = sharedPreferences.getInt(ID_KEY, -1);

            welcomeBack.setText("Welcome back, \n" + name + "!");
            welcomeBack.setVisibility(View.VISIBLE);



//            loginRegisterDataService.getName(user, new LoginRegisterDataService.GetNameResponseListener() {
//                @Override
//                public void onError(String message) {
//
//                }
//
//                @Override
//                public void onResponse(String name) {
//                    progressBar.setVisibility(View.INVISIBLE);
//                    welcomeBack.setText("Welcome back, \n" + name + "!");
//                    welcomeBack.setVisibility(View.VISIBLE);
//                }
//            });

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        EditText searchText = findViewById(R.id.searchEditText);
        String searchInput = searchText.getText().toString();

        if (id == R.id.testButton) {
            Intent intent = new Intent(this, ComparisonActivity.class);
            startActivity(intent);
        }

        if (id == R.id.searchButton)
        {
            if (TextUtils.isEmpty(searchText.getText().toString())) {
                searchText.setError("Please enter a search query.");
            }
            else {
                Intent intent = new Intent(this, PropertyListActivity.class);
                intent.putExtra("Search", searchInput);
                startActivity(intent);
            }

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
            TextView welcomeBack = findViewById(R.id.welcomeBackTextView);
            welcomeBack.setVisibility(View.INVISIBLE);
            sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.clear();
            editor.apply();

            Button logoutButton = findViewById(R.id.logoutButton);
            logoutButton.setVisibility(View.INVISIBLE);

            Button loginButton = findViewById(R.id.toLoginButton);
            loginButton.setVisibility(View.VISIBLE);

            Button myListButton = findViewById(R.id.myListButton);
            myListButton.setVisibility(View.INVISIBLE);
        }

        if (id == R.id.myListButton) {
            Intent intent = new Intent(this, FavouritesListActivity.class);

            startActivity(intent);
        }

        if (id == R.id.toNewPropertyListButton) {
            Intent intent = new Intent(this, NewPropertyListActivity.class);

            startActivity(intent);
        }
    }
}