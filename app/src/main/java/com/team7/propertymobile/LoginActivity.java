package com.team7.propertymobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String USER_CREDENTIALS = "user_credentials";
    public static final String USER_KEY = "user_key";
    public static final String NAME_KEY = "name_key";
    public static final String ID_KEY = "id_key";
    public static final String IS_LOGIN = "is_login";

    LoginRegisterDataService loginRegisterDataService = new LoginRegisterDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set the toolbar as the app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(myToolbar);

        // can click the icon (at the left of the activity title) to go back to previous page
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        TextView registration = findViewById(R.id.toRegisterTextView);
        registration.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.loginButton) {
            ProgressBar progressBar = findViewById(R.id.loginLoadProgressBar);
            Button loginButton = findViewById(R.id.loginButton);
            TextView loginSuccessTextView = findViewById(R.id.loginSuccessTextView);
            loginButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            EditText loginEmail = findViewById(R.id.loginTextEmailAddress);
            EditText loginPassword = findViewById(R.id.loginTextPassword);


            SharedPreferences sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // display login error message and authenticate user info
            if (TextUtils.isEmpty(loginEmail.getText().toString()) && TextUtils.isEmpty(loginPassword.getText().toString())) {
                loginEmail.setError("Please enter your email address.");
                loginPassword.setError("Please enter your password.");

                loginButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (TextUtils.isEmpty(loginEmail.getText().toString())) {
                loginEmail.setError("Please enter your email address.");

                loginButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (TextUtils.isEmpty(loginPassword.getText().toString())) {
                loginPassword.setError("Please enter your password.");

                loginButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                JSONObject user = new JSONObject();
                try {
                    user.put("email", loginEmail.getText().toString());
                    user.put("password", loginPassword.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loginRegisterDataService.login(user, new LoginRegisterDataService.AuthResponseListener() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(boolean success, int id, String name) {
                        progressBar.setVisibility(View.INVISIBLE);

                        if (success) {
                            editor.putString(USER_KEY, loginEmail.getText().toString());
                            editor.putInt(ID_KEY, id);
                            editor.putString(NAME_KEY, name);
                            editor.putBoolean(IS_LOGIN, true);
                            editor.apply();
                            loginSuccessTextView.setVisibility(View.VISIBLE);

                            finish();
                        } else {
                            loginButton.setVisibility(View.VISIBLE);
                            Toast toast = Toast.makeText(LoginActivity.this, "Wrong Email Address or Password. Please try again", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
            }
        }

        if (id == R.id.toRegisterTextView) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}