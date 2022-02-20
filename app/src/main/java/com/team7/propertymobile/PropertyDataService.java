package com.team7.propertymobile;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PropertyDataService {

    Context context;
    public static final String QUERY_PROJECT_SEARCH = "http://10.0.2.2:8080/api/mobile/projects/search/";
    public static final String LIVE_QUERY_PROJECT_SEARCH = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/projects/search/";
    public static final String QUERY_PROJECT_GET = "http://10.0.2.2:8080/api/mobile/projects/get/";
    public static final String LIVE_QUERY_PROJECT_GET = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/projects/get/";

    public PropertyDataService(Context context) {
        this.context = context;
    }


    public interface ProjectsResponseListener {
        void onError(String message);

        void onResponse(List<Property> projects);
    }

    public interface SingleProjectResponseListener {
        void onError(String message);

        void onResponse(Property projects);
    }

    // use REST API to get property info using keyword input
    public void searchProjects(String search, ProjectsResponseListener projectsResponseListener) {
        List<Property> projects = new ArrayList<>();
        String url = LIVE_QUERY_PROJECT_SEARCH + search;


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

                projectsResponseListener.onResponse(projects);

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

    // use REST API to get single property info
    public void getSingleProject(String id, SingleProjectResponseListener singleProjectResponseListener) {
        String url = LIVE_QUERY_PROJECT_GET + id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {

            try {
                Property property = new Property();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonProperty = response.getJSONObject(i);

                    property.setProjectId(jsonProperty.getInt("projectId"));
                    property.setPropertyName(jsonProperty.getString("name"));
                    property.setRegion(jsonProperty.getString("segment"));
                    property.setStreet(jsonProperty.getString("street"));
                    property.setxCoordinates(jsonProperty.getString("x"));
                    property.setyCoordinates(jsonProperty.getString("y"));

                }

                singleProjectResponseListener.onResponse(property);

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

}
