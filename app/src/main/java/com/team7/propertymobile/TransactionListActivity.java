package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class TransactionListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    List<Transaction> transactionList;
    Property selectedProperty;

    TransactionDataService transactionDataService = new TransactionDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

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

                if (transactionListView != null) {
                    TransactionAdapter adapter = new TransactionAdapter(TransactionListActivity.this, transactionList);
                    transactionListView.setAdapter(adapter);
                    transactionListView.setOnItemClickListener(TransactionListActivity.this);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id) {

    }
}