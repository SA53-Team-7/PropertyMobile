package com.team7.propertymobile;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PredictionDataService {

    Context context;

    public static final String QUERY_FOR_PREDICTION_LOCAL = "http://10.0.2.2:5000/";
    public static final String QUERY_FOR_PREDICTION= "https://msdocs-python-webapp-quickstart-te7.azurewebsites.net/";

    public PredictionDataService(Context context) {
        this.context = context;
    }

    public interface PredictionResponseListener {
        void onError(String message);

        void onResponse(String response);
    }

    public void callPrediction (JSONArray data, PredictionDataService.PredictionResponseListener predictionResponseListener)
    {
        String url = QUERY_FOR_PREDICTION;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, data, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                String predictionResponse= response.toString();
                predictionResponseListener.onResponse(predictionResponse);

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
