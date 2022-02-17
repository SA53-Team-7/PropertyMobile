package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparisonActivity extends AppCompatActivity {

    LinearLayout compareLayout;

    Property project1;
    Property project2;

    TextView leftName;
    TextView leftRegion;
    TextView leftRoad;
    TextView leftMRT;
    TextView leftTime;
    TextView leftPrice;
    TextView leftFloor;
    TextView leftTenure;
    TextView leftTOP;

    TextView rightName;
    TextView rightRegion;
    TextView rightRoad;
    TextView rightMRT;
    TextView rightTime;
    TextView rightPrice;
    TextView rightFloor;
    TextView rightTenure;
    TextView rightTOP;

    PropertyDataService propertyDataService = new PropertyDataService(this);
    TransactionDataService transactionDataService = new TransactionDataService(this);
    MapDataService mapDataService = new MapDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        setViews();

        //set test shared preferences
        SharedPreferences setPref = getSharedPreferences("compare", MODE_PRIVATE);
        SharedPreferences.Editor editor = setPref.edit();
        editor.putString("compare1", "2");
        editor.putString("compare2", "32");
        editor.commit();


        SharedPreferences pref = getSharedPreferences("compare", MODE_PRIVATE);
        String compare1 = pref.getString("compare1", "-");
        String compare2 = pref.getString("compare2", "-");

        if (compare1.equals("-")) {
            setNoneToCompare();
        }
        else if (compare2.equals("-")){
            setCompareOneOnly();
        }
        else {
            loadProjectData(compare1, 1);
            loadProjectData(compare2, 2);
        }

    }

    private void setViews() {
        compareLayout = findViewById(R.id.compareLayout);

        leftName = findViewById(R.id.leftNameTextView);
        leftRegion = findViewById(R.id.leftRegionTextView);
        leftRoad = findViewById(R.id.leftRoadTextView);
        leftMRT = findViewById(R.id.leftMRTTextView);
        leftTime = findViewById(R.id.leftTimeTextView);
        leftPrice = findViewById(R.id.leftPriceTextView);
        leftFloor = findViewById(R.id.leftRangeTextView);
        leftTenure = findViewById(R.id.leftTenureTextView);
        leftTOP = findViewById(R.id.leftTOPTextView);

        rightName = findViewById(R.id.rightNameTextView);
        rightRegion = findViewById(R.id.rightRegionTextView);
        rightRoad = findViewById(R.id.rightRoadTextView);
        rightMRT = findViewById(R.id.rightMRTTextView);
        rightTime = findViewById(R.id.rightTimeTextView);
        rightPrice = findViewById(R.id.rightPriceTextView);
        rightFloor = findViewById(R.id.rightRangeTextView);
        rightTenure = findViewById(R.id.rightTenureTextView);
        rightTOP = findViewById(R.id.rightTOPTextView);
    }

    private void setNoneToCompare() {
        compareLayout.setVisibility(View.GONE);
    }

    private void setCompareOneOnly() {

    }

    private void loadProjectData(String pId, Integer side) {
        propertyDataService.getSingleProject(pId, new PropertyDataService.SingleProjectResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Property project) {
                if (side == 1){
                    project1 = project;
                    leftName.setText(project.getPropertyName());
                    leftRegion.setText(project.getRegion());
                    leftRoad.setText(project.getStreet());

                    loadMRTData(project, side);
                    loadTransactionsData(project, side);
                }
                else {
                    project2 = project;
                    rightName.setText(project.getPropertyName());
                    rightRegion.setText(project.getRegion());
                    rightRoad.setText(project.getStreet());

                    loadMRTData(project, side);
                    loadTransactionsData(project, side);
                }

            }
        });
    }

    private void loadMRTData(Property selectedProperty, Integer side) {
        mapDataService.distanceListLocations(selectedProperty, new MapDataService.DistanceListLocationsResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String nearestLocation, double nearestDistance) {
                if (nearestDistance == -1.00)
                {
                    if (side == 1){
                        leftMRT.setText("GPS Coordinates Unavailable");
                        leftTime.setText("GPS Coordinates Unavailable");
                    }
                    else {
                        rightMRT.setText("GPS Coordinates Unavailable");
                        rightTime.setText("GPS Coordinates Unavailable");
                    }
                }
                else
                {
                    DecimalFormat df = new DecimalFormat("0.00");
                    df.setRoundingMode(RoundingMode.UP);

                    DecimalFormat df2 = new DecimalFormat("0");
                    df.setRoundingMode(RoundingMode.UP);

                    double time = nearestDistance / 5 * 60;

                    if (side == 1){
                        leftMRT.setText(nearestLocation + " (" + df.format(nearestDistance) + " KM)");
                        leftTime.setText(df2.format(time) + " minutes walk");
                    }
                    else {
                        rightMRT.setText(nearestLocation + " (" + df.format(nearestDistance) + " KM)");
                        rightTime.setText(df2.format(time) + " minutes walk");
                    }
                }
            }
        });
    }

    private void loadTransactionsData(Property selectedProperty, Integer side){
        transactionDataService.callTransactionsById(selectedProperty.getProjectId(), new TransactionDataService.TransactionResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<Transaction> transactions) {
                Toast toast = Toast.makeText(ComparisonActivity.this, "reply", Toast.LENGTH_LONG);
                toast.show();
                List<Transaction> transactionList = transactions;
                String tenure;
                String district;
                String year;

                if (transactionList != null) {

                    Transaction ref = transactionList.get(1);

                    district = ref.getDistrict();

                    if (ref.getTenure().equals("Freehold")) {
                        tenure = ref.getTenure();
                        year = "0";
                    }
                    else {
                        int indexTenure = ref.getTenure().indexOf("y");
                        int indexYear = ref.getTenure().indexOf("2");
                        if (indexYear == -1){
                            indexYear = ref.getTenure().indexOf("1");
                        }
                        String temp = ref.getTenure();
                        tenure = (String)temp.substring(0, indexTenure - 1);
                        year =  (String) temp.substring(indexYear);
                    }

                    ArrayList<String> floorRange = new ArrayList<>();
                    ArrayList<String> floorArea = new ArrayList<>();
                    ArrayList<Double> prices = new ArrayList<>();
                    for (Transaction t: transactionList) {
                        if (!floorRange.contains(t.getFloorRange())){
                            floorRange.add(t.getFloorRange());
                        }
                        if (!floorArea.contains(String.valueOf(t.getFloorArea()))){
                            floorArea.add(String.valueOf(t.getFloorArea()));
                        }
                        prices.add(t.getPrice());
                    }
                    Collections.sort(floorArea);
                    Collections.sort(floorRange);

                    Double averagePrice = prices.stream().mapToDouble(d -> d).average().orElse(0.0);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    formatter.setRoundingMode(RoundingMode.UP);


                    String minFloor = floorArea.get(floorArea.size()-1);
                    String maxFloor = floorArea.get(0);
                    String floors = minFloor + " - " + maxFloor;

                    if (side == 1){
                        leftTenure.setText(tenure + " years");
                        leftTOP.setText(year);
                        leftFloor.setText(floors);
                        leftPrice.setText(formatter.format(averagePrice));
                    }
                    else {
                        rightTenure.setText(tenure + " years");
                        rightTOP.setText(year);
                        rightFloor.setText(floors);
                        rightPrice.setText(formatter.format(averagePrice));
                    }
                }
            }
        });
    }

}