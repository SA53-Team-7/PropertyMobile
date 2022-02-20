package com.team7.propertymobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.NumberFormat;
import java.util.Locale;

public class LoanCalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_calculator);

        // set the toolbar as the app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.homeloan_toolbar);
        setSupportActionBar(myToolbar);

        // can click the icon (at the left of the activity title) to go back to previous page
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.calculateButton) {
            EditText priceInput = findViewById(R.id.propertyPriceInput);
            EditText depositInput = findViewById(R.id.depositInput);
            EditText loanLengthInput = findViewById(R.id.loanLengthInput);
            EditText interestRateInput = findViewById(R.id.interestRateInput);

            // calculate home loan
            double price = Double.parseDouble(priceInput.getText().toString());
            double deposit = Double.parseDouble(depositInput.getText().toString());

            double amountToLoan = price - deposit;

            int numberOfPayments = Integer.parseInt(loanLengthInput.getText().toString()) * 12;
            double interestRate = Double.parseDouble(interestRateInput.getText().toString()) / 1200;

            double monthlyPayment = (amountToLoan * interestRate * Math.pow((1 + interestRate), numberOfPayments)) / (Math.pow((1 + interestRate), numberOfPayments) - 1);

            TextView result = findViewById(R.id.monthlyPaymentView);
            Locale sg = new Locale("en", "SG");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(sg);
            result.setText(String.format("%s per month", formatter.format(monthlyPayment)));
            result.setVisibility(View.VISIBLE);
        }
    }
}