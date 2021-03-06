package com.team7.propertymobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    LoginRegisterDataService loginRegisterDataService = new LoginRegisterDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // set the toolbar as the app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(myToolbar);

        // can click the icon (at the left of the activity title) to go back to previous page
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.registerButton) {
            Button registerButton = findViewById(R.id.registerButton);
            ProgressBar progressBar = findViewById(R.id.registerLoadProgressBar);
            registerButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            TextView registerSuccessTextView = findViewById(R.id.registerSuccessTextView);

            JSONObject newUser = new JSONObject();
            EditText email = findViewById(R.id.registerTextEmailAddress);
            EditText name = findViewById(R.id.nameTextPersonName);
            EditText password = findViewById(R.id.registerTextPassword);

            // display registration error message and save user info after registration
            if (TextUtils.isEmpty(email.getText().toString()) && TextUtils.isEmpty(password.getText().toString()) && TextUtils.isEmpty(name.getText().toString())) {
                email.setError("Please enter your email address.");
                password.setError("Please enter your password.");
                name.setError("Please enter your name.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (TextUtils.isEmpty(email.getText().toString())) {
                email.setError("Please enter your email address.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                email.setError("Please enter a valid email address.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (TextUtils.isEmpty(password.getText().toString())) {
                password.setError("Please enter your password.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (TextUtils.isEmpty(name.getText().toString())) {
                name.setError("Please enter your name.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                if (TextUtils.isEmpty(name.getText().toString())) {
                    try {
                        newUser.put("email", email.getText().toString());
                        newUser.put("password", password.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        newUser.put("email", email.getText().toString());
                        newUser.put("username", name.getText().toString());
                        newUser.put("password", password.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                loginRegisterDataService.register(newUser, new LoginRegisterDataService.RegisterResponseListener() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(boolean success) {
                        progressBar.setVisibility(View.INVISIBLE);

                        if (success) {
                            registerSuccessTextView.setVisibility(View.VISIBLE);
                            finish();
                        } else {
                            registerButton.setVisibility(View.VISIBLE);
                            Toast toast = Toast.makeText(RegisterActivity.this, "An account with this email already exists. Please use another email address.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });

            }

        }
    }
}