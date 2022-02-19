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

public class RecommendDataService {

    Context context;
    public static final String QUERY_PROJECT_Recommendation = "http://10.0.2.2:8080/api/mobile/projects/recommend/";
    public static final String QUERY_PROJECT_RecommendationDistrict = "http://10.0.2.2:8080/api/mobile/transactions/top/";


    public RecommendDataService(Context context) {
        this.context = context;
    }

    public interface RecommendResponseListener {
        void onError(String message);

        void onResponse(List<Property> projects);
    }

    public interface RecommendDistrictResponseListener {
        void onError(String message);

        void onResponse(Transaction transactions);
    }

    public void callRecommendProjects(String district, RecommendResponseListener recommendResponseListener) {

        List<Property> projects = new ArrayList<>();
        String url = QUERY_PROJECT_Recommendation + district;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject jsonProperty = response.getJSONObject(i);
                        Property property = new Property();
                        property.setProjectId(jsonProperty.getInt("projectId"));
                        property.setPropertyName(jsonProperty.getString("name"));
                        property.setRegion(jsonProperty.getString("segment"));
                        property.setStreet(jsonProperty.getString("street"));
                        property.setxCoordinates(jsonProperty.getString("x"));
                        property.setyCoordinates(jsonProperty.getString("y"));

                        projects.add(property);
                    }
                    recommendResponseListener.onResponse(projects);

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

    public void recommendDistrictProject(int id, RecommendDistrictResponseListener recommendDistrictResponseListener)
    {
        String url = QUERY_PROJECT_RecommendationDistrict + id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    Transaction transaction = new Transaction();
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject jsonTransaction = response.getJSONObject(i);
                        transaction.setTransactionId(jsonTransaction.getInt("txnId"));
                        transaction.setContractDate(jsonTransaction.getString("contractDate"));
                        transaction.setFloorArea(jsonTransaction.getDouble("floorArea"));
                        transaction.setPrice(jsonTransaction.getDouble("price"));
                        transaction.setPropertyType(jsonTransaction.getString("propType"));
                        transaction.setAreaType(jsonTransaction.getString("areaType"));
                        transaction.setTenure(jsonTransaction.getString("tenure"));
                        transaction.setFloorRange(jsonTransaction.getString("floorRange"));
                        transaction.setDistrict(jsonTransaction.getString("district"));
                        transaction.setUnitsSold(jsonTransaction.getInt("noOfUnits"));

                    }

                    recommendDistrictResponseListener.onResponse(transaction);

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