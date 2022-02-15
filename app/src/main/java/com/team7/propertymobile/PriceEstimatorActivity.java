package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

public class PriceEstimatorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    List<Transaction> transactionList;
    Property selectedProperty;

    String name;
    ArrayList<String> floorRange;
    ArrayList<String> floorArea;
    String district;
    String year;
    String tenure;

    Spinner areaSpinner;
    Spinner rangeSpinner;

    Button predictButton;

    TextView estimateView;

    TransactionDataService transactionDataService = new TransactionDataService(this);
    PredictionDataService predictionDataService = new PredictionDataService(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_estimator);

        Intent intent = getIntent();
        selectedProperty = (Property) intent.getSerializableExtra("Property");

        name = selectedProperty.getPropertyName();

        loadSpinnerData();
        Toast toast = Toast.makeText(this, "oncreate", Toast.LENGTH_SHORT);
        toast.show();

        predictButton = findViewById(R.id.buttonPredict);
        predictButton.setOnClickListener(this);

        estimateView = findViewById(R.id.textViewEstimate);
        estimateView.setVisibility(View.INVISIBLE);

    }

    private void loadSpinnerData() {
        transactionDataService.callTransactionsById(selectedProperty.getProjectId(), new TransactionDataService.TransactionResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<Transaction> transactions) {
                Toast toast = Toast.makeText(PriceEstimatorActivity.this, "reply", Toast.LENGTH_LONG);
                toast.show();
                transactionList = transactions;

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
                        String temp = ref.getTenure();
                        tenure = (String)temp.substring(0, indexTenure - 1);
                        year =  (String) temp.substring(indexYear);
                    }
                    floorRange = new ArrayList<>();
                    floorArea = new ArrayList<>();
                    for (Transaction t: transactionList) {
                        if (!floorRange.contains(t.getFloorRange())){
                            floorRange.add(t.getFloorRange());
                        }
                        if (!floorArea.contains(String.valueOf(t.getFloorArea()))){
                            floorArea.add(String.valueOf(t.getFloorArea()));
                        }
                    }
                    Collections.sort(floorArea);
                    Collections.sort(floorRange);
                }
                populateData();
            }
        });
    }

    private void populateData() {

        TextView projectName = findViewById(R.id.projectInfoTextView);
        projectName.setText(name);

        areaSpinner = findViewById(R.id.spinnerArea);
        rangeSpinner = findViewById(R.id.spinnerRange);

        ArrayAdapter<String> adapterRange = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, floorRange);
        ArrayAdapter<String> adapterArea = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, floorArea);

        adapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterRange.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        TextView districtView = findViewById(R.id.textViewDistrict);
        districtView.setText(district);

        TextView tenureView = findViewById(R.id.textViewTenure);
        tenureView.setText(tenure);

        TextView yearView = findViewById(R.id.textViewYear);
        yearView.setText(year);

        areaSpinner.setAdapter(adapterArea);
        rangeSpinner.setAdapter(adapterRange);
    }

    private JSONArray createRequestPackage() throws JSONException {

        JSONArray request = new JSONArray();

        JSONObject data = new JSONObject();

        LocalDate date = LocalDate.now();

        String currentDate = String.valueOf(date);
        String inputYear = currentDate.substring(2,4);

        String inputMonth;
        if (currentDate.substring(5,6).equals("0")){
            inputMonth = currentDate.substring(6,7);
        }
        else {
            inputMonth = currentDate.substring(5,7);
        }

        String floorRange = rangeSpinner.getSelectedItem().toString();
        String floorArea = areaSpinner.getSelectedItem().toString();

        data.put("projectId", String.valueOf(selectedProperty.getProjectId()));
        data.put("district", formatDistrict());
        data.put("floor_range", formatFloor(floorRange));
        data.put("floor_area", floorArea.substring(0,floorArea.lastIndexOf('.')));
        data.put("top", year);
        data.put("tenure", formatTenure());
        data.put("year", inputYear);
        data.put("month", inputMonth);

        request.put(data);
            String debugRequest = request.toString();
        return request;
    }

    private String formatFloor(String range) {
        if (range.equals("-")){
            return "1";
        }
        else {
            if (range.charAt(0) == '0'){
                String floor = range.substring(1,2);
                return floor;
            }
            else {
                String floor = range.substring(0,2);
                return floor;
            }
        }
    }

    private String formatDistrict() {
        if (district.charAt(0) == '0'){
            return district.substring(1);
        }
        return district;
    }

    private String formatTenure() {
        if (tenure.equals("Freehold")) {
            return "999999";
        }
        return tenure;
    }

    private String formatPrice(String predict) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        formatter.setMaximumFractionDigits(0);
        formatter.setCurrency(Currency.getInstance("SGD"));
        predict = predict.substring(2, predict.length() -2);
        Double price = Double.parseDouble(predict);
        formatter.format(price);
        String display = "$" + price;
        return display;

    }

    private void getPrediction() throws JSONException {

        JSONArray data = createRequestPackage();
        predictionDataService.callPrediction(data, new PredictionDataService.PredictionResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String response) {
                estimateView.setVisibility(View.VISIBLE);
                estimateView.setText("Estimated price: " + formatPrice(response));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonPredict){
            try {
                getPrediction();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}