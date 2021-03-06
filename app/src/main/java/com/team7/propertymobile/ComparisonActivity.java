package com.team7.propertymobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparisonActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout compareLayout;
    LinearLayout leftColumn;
    LinearLayout rightColumn;
    LinearLayout blankColumn;

    Property project1;
    Property project2;

    TextView leftName;
    TextView leftDistrict;
    TextView leftRoad;
    TextView leftMRT;
    TextView leftTime;
    TextView leftPrice;
    TextView leftFloor;
    TextView leftTenure;
    TextView leftTOP;

    TextView rightName;
    TextView rightDistrict;
    TextView rightRoad;
    TextView rightMRT;
    TextView rightTime;
    TextView rightPrice;
    TextView rightFloor;
    TextView rightTenure;
    TextView rightTOP;

    ImageView leftImage;
    ImageView rightImage;

    TextView errorText;

    Button search;
    Button smallSearch;
    Button clearButton;

    ProgressBar spinny;

    PropertyDataService propertyDataService = new PropertyDataService(this);
    TransactionDataService transactionDataService = new TransactionDataService(this);
    MapDataService mapDataService = new MapDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        // set the toolbar as the app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.compare_toolbar);
        setSupportActionBar(myToolbar);

        // can click the icon (at the left of the activity title) to go back to previous page
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setViews();

        // set property info from PropertyDetailsActivity to this activity
        SharedPreferences pref = getSharedPreferences("compare", MODE_PRIVATE);
        String compare1 = pref.getString("compare1", "-");
        String compare2 = pref.getString("compare2", "-");

        if (compare1.equals("-")) {
            setNoneToCompare();
        } else if (compare2.equals("-")) {
            setCompareOneOnly();
            loadProjectData(compare1, 1);
        } else {
            loadProjectData(compare1, 1);
            loadProjectData(compare2, 2);
        }
    }

    private void setViews() {
        compareLayout = findViewById(R.id.compareLayout);
        leftColumn = findViewById(R.id.leftCompareLayout);
        rightColumn = findViewById(R.id.rightCompareLayout);
        blankColumn = findViewById(R.id.blankCompareLayout);

        leftName = findViewById(R.id.leftNameTextView);
        leftDistrict = findViewById(R.id.leftDistrictTextView);
        leftRoad = findViewById(R.id.leftRoadTextView);
        leftMRT = findViewById(R.id.leftMRTTextView);
        leftTime = findViewById(R.id.leftTimeTextView);
        leftPrice = findViewById(R.id.leftPriceTextView);
        leftFloor = findViewById(R.id.leftRangeTextView);
        leftTenure = findViewById(R.id.leftTenureTextView);
        leftTOP = findViewById(R.id.leftTOPTextView);
        leftImage = findViewById(R.id.leftStaticMapImageView);

        rightName = findViewById(R.id.rightNameTextView);
        rightDistrict = findViewById(R.id.rightDistrictTextView);
        rightRoad = findViewById(R.id.rightRoadTextView);
        rightMRT = findViewById(R.id.rightMRTTextView);
        rightTime = findViewById(R.id.rightTimeTextView);
        rightPrice = findViewById(R.id.rightPriceTextView);
        rightFloor = findViewById(R.id.rightRangeTextView);
        rightTenure = findViewById(R.id.rightTenureTextView);
        rightTOP = findViewById(R.id.rightTOPTextView);
        rightImage = findViewById(R.id.rightStaticMapImageView);

        search = findViewById(R.id.searchButton);
        search.setOnClickListener(this);
        smallSearch = findViewById(R.id.smallSearchButton);
        smallSearch.setOnClickListener(this);
        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);

        spinny = findViewById(R.id.loadProgressBar);
        errorText = findViewById(R.id.errorTextView);
    }

    private void setNoneToCompare() {
        compareLayout.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        spinny.setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
        smallSearch.setVisibility(View.GONE);
    }

    private void setCompareOneOnly() {
        leftColumn.setVisibility(View.INVISIBLE);
        compareLayout.setVisibility(View.VISIBLE);
        rightColumn.setVisibility(View.GONE);
        blankColumn.setVisibility(View.VISIBLE);
        smallSearch.setVisibility(View.VISIBLE);
    }

    private void onDataLoad() {
        compareLayout.setVisibility(View.VISIBLE);
        spinny.setVisibility(View.GONE);
        if (rightColumn.getVisibility() == View.GONE) {
            leftColumn.setVisibility(View.VISIBLE);
        }
        clearButton.setVisibility(View.VISIBLE);
    }

    // set single property info via REST API
    private void loadProjectData(String pId, Integer side) {
        propertyDataService.getSingleProject(pId, new PropertyDataService.SingleProjectResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Property project) {
                if (side == 1) {
                    project1 = project;
                    leftName.setText(project.getPropertyName());
                    leftRoad.setText(project.getStreet());

                } else {
                    project2 = project;
                    rightName.setText(project.getPropertyName());
                    rightRoad.setText(project.getStreet());

                }
                loadMRTData(project, side);
                loadTransactionsData(project, side);
                loadMiniMaps(project, side);

            }
        });
    }

    // set the distance between the property and nearest MRT station
    private void loadMRTData(Property selectedProperty, Integer side) {
        mapDataService.distanceListLocations(selectedProperty, new MapDataService.DistanceListLocationsResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String nearestLocation, double nearestDistance) {
                if (nearestDistance == -1.00) {
                    if (side == 1) {
                        leftMRT.setText(R.string.gps_unavailable);
                        leftTime.setText(R.string.gps_unavailable);
                    } else {
                        rightMRT.setText(R.string.gps_unavailable);
                        rightTime.setText(R.string.gps_unavailable);
                    }
                } else {
                    DecimalFormat df = new DecimalFormat("0.00");
                    df.setRoundingMode(RoundingMode.UP);

                    DecimalFormat df2 = new DecimalFormat("0");
                    df.setRoundingMode(RoundingMode.UP);

                    double time = nearestDistance / 5 * 60;

                    if (side == 1) {
                        leftMRT.setText(String.format("%s (%s KM)", nearestLocation, df.format(nearestDistance)));
                        leftTime.setText(String.format("%s minutes walk", df2.format(time)));
                    } else {
                        rightMRT.setText(String.format("%s (%s KM)", nearestLocation, df.format(nearestDistance)));
                        rightTime.setText(String.format("%s minutes walk", df2.format(time)));
                    }
                }
            }
        });
    }

    // set the transaction info of the selected property via REST API
    private void loadTransactionsData(Property selectedProperty, Integer side) {
        transactionDataService.callTransactionsById(selectedProperty.getProjectId(), new TransactionDataService.TransactionResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<Transaction> transactions) {
                String tenure;
                String district;
                String year;

                if (transactions != null) {

                    Transaction ref = transactions.get(1);

                    district = ref.getDistrict();

                    if (ref.getTenure().equals("Freehold")) {
                        tenure = ref.getTenure();
                        year = "0";
                    } else {
                        int indexTenure = ref.getTenure().indexOf("y");
                        int indexYear = ref.getTenure().indexOf("2");
                        if (indexYear == -1) {
                            indexYear = ref.getTenure().indexOf("1");
                        }
                        String temp = ref.getTenure();
                        tenure = (String) temp.substring(0, indexTenure - 1);
                        year = (String) temp.substring(indexYear);
                    }

                    ArrayList<String> floorRange = new ArrayList<>();
                    ArrayList<String> floorArea = new ArrayList<>();
                    ArrayList<Double> prices = new ArrayList<>();
                    for (Transaction t : transactions) {
                        if (!floorRange.contains(t.getFloorRange())) {
                            floorRange.add(t.getFloorRange());
                        }
                        if (!floorArea.contains(String.valueOf(t.getFloorArea()))) {
                            floorArea.add(String.valueOf(t.getFloorArea()));
                        }
                        prices.add(t.getPrice());
                    }
                    Collections.sort(floorArea);
                    Collections.sort(floorRange);

                    Double averagePrice = prices.stream().mapToDouble(d -> d).average().orElse(0.0);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    formatter.setRoundingMode(RoundingMode.UP);


                    String minFloor = floorArea.get(floorArea.size() - 1);
                    String maxFloor = floorArea.get(0);
                    String floors = minFloor + " - " + maxFloor;

                    if (side == 1) {
                        leftDistrict.setText(district);
                        leftTenure.setText(String.format("%s years", tenure));
                        leftTOP.setText(year);
                        leftFloor.setText(floors);
                        leftPrice.setText(formatter.format(averagePrice));
                    } else {
                        rightDistrict.setText(district);
                        rightTenure.setText(String.format("%s years", tenure));
                        rightTOP.setText(year);
                        rightFloor.setText(floors);
                        rightPrice.setText(formatter.format(averagePrice));
                    }
                }
                onDataLoad();
            }
        });
    }

    // set property's map via OneMap API and coordinate converter
    private void loadMiniMaps(Property selectedProperty, Integer side) {
        mapDataService.callStaticMapAfterConversion(selectedProperty.getxCoordinates(), selectedProperty.getyCoordinates(), new MapDataService.CallStaticMapAfterConversionResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Bitmap bitmap) {
                if (bitmap == null)
                    if (side == 1) {
                        leftImage.setImageResource(R.drawable.no_map);
                    } else {
                        rightImage.setImageResource(R.drawable.no_map);
                    }
                else if (side == 1) {
                    leftImage.setImageBitmap(bitmap);
                } else {
                    rightImage.setImageBitmap(bitmap);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.searchButton || id == R.id.smallSearchButton) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        if (id == R.id.clearButton) {
            SharedPreferences setPref = getSharedPreferences("compare", MODE_PRIVATE);
            SharedPreferences.Editor editor = setPref.edit();
            editor.putString("compare1", "-");
            editor.putString("compare2", "-");
            editor.apply();

            setNoneToCompare();
        }
    }
}