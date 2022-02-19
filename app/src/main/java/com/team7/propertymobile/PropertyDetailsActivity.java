package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PropertyDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    public static final String USER_CREDENTIALS = "user_credentials";
    public static final String USER_KEY = "user_key";
    public static final String NAME_KEY = "name_key";
    public static final String ID_KEY = "id_key";

    MapDataService mapDataService = new MapDataService(this);
    FavouritesDataService favouritesDataService = new FavouritesDataService(this);

    Property selectedProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_info);

        Intent intent = getIntent();
        selectedProperty = (Property) intent.getSerializableExtra("Property");

        TextView projectInfoTextView = findViewById(R.id.projectInfoTextView);
        projectInfoTextView.setText(selectedProperty.getPropertyName());

        TextView regionInfoTextView = findViewById(R.id.regionInfoTextView);
        String region = segmentToRegion(selectedProperty.getRegion());
        regionInfoTextView.setText(region);

        TextView streetInfoTextView = findViewById(R.id.streetInfoTextView);
        streetInfoTextView.setText(selectedProperty.getStreet());

        TextView distanceFromTrain = findViewById(R.id.distanceToTrainTextView);
        ProgressBar distanceProgressBar = findViewById(R.id.distanceLoadProgressBar);
        distanceProgressBar.setVisibility(View.VISIBLE);

        mapDataService.distanceListLocations(selectedProperty, new MapDataService.DistanceListLocationsResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String nearestLocation, double nearestDistance) {
                distanceProgressBar.setVisibility(View.GONE);
                if (nearestDistance == -1.00)
                {
                    distanceFromTrain.setText("GPS Coordinates Unavailable");
                    distanceFromTrain.setVisibility(View.VISIBLE);
                }
                else
                {
                    DecimalFormat df = new DecimalFormat("0.00");
                    df.setRoundingMode(RoundingMode.UP);

                    DecimalFormat df2 = new DecimalFormat("0");
                    df.setRoundingMode(RoundingMode.UP);

                    double time = nearestDistance / 5 * 60;

                    String mrtDistance = nearestLocation + "\n(" + df.format(nearestDistance) + " KM)\n~ " + df2.format(time) + " minutes walk" ;
                    distanceFromTrain.setText(mrtDistance);
                    distanceFromTrain.setVisibility(View.VISIBLE);
                }
            }
        });


        ImageView imageView = findViewById(R.id.staticMapImageView);
        ProgressBar progressBar = findViewById(R.id.mapLoadProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        mapDataService.callStaticMapAfterConversion(selectedProperty.getxCoordinates(), selectedProperty.getyCoordinates(), new MapDataService.CallStaticMapAfterConversionResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Bitmap bitmap) {
                progressBar.setVisibility(View.GONE);
                if (bitmap == null)
                    imageView.setImageResource(R.drawable.no_map);
                else
                    imageView.setImageBitmap(bitmap);
            }
        });


        Button button = findViewById(R.id.transactionButton);
        button.setOnClickListener(this);

        Button priceEstimator = findViewById(R.id.priceEstimatorButton);
        priceEstimator.setOnClickListener(this);

        ToggleButton toggleButton = findViewById(R.id.favouriteToggleButton);

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
        int selectedUserId = sharedPreferences.getInt(ID_KEY, -1);

        if (selectedUserId == -1) {
            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PropertyDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
//                    int selectedUserId = sharedPreferences.getInt(ID_KEY, -1);

                        if (b) {
                            saveFavourites(selectedProperty);
                        }
                        else {
                            saveFavourites(selectedProperty);
                        }

                }
            });
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);

        ToggleButton fave = findViewById(R.id.favouriteToggleButton);

        int selectedPropertyId = selectedProperty.getProjectId();
        int selectedUserId = sharedPreferences.getInt(ID_KEY, -1);

        JSONObject jsonSelectedProperty = new JSONObject();
        try {
            jsonSelectedProperty.put("projectId", selectedPropertyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonSelecetedUser = new JSONObject();
        try {
            jsonSelecetedUser.put("userId", selectedUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject selectedUserAndProperty = new JSONObject();
        try {
            selectedUserAndProperty.put("project", jsonSelectedProperty);
            selectedUserAndProperty.put("user", jsonSelecetedUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (selectedUserId == -1) {
            fave.setChecked(false);
        }
        else {
            ProgressBar progressBar = findViewById(R.id.isSavedLoadProgressBar);
            progressBar.setVisibility(View.VISIBLE);

            favouritesDataService.isSaved(selectedUserAndProperty, new FavouritesDataService.SaveResponseListener() {
                @Override
                public void onError(String message) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onResponse(boolean isSaved) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (isSaved)
                    {
//                    unsave.setVisibility(View.VISIBLE);
                        fave.setChecked(true);
                    }
                    else {
//                    save.setVisibility(View.VISIBLE);
                        fave.setChecked(false);
                    }
                }
            });
        }

    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if(id == R.id.transactionButton)
        {
            Intent intent = new Intent(this, TransactionListActivity.class);
            intent.putExtra("Property", selectedProperty);

            startActivity(intent);
        }

        if (id == R.id.priceEstimatorButton)
        {
            Intent intent = new Intent(this, PriceEstimatorActivity.class);
            intent.putExtra("Property", selectedProperty);

            startActivity(intent);
        }
    }


    String segmentToRegion (String segment){
        String region = "";

        switch (segment) {
            case "CCR":
                region = "Core Central Region";
                break;
            case "RCR":
                region = "Rest of Central Region";
                break;
            case "OCR":
                region = "Outside Central Region";
                break;
        }

        return region;
    }

    public void saveFavourites (Property selectedProperty) {
        ProgressBar progressBar = findViewById(R.id.isSavedLoadProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
        ToggleButton fave = findViewById(R.id.favouriteToggleButton);


        int selectedPropertyId = selectedProperty.getProjectId();
        int selectedUserId = sharedPreferences.getInt(ID_KEY, -1);

        JSONObject jsonSelectedProperty = new JSONObject();
        try {
            jsonSelectedProperty.put("projectId", selectedPropertyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonSelecetedUser = new JSONObject();
        try {
            jsonSelecetedUser.put("userId", selectedUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject selectedUserAndProperty = new JSONObject();
        try {
            selectedUserAndProperty.put("project", jsonSelectedProperty);
            selectedUserAndProperty.put("user", jsonSelecetedUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        favouritesDataService.save(selectedUserAndProperty, new FavouritesDataService.SaveResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(boolean isSaved) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}