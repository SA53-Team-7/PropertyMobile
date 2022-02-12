package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PropertyDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    MapDataService mapDataService = new MapDataService(this);

    Property selectedProperty;

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
        button.setOnClickListener(this);

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
}