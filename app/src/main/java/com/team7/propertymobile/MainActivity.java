package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    PropertyDataService propertyDataService = new PropertyDataService(this);
    PredictionDataService predictionDataService = new PredictionDataService(this);

    Boolean springFlag = false;
    Boolean flaskFlag = false;

    TextView error;
    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        error = findViewById(R.id.errorTextView);
        retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(this);

        wakeSpringWeb();
        wakePrediction();

        // create splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (springFlag == false) {
                    error.setVisibility(View.VISIBLE);
                    retryButton.setVisibility(View.VISIBLE);
                } else if (flaskFlag == false) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Unable to connect to the Prediction server. Price prediction functions will be unavailable");
                    continueToApp();
                }
            }
        }, 30000);

    }

    private void retryConnection() {
        error.setVisibility(View.INVISIBLE);
        retryButton.setVisibility(View.INVISIBLE);
        wakePrediction();
        wakeSpringWeb();
        if (flaskFlag == true & springFlag == true) {
            continueToApp();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                error.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
            }
        }, 10000);
    }

    private void continueToApp() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
        finish();
    }

    // speed up the data transfer for web
    private void wakeSpringWeb() {
        propertyDataService.getSingleProject("1", new PropertyDataService.SingleProjectResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Property project) {
                springFlag = true;
                if (flaskFlag) {
                    continueToApp();
                }
            }
        });
    }

    // speed up the data transfer for machine learning model
    private void wakePrediction() {
        JSONArray request = new JSONArray();
        JSONObject data = new JSONObject();
        try {
            data.put("projectId", "7");
            data.put("district", "7");
            data.put("floor_range", "11");
            data.put("floor_area", "43");
            data.put("top", "2014");
            data.put("tenure", "99");
            data.put("year", "22");
            data.put("month", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.put(data);
        predictionDataService.callPrediction(request, new PredictionDataService.PredictionResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String response) {
                flaskFlag = true;
                if (springFlag) {
                    continueToApp();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.retryButton) {
            retryConnection();
        }
    }
}