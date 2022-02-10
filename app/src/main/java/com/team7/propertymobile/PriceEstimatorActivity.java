package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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

        transactionDataService.callTransactionsById(selectedProperty.getProjectId(), new TransactionDataService.TransactionResponseListener() {


            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<Transaction> transactions) {
                ListView transactionListView = findViewById(R.id.transactionListView);
                transactionList = transactions;

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
                    year =  (String) temp.substring(indexYear, temp.length() -1);
                }
                if (transactionListView != null) {
                    for (Transaction t: transactionList) {
                        if (!floorRange.contains(t.getFloorRange())){
                            floorRange.add(t.getFloorRange());
                        }
                        if (!floorArea.contains(t.getFloorArea())){
                            floorArea.add(String.valueOf(t.getFloorArea()));
                        }
                    }
                }
                Spinner area = findViewById(R.id.spinnerArea);
                Spinner range = findViewById(R.id.spinnerRange);

                ArrayAdapter adapterRange = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, floorRange);
                ArrayAdapter adapterArea = new ArrayAdapter<String>(PriceEstimatorActivity, android.R.layout.simple_spinner_item, floorArea);

                adapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapterRange.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                area.setAdapter(adapterArea);
                range.setAdapter(adapterRange);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}