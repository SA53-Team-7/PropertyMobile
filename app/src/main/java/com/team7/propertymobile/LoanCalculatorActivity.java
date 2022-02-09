package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

public class LoanCalculatorActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_calculator);
        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(this);
    }

    @Override
    public void onClick (View view){
        int id = view.getId();

        if (id == R.id.calculateButton) {
            EditText priceInput = findViewById(R.id.propertyPriceInput);
            EditText depositInput = findViewById(R.id.depositInput);
            EditText loanLengthInput = findViewById(R.id.loanLengthInput);
            EditText interestRateInput = findViewById(R.id.interestRateInput);

            double price = Double.valueOf(priceInput.getText().toString());
            double deposit = Double.valueOf(depositInput.getText().toString());

            double amountToLoan = price - deposit;

            int numberOfPayments = Integer.valueOf(loanLengthInput.getText().toString()) * 12;
            double interestRate = Double.valueOf(interestRateInput.getText().toString()) / 1200;

            double monthlyPayment = (amountToLoan * interestRate * Math.pow((1+interestRate), numberOfPayments)) / (Math.pow((1+interestRate), numberOfPayments) - 1);

            TextView result = findViewById(R.id.monthlyPaymentView);
            Locale sg = new Locale("en", "SG");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(sg);
            result.setText(formatter.format(monthlyPayment) + " per month");
            result.setVisibility(View.VISIBLE);
        }
    }
}