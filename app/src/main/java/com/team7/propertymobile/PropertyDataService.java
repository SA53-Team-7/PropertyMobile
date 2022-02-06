package com.team7.propertymobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PropertyDataService{
//    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
//    String cityID;

    Context context;
    public static final String QUERY_FOR_PROJECTS = "http://10.0.2.2:8080/api/mobile/projects";

    public static final String CONVERT_COORDINATES_TO_LAT_LONG = "https://developers.onemap.sg/commonapi/convert/3414to4326?";
    public static final String STATIC_MAP_CREATE_FRONT = "https://developers.onemap.sg/commonapi/staticmap/getStaticImage?layerchosen=default&lat=";
    public static final String STATIC_MAP_CREATE_BEFORE_POINT = "&zoom=17&height=512&width=512&points=[";
    public static final String STATIC_MAP_CREATE_BACK = ",\"168,228,160\", \"A\"]";

    public static final String STATIC_MAP_TEST = "https://developers.onemap.sg/commonapi/staticmap/getStaticImage?layerchosen=default&lat=1.2424409850962639&lng=103.83675517458369&zoom=17&height=512&width=512&points=[1.2424409850962639,103.83675517458369,\"168,228,160\", \"A\"]";

    public PropertyDataService(Context context) {
        this.context = context;
    }

//    // async callback listener
//    public interface VolleyResponseListener {
//        void onError(String message);
//
//        void onResponse(String cityID);
//    }
//
//    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener)
//    {
//        String url = QUERY_FOR_CITY_ID + cityName;
//
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                cityID = "";
//
//                try {
//                    JSONObject cityInfo = response.getJSONObject(0);
//                    cityID = cityInfo.getString("woeid");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                // works
////                Toast toast = Toast.makeText(context, "City ID = " + cityID, Toast.LENGTH_SHORT);
////                toast.show();
//                volleyResponseListener.onResponse(cityID);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                Toast toastError = Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT);
////                toastError.show();
//                volleyResponseListener.onError("Error occurred");
//            }
//        });
//
//
//        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
//
////        return cityID;
//    }

    public interface ProjectsResponseListener {
        void onError(String message);

        void onResponse(List<Property> projects);
    }

    public void callProjects(ProjectsResponseListener projectsResponseListener)
    {
        List<Property> projects = new ArrayList<>();



        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, QUERY_FOR_PROJECTS, null, new Response.Listener<JSONArray>() {
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

                    projectsResponseListener.onResponse(projects);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface CoordinatesConvertResponseListener {
        void onError(String message);

        void onResponse(double latitude, double longitude);
    }

    public void coordinatesConvert(String x, String y, CoordinatesConvertResponseListener coordinatesConvertResponseListener)
    {
        String url = CONVERT_COORDINATES_TO_LAT_LONG + "X=" + x + "&Y=" + y;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    double latitude = response.getDouble("latitude");
                    double longitude = response.getDouble("longitude");

                    coordinatesConvertResponseListener.onResponse(latitude, longitude);
                }


                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface StaticMapResponseListener {
        void onError(String message);

        void onResponse(Bitmap bitmap);
    }

    public void createStaticMap(double latitude, double longitude, StaticMapResponseListener staticMapResponseListener) {

//        String url = "https://developers.onemap.sg/commonapi/staticmap/getStaticImage?layerchosen=default&lat=" + latitude + "&lng=" + longitude + "&zoom=17&height=512&width=512";

        String url = "https://developers.onemap.sg/commonapi/staticmap/getStaticImage?layerchosen=default&lat=1.2424409850962639&lng=103.83675517458369&zoom=17&height=512&width=512";

        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                staticMapResponseListener.onResponse(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

//    public interface ShowMapResponseListener {
//        void onError(String message);
//
//        void onResponse(Bitmap bitmap);
//    }
//
//    public void showMap(String x, String y, final ShowMapResponseListener showMapResponseListener)
//    {
//        coordinatesConvert(x, y, new CoordinatesConvertResponseListener() {
//            @Override
//            public void onError(String message) {
//
//            }
//
//            @Override
//            public void onResponse(double latitude, double longitude) {
//                createStaticMap(latitude, longitude, new StaticMapResponseListener() {
//                    @Override
//                    public void onError(String message) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Bitmap bitmap) {
//                        showMapResponseListener.onResponse(bitmap);
//                    }
//                });
//            }
//        });
//    }

}
