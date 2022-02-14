package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String USER_CREDENTIALS = "user_credentials";
    public static final String EMAIL_KEY = "hello";
    public static final String PASSWORD_KEY = "hello";
    public static final String TOKEN_KEY = "token_key";
    public static final String IS_LOGGEDIN = "is_loggedin";
    private String email;
    private String password;
    private String token;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        TextView registration = findViewById(R.id.toRegisterTextView);
        registration.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.loginButton) {
            EditText loginEmail = findViewById(R.id.loginTextEmailAddress);
            EditText loginPassword = findViewById(R.id.loginTextPassword);

            sharedPreferences = getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (TextUtils.isEmpty(loginEmail.getText().toString())){
                loginEmail.setError("Please enter your email address.");
            }
            if (TextUtils.isEmpty(loginPassword.getText().toString())){
                loginPassword.setError("Please enter your password.");
            }
            else {
                if (TextUtils.equals(loginEmail.getText().toString(), EMAIL_KEY) && TextUtils.equals(loginPassword.getText().toString(), PASSWORD_KEY)) {
                    editor.putString(TOKEN_KEY, "abcde12345");
                    editor.putBoolean(IS_LOGGEDIN, true);
                    editor.apply();

                    finish();
                }
                else {
                    Toast toast = Toast.makeText(this, "Wrong Emaill Address or Password. Please try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

        }

        if (id == R.id.toRegisterTextView) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}