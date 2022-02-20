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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewPropertyDataService {
    Context context;

    public static final String QUERY_FOR_NEW_PROJECTS = "http://10.0.2.2:8080/api/mobile/newprojects";
    public static final String LIVE_QUERY_FOR_NEW_PROJECTS = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/newprojects";

    public NewPropertyDataService(Context context) {
        this.context = context;
    }

    public interface NewProjectsResponseListener {
        void onError(String message);

        void onResponse(List<NewProperty> projects);
    }

    // use REST API to get new launch property info
    public void callAllNewProjects(NewProjectsResponseListener newProjectsResponseListener) {
        List<NewProperty> newProjectsList = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, LIVE_QUERY_FOR_NEW_PROJECTS, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonProperty = response.getJSONObject(i);
                        NewProperty newProperty = new NewProperty();

                        newProperty.setNewProjectId(jsonProperty.getInt("id"));
                        newProperty.setPropertyName(jsonProperty.getString("projectName"));

                        newProperty.setDate(jsonProperty.getString("landTxnDate"));

                        newProperty.setLandPrice(jsonProperty.getDouble("landTxnPrice"));
                        newProperty.setPredictedPrice(jsonProperty.getDouble("predictPrice"));

                        newProjectsList.add(newProperty);
                    }

                    newProjectsResponseListener.onResponse(newProjectsList);

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
