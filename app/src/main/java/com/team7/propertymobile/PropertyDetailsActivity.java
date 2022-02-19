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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    private String district;

    List<Property> propertyList;

    RecommendDataService recommendDataService = new RecommendDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_info);

        Intent intent = getIntent();
        selectedProperty = (Property) intent.getSerializableExtra("Property");

//        Location pasirRisMRT = new Location("Pasir Ris MRT", 1.37304331635804, 103.949284527763);
//        Location jurongEastMRT = new Location("Jurong East MRT", 1.33315281585758, 103.742286332403);
//        Location woodlandsMRT = new Location("Woodlands MRT", 1.43681962961519, 103.786066799253);
//        Location marinaBayMRT = new Location("Marina Bay MRT", 1.276410298755, 103.854595522263);
//        Location orchardMRT = new Location("Orchard MRT", 1.30398013681715, 103.832245244375);
//
//        locationList.add(pasirRisMRT);
//        locationList.add(jurongEastMRT);
//        locationList.add(woodlandsMRT);
//        locationList.add(marinaBayMRT);
//        locationList.add(orchardMRT);

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

                    String mrtDistance = "Nearest Train Station\n" + nearestLocation + "\n(" + df.format(nearestDistance) + " KM)\n~ " + df2.format(time) + " minutes walk" ;
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

        Button save = findViewById(R.id.saveButton);
        save.setOnClickListener(this);
        Button unsave = findViewById(R.id.unsaveButton);
        unsave.setOnClickListener(this);

        ToggleButton toggleButton = findViewById(R.id.favouriteToggleButton);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    saveFavourites(selectedProperty);
                }
                else {
                    saveFavourites(selectedProperty);
                }
            }
        });


        recommendDataService.recommendDistrictProject(selectedProperty.getProjectId(), new RecommendDataService.RecommendDistrictResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Transaction transactions) {

                district = transactions.getDistrict();

            }
        });


        recommendDataService.callRecommendProjects(district, new RecommendDataService.RecommendResponseListener() {
            @Override
            public void onError(String message) {


            }


            @Override
            public void onResponse(List<Property> projects) {
                progressBar.setVisibility(View.INVISIBLE);
                TextView recommendTextView1 = findViewById(R.id.recommendTextView1);
                TextView recommendTextView2 = findViewById(R.id.recommendTextView2);
                propertyList = projects;

                if (recommendTextView1 != null)
                {
                    recommendTextView1.setText(propertyList.get(0).getPropertyName());
                }

                if (recommendTextView2 != null)
                {
                    recommendTextView2.setText(propertyList.get(1).getPropertyName());
                }

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
//        Button save = findViewById(R.id.saveButton);
//        Button unsave = findViewById(R.id.unsaveButton);

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
        ProgressBar progressBar = findViewById(R.id.isSavedLoadProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        favouritesDataService.isSaved(selectedUserAndProperty, new FavouritesDataService.SaveResponseListener() {
            @Override
            public void onError(String message) {

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


//        if (id == R.id.saveButton || id == R.id.unsaveButton)
//        if (id == R.id.favouriteToggleButton)
//        {
//            ProgressBar progressBar = findViewById(R.id.isSavedLoadProgressBar);
//            progressBar.setVisibility(View.VISIBLE);
//
//            sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
////            Button save = findViewById(R.id.saveButton);
////            Button unsave = findViewById(R.id.unsaveButton);
//            ToggleButton fave = findViewById(R.id.favouriteToggleButton);
//
////            save.setVisibility(View.INVISIBLE);
////            unsave.setVisibility(View.INVISIBLE);
//
//            int selectedPropertyId = selectedProperty.getProjectId();
//            int selectedUserId = sharedPreferences.getInt(ID_KEY, -1);
//
//            JSONObject jsonSelectedProperty = new JSONObject();
//            try {
//                jsonSelectedProperty.put("projectId", selectedPropertyId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            JSONObject jsonSelecetedUser = new JSONObject();
//            try {
//                jsonSelecetedUser.put("userId", selectedUserId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            JSONObject selectedUserAndProperty = new JSONObject();
//            try {
//                selectedUserAndProperty.put("project", jsonSelectedProperty);
//                selectedUserAndProperty.put("user", jsonSelecetedUser);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            favouritesDataService.save(selectedUserAndProperty, new FavouritesDataService.SaveResponseListener() {
//                @Override
//                public void onError(String message) {
//
//                }
//
//                @Override
//                public void onResponse(boolean isSaved) {
//                    progressBar.setVisibility(View.INVISIBLE);
//                    if (isSaved)
//                    {
////                        unsave.setVisibility(View.VISIBLE);
//                        fave.setChecked(true);
//                    }
//                    else {
////                        save.setVisibility(View.VISIBLE);
//                        fave.setChecked(false);
//                    }
//                }
//            });
//        }
    }


    String segmentToRegion (String segment){
        String region = "";

        switch (segment) {
            case "CCR":
                region = "Core Central Region (CCR)";
                break;
            case "RCR":
                region = "Rest of Central Region (RCR)";
                break;
            case "OCR":
                region = "Outside Central Region (OCR)";
                break;
        }

        return region;
    }

    private void saveFavourites (Property selectedProperty) {
        ProgressBar progressBar = findViewById(R.id.isSavedLoadProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
//            Button save = findViewById(R.id.saveButton);
//            Button unsave = findViewById(R.id.unsaveButton);
        ToggleButton fave = findViewById(R.id.favouriteToggleButton);

//            save.setVisibility(View.INVISIBLE);
//            unsave.setVisibility(View.INVISIBLE);

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
//                if (isSaved)
//                {
////                        unsave.setVisibility(View.VISIBLE);
//                    fave.setChecked(true);
//                }
//                else {
////                        save.setVisibility(View.VISIBLE);
//                    fave.setChecked(false);
//                }
            }
        });

    }
}