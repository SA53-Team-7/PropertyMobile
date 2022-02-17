package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    LoginRegisterDataService loginRegisterDataService = new LoginRegisterDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

            if (TextUtils.isEmpty(email.getText().toString()) && TextUtils.isEmpty(password.getText().toString()) && TextUtils.isEmpty(name.getText().toString())){
                email.setError("Please enter your email address.");
                password.setError("Please enter your password.");
                name.setError("Please enter your name.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else if (TextUtils.isEmpty(email.getText().toString())){
                email.setError("Please enter your email address.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else if (!patternMatches(email.getText().toString(), "^(.+)@(\\S+)$")) {
                email.setError("Please enter a valid email address.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else if (TextUtils.isEmpty(password.getText().toString())){
                password.setError("Please enter your password.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else if (TextUtils.isEmpty(name.getText().toString())){
                name.setError("Please enter your name.");

                registerButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else {
                if (TextUtils.isEmpty(name.getText().toString())) {
                    try {
                        newUser.put("email", email.getText().toString());
                        newUser.put("password", password.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        newUser.put("email", email.getText().toString());
                        newUser.put("username", name.getText().toString());
                        newUser.put("password", password.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                loginRegisterDataService.register(newUser, new LoginRegisterDataService.AuthResponseListener() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(boolean success) {
                        progressBar.setVisibility(View.INVISIBLE);

                        if (success) {
                            registerSuccessTextView.setVisibility(View.VISIBLE);
                            finish();
                        }
                        else {
                            registerButton.setVisibility(View.VISIBLE);
                            Toast toast = Toast.makeText(RegisterActivity.this, "An account with this email already exists. Please use another email address.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });

            }

        }
    }


    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

}