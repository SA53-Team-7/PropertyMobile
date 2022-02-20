package com.team7.propertymobile;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendDataService {

    public static final String QUERY_PROJECT_Recommendation = "http://10.0.2.2:8080/api/mobile/projects/recommend/";
    public static final String LIVE_QUERY_PROJECT_Recommendation = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/projects/recommend/";
    public static final String QUERY_PROJECT_RecommendationDistrict = "http://10.0.2.2:8080/api/mobile/transactions/top/";
    public static final String LIVE_QUERY_PROJECT_RecommendationDistrict = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/transactions/top/";
    Context context;


    public RecommendDataService(Context context) {
        this.context = context;
    }

    // use REST API to get recommended properties based on same district
    public void callRecommendProjects(String district, RecommendResponseListener recommendResponseListener) {

        List<Property> projects = new ArrayList<>();
        String url = LIVE_QUERY_PROJECT_Recommendation + district;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
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
        }, error -> {

        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    // use REST API to get district info for recommendation
    public void recommendDistrictProject(int id, RecommendDistrictResponseListener recommendDistrictResponseListener) {
        String url = LIVE_QUERY_PROJECT_RecommendationDistrict + id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {

            try {
                Transaction transaction = new Transaction();
                for (int i = 0; i < response.length(); i++) {
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

        }, error -> {

        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface RecommendResponseListener {
        void onError(String message);

        void onResponse(List<Property> projects);
    }

    public interface RecommendDistrictResponseListener {
        void onError(String message);

        void onResponse(Transaction transactions);
    }


}
