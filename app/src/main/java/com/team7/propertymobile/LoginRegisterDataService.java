package com.team7.propertymobile;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginRegisterDataService {
    Context context;

    public static final String LOGIN_CALL = "http://10.0.2.2:8080/api/mobile/auth/login";
    public static final String REGISTER_CALL = "http://10.0.2.2:8080/api/mobile/auth/register";
    public static final String NAME_CALL = "http://10.0.2.2:8080/api/mobile/auth/name";

    public LoginRegisterDataService(Context context) {
        this.context = context;
    }


    public interface AuthResponseListener {
        void onError(String message);

        void onResponse(boolean success, int id);
    }

    public void login (JSONObject user, AuthResponseListener authResponseListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_CALL, user, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int success = response.getInt("login");

                    switch (success) {
                        case 0:
                            authResponseListener.onResponse(false, -1);
                            break;
                        case 1:
                            authResponseListener.onResponse(true, response.getInt("id"));
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface RegisterResponseListener {
        void onError(String message);

        void onResponse(boolean success);
    }

    public void register (JSONObject newUser, RegisterResponseListener registerResponseListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REGISTER_CALL, newUser, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface GetNameResponseListener {
        void onError(String message);

        void onResponse(String name);
    }

    public void getName (JSONObject user, GetNameResponseListener getNameResponseListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, NAME_CALL, user, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String name = response.getString("name");
                    getNameResponseListener.onResponse(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }


}
