package com.team7.propertymobile;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

public class PredictionDataService {

    Context context;

    // for local server
    public static final String QUERY_FOR_PREDICTION_LOCAL = "http://10.0.2.2:5000/";
    // for cloud server
    public static final String QUERY_FOR_PREDICTION = "https://msdocs-python-webapp-quickstart-te7.azurewebsites.net/";

    public PredictionDataService(Context context) {
        this.context = context;
    }

    public interface PredictionResponseListener {
        void onError(String message);

        void onResponse(String response);
    }

    // use REST API to get resale property model
    public void callPrediction(JSONArray data, PredictionDataService.PredictionResponseListener predictionResponseListener) {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, QUERY_FOR_PREDICTION, data, response -> {

            String predictionResponse = response.toString();
            predictionResponseListener.onResponse(predictionResponse);

        }, error -> {

        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

}
