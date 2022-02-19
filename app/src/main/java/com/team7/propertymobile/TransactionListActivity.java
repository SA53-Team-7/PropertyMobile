package com.team7.propertymobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

public class TransactionListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    List<Transaction> transactionList;
    Property selectedProperty;

    TransactionDataService transactionDataService = new TransactionDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.transactions_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        selectedProperty = (Property) intent.getSerializableExtra("Property");

        ProgressBar progressBar = findViewById(R.id.transactionProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        transactionDataService.callTransactionsById(selectedProperty.getProjectId(), new TransactionDataService.TransactionResponseListener() {


            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(List<Transaction> transactions) {
                progressBar.setVisibility(View.GONE);
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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id) {

    }
}