package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriceEstimatorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<Transaction> transactionList;
    Property selectedProperty;

    ArrayList<String> floorRange;
    ArrayList<String> floorArea;
    String district;
    String year;
    String tenure;

    TransactionDataService transactionDataService = new TransactionDataService(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_estimator);

        Intent intent = getIntent();
        selectedProperty = (Property) intent.getSerializableExtra("Property");

        loadSpinnerData();
        Toast toast = Toast.makeText(this, "oncreate", Toast.LENGTH_SHORT);
        toast.show();

    }

    private void populateSpinner() {
        Spinner area = findViewById(R.id.spinnerArea);
        Spinner range = findViewById(R.id.spinnerRange);

        ArrayAdapter adapterRange = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, floorRange);
        ArrayAdapter adapterArea = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, floorArea);

        adapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterRange.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        TextView districtView = findViewById(R.id.textViewDistrict);
        districtView.setText(district);

        TextView tenureView = findViewById(R.id.textViewTenure);
        tenureView.setText(tenure);

        TextView yearView = findViewById(R.id.textViewYear);
        yearView.setText(year);

        area.setAdapter(adapterArea);
        range.setAdapter(adapterRange);
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

                    if (ref.getTenure() == "Freehold") {
                        tenure = ref.getTenure();
                        year = "0";
                    }
                    else {
                        int indexTenure = ref.getTenure().indexOf("y");
                        int indexYear = ref.getTenure().indexOf("2");
                        String temp = ref.getTenure();
                        tenure = (String)temp.substring(0, indexTenure - 1);
                        year =  (String) temp.substring(indexYear, temp.length());
                    }
                    floorRange = new ArrayList<String>();
                    floorArea = new ArrayList<String>();
                    for (Transaction t: transactionList) {
                        if (!floorRange.contains(t.getFloorRange())){
                            floorRange.add(t.getFloorRange());
                        }
                        if (!floorArea.contains(t.getFloorArea())){
                            floorArea.add(String.valueOf(t.getFloorArea()));
                        }
                    }
                }
                populateSpinner();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}