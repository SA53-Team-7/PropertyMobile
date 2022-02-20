package com.team7.propertymobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class PropertyDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    public static final String USER_CREDENTIALS = "user_credentials";
    public static final String USER_KEY = "user_key";
    public static final String NAME_KEY = "name_key";
    public static final String ID_KEY = "id_key";

    MapDataService mapDataService = new MapDataService(this);
    FavouritesDataService favouritesDataService = new FavouritesDataService(this);

    Property selectedProperty;

    String compare1;
    String compare2;

    Button clearAddButton;
    Button compareButton;

    Transaction transactionDistrict;

    List<Property> propertyList;

    RecommendDataService recommendDataService = new RecommendDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_info);

        Intent intent = getIntent();
        selectedProperty = (Property) intent.getSerializableExtra("Property");

        // set the toolbar as the app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.propertyDetails_toolbar);
        setSupportActionBar(myToolbar);

        // can click the icon (at the left of the property's name and location(street)) to go back to previous page
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(selectedProperty.getPropertyName());
        ab.setSubtitle(selectedProperty.getStreet());

        TextView regionInfoTextView = findViewById(R.id.regionInfoTextView);
        String region = segmentToRegion(selectedProperty.getRegion());
        regionInfoTextView.setText(region);

        TextView distanceFromTrain = findViewById(R.id.distanceToTrainTextView);
        ProgressBar distanceProgressBar = findViewById(R.id.distanceLoadProgressBar);
        distanceProgressBar.setVisibility(View.VISIBLE);

        // set the distance between the property and its nearest MRT station
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

        // set the property's map
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

        compareButton = findViewById(R.id.compareButton);
        compareButton.setOnClickListener(this);

        clearAddButton = findViewById(R.id.clearAndCompareButton);
        clearAddButton.setOnClickListener(this);

        readCompare();

        if (!compare1.equals("-") & !compare2.equals("-")){
            clearAddButton.setVisibility(View.VISIBLE);
            compareButton.setVisibility(View.INVISIBLE);
        }

        ToggleButton toggleButton = findViewById(R.id.favouriteToggleButton);
        toggleButton.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
        int selectedUserId = sharedPreferences.getInt(ID_KEY, -1);

        ProgressBar recommendLoadBar = findViewById(R.id.recommendLoadProgressBar);
        recommendLoadBar.setVisibility(View.VISIBLE);

        ProgressBar districtLoadBar = findViewById(R.id.districtLoadProgressBar);
        districtLoadBar.setVisibility(View.VISIBLE);

        // set the recommended properties based on same district
        recommendDataService.recommendDistrictProject(selectedProperty.getProjectId(), new RecommendDataService.RecommendDistrictResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Transaction transactions) {
                districtLoadBar.setVisibility(View.INVISIBLE);
                TextView districtInfoTextView = findViewById(R.id.districtInfoTextView);
                districtInfoTextView.setVisibility(View.VISIBLE);
                transactionDistrict = transactions;

                if (districtInfoTextView != null)
                {
                    districtInfoTextView.setText("District "+transactionDistrict.getDistrict());

                    recommendDataService.callRecommendProjects(transactionDistrict.getDistrict(), new RecommendDataService.RecommendResponseListener() {
                        @Override
                        public void onError(String message) {


                        }


                        @Override
                        public void onResponse(List<Property> projects) {
                            recommendLoadBar.setVisibility(View.INVISIBLE);
                            TextView recommendTextView1 = findViewById(R.id.recommendTextView1);
                            TextView recommendTextView2 = findViewById(R.id.recommendTextView2);
                            propertyList = projects;

                            if (recommendTextView1 != null)
                            {
                                recommendTextView1.setText(propertyList.get(0).getPropertyName());
                                recommendTextView1.setVisibility(View.VISIBLE);
                            }

                            if (recommendTextView2 != null)
                            {
                                recommendTextView2.setText(propertyList.get(1).getPropertyName());
                                recommendTextView2.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);

        ToggleButton fave = findViewById(R.id.favouriteToggleButton);

        // check whether the property is saved in shortlist
        int selectedPropertyId = selectedProperty.getProjectId();
        int selectedUserId = sharedPreferences.getInt(ID_KEY, -1);

        JSONObject jsonSelectedProperty = new JSONObject();
        try {
            jsonSelectedProperty.put("projectId", selectedPropertyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonSelectedUser = new JSONObject();
        try {
            jsonSelectedUser.put("userId", selectedUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject selectedUserAndProperty = new JSONObject();
        try {
            selectedUserAndProperty.put("project", jsonSelectedProperty);
            selectedUserAndProperty.put("user", jsonSelectedUser);
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
                        fave.setChecked(true);
                    }
                    else {
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

        if (id == R.id.compareButton) {
            setComparisonProperty();
        }

        if (id == R.id.clearAndCompareButton) {
            clearCompare();
            setComparisonProperty();
        }

        if (id == R.id.recommendTextView1)
        {
            Property recommendProperty1 = propertyList.get(0);
            Intent intent = new Intent(this, PropertyDetailsActivity.class);
            intent.putExtra("Property", recommendProperty1);

            startActivity(intent);

        }

        if (id == R.id.recommendTextView2)
        {
            Property recommendProperty2 = propertyList.get(1);
            Intent intent = new Intent(this, PropertyDetailsActivity.class);
            intent.putExtra("Property", recommendProperty2);

            startActivity(intent);
        }

        if (id == R.id.favouriteToggleButton) {
            sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
            int selectedUserId = sharedPreferences.getInt(ID_KEY, -1);

            ToggleButton fave = findViewById(R.id.favouriteToggleButton);

            if (selectedUserId == -1) {
                Intent intent = new Intent(PropertyDetailsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            else {
                saveFavourites(selectedProperty);
                if (fave.isChecked()) {
                    fave.setChecked(true);
                }
                else {
                    fave.setChecked(false);
                }
            }

        }
    }

    // store and set selected properties for comparison
    private void readCompare() {
        SharedPreferences pref = getSharedPreferences("compare", MODE_PRIVATE);
        compare1 = pref.getString("compare1", "-");
        compare2 = pref.getString("compare2", "-");
    }

    private void setComparisonProperty() {
        readCompare();
        if (compare1.equals(String.valueOf(selectedProperty.getProjectId())) || compare2.equals(String.valueOf(selectedProperty.getProjectId()))){
            Toast toast = Toast.makeText(this, "This property is already selected", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            if (compare1.equals("-")) {
                SharedPreferences setPref = getSharedPreferences("compare", MODE_PRIVATE);
                SharedPreferences.Editor editor = setPref.edit();
                editor.putString("compare1", String.valueOf(selectedProperty.getProjectId()));
                editor.commit();
                Toast toast = Toast.makeText(this, "Added to comparison as 1", Toast.LENGTH_LONG);
                toast.show();
            }
            else if (compare2.equals("-")){
                SharedPreferences setPref = getSharedPreferences("compare", MODE_PRIVATE);
                SharedPreferences.Editor editor = setPref.edit();
                editor.putString("compare2", String.valueOf(selectedProperty.getProjectId()));
                editor.commit();
                Toast toast = Toast.makeText(this, "Added to comparison as 2", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void clearCompare() {
        SharedPreferences setPref = getSharedPreferences("compare", MODE_PRIVATE);
        SharedPreferences.Editor editor = setPref.edit();
        editor.putString("compare1", "-");
        editor.putString("compare2", "-");
        editor.commit();

        clearAddButton.setVisibility(View.INVISIBLE);
        compareButton.setVisibility(View.VISIBLE);


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

    // store user and project id for adding the property to shortlist
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

        JSONObject jsonSelectedUser = new JSONObject();
        try {
            jsonSelectedUser.put("userId", selectedUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject selectedUserAndProperty = new JSONObject();
        try {
            selectedUserAndProperty.put("project", jsonSelectedProperty);
            selectedUserAndProperty.put("user", jsonSelectedUser);
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