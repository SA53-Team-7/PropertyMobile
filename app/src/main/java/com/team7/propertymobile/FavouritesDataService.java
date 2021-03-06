package com.team7.propertymobile;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavouritesDataService {

    public static final String QUERY_FOR_MY_LIST = "http://10.0.2.2:8080/api/mobile/favourites/list/";
    public static final String LIVE_QUERY_FOR_MY_LIST = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/favourites/list/";
    public static final String IS_FAVE_SAVED = "http://10.0.2.2:8080/api/mobile/favourites/check";
    public static final String LIVE_IS_FAVE_SAVED = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/favourites/check";
    public static final String SAVE_OR_UPDATE = "http://10.0.2.2:8080/api/mobile/favourites/save";
    public static final String LIVE_SAVE_OR_UPDATE = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/favourites/save";
    Context context;

    public FavouritesDataService(Context context) {
        this.context = context;
    }

    // use REST API to get shortlist info
    public void callAllProjects(int userId, CallMyListResponseListener callMyListResponseListener) {
        List<Property> projects = new ArrayList<>();

        String url = LIVE_QUERY_FOR_MY_LIST + userId;

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
                callMyListResponseListener.onResponse(projects);

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

    // use REST API to check the shortlist info
    public void isSaved(JSONObject userAndProject, SaveResponseListener saveResponseListener) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LIVE_IS_FAVE_SAVED, userAndProject, response -> {
            try {
                int isSaved = response.getInt("isSaved");

                switch (isSaved) {
                    case -1:
                        saveResponseListener.onResponse(false);
                        break;
                    case 1:
                        saveResponseListener.onResponse(true);
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

    // use REST API to update the shortlist info
    public void save(JSONObject userAndProject, SaveResponseListener saveResponseListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LIVE_SAVE_OR_UPDATE, userAndProject, response -> {
            try {
                int isSaved = response.getInt("success");

                switch (isSaved) {
                    case -1:
                        saveResponseListener.onResponse(false);
                        break;
                    case 1:
                        saveResponseListener.onResponse(true);
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

    public interface CallMyListResponseListener {
        void onError(String message);

        void onResponse(List<Property> projects);
    }


    public interface SaveResponseListener {
        void onError(String message);

        void onResponse(boolean isSaved);
    }
}
