package com.team7.propertymobile;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginRegisterDataService {
    public static final String LOGIN_CALL = "http://10.0.2.2:8080/api/mobile/auth/login";
    public static final String LIVE_LOGIN_CALL = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/auth/login";
    public static final String REGISTER_CALL = "http://10.0.2.2:8080/api/mobile/auth/register";
    public static final String LIVE_REGISTER_CALL = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/auth/register";
    Context context;

    public LoginRegisterDataService(Context context) {
        this.context = context;
    }

    // use REST API to authenticate user info
    public void login(JSONObject user, AuthResponseListener authResponseListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LIVE_LOGIN_CALL, user, response -> {
            try {
                int success = response.getInt("login");

                switch (success) {
                    case 0:
                        authResponseListener.onResponse(false, -1, null);
                        break;
                    case 1:
                        authResponseListener.onResponse(true, response.getInt("id"), response.getString("name"));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {

        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    // use REST API to update user info after registration
    public void register(JSONObject newUser, RegisterResponseListener registerResponseListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LIVE_REGISTER_CALL, newUser, response -> {
            try {
                int success = response.getInt("register");

                switch (success) {
                    case 0:
                        registerResponseListener.onResponse(false);
                        break;
                    case 1:
                        registerResponseListener.onResponse(true);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface AuthResponseListener {
        void onError(String message);

        void onResponse(boolean success, int id, String name);
    }

    public interface RegisterResponseListener {
        void onError(String message);

        void onResponse(boolean success);
    }
}
