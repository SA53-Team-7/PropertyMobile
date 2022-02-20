package com.team7.propertymobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapDataService {
    Context context;

    public static final String STATIC_MAP1 = "https://developers.onemap.sg/commonapi/staticmap/getStaticImage?layerchosen=default&lat=";
    public static final String STATIC_MAP2 = "&zoom=17&height=512&width=512";

    public static final String QUERY_FOR_ALL_LOCATIONS = "http://10.0.2.2:8080/api/mobile/amenities";
    public static final String QUERY_FOR_ALL_TRAIN_STATIONS = "http://10.0.2.2:8080/api/mobile/amenities/trains";

    public static final String STATIC_MAP_TEST = "https://developers.onemap.sg/commonapi/staticmap/getStaticImage?layerchosen=default&lat=1.2424409850962639&lng=103.83675517458369&zoom=17&height=512&width=512" +
            "&points=[1.2424409850962639,103.83675517458369,\"168,228,160\", \"A\"]";


    public MapDataService(Context context) {
        this.context = context;
    }


    public interface StaticMapResponseListener {
        void onError(String message);

        void onResponse(Bitmap bitmap);
    }

    // use onemap API to get the property's map info
    public void callStaticMap (double latitude, double longitude, StaticMapResponseListener staticMapResponseListener)
    {

        String url = STATIC_MAP1 + latitude + "&lng=" + longitude + STATIC_MAP2
                + "&points=[" + latitude + "," + longitude + ",\"168,228,160\", \"A\"]";

        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                staticMapResponseListener.onResponse(response);
            }
        }, 512, 512, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, new Response.ErrorListener() {
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

    public interface CoordinatesConverterResponseListener {
        void onError(String message);

        void onResponse(double latitude, double longitude);
    }

    // get x and y coordinate of the specific location from onemap API
    public void coordinatesConverter (String x, String y, CoordinatesConverterResponseListener coordinatesConverterResponseListener)
    {
        String url = "https://developers.onemap.sg/commonapi/convert/3414to4326?X=" + x + "&Y=" + y;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    double latitude = response.getDouble("latitude");
                    double longitude = response.getDouble("longitude");

                    coordinatesConverterResponseListener.onResponse(latitude, longitude);
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

    public interface CallStaticMapAfterConversionResponseListener {
        void onError(String message);

        void onResponse(Bitmap bitmap);
    }

    // get the property's map using coordinate converter
    public void callStaticMapAfterConversion (String x, String y, CallStaticMapAfterConversionResponseListener callStaticMapAfterConversionResponseListener) {
        if (x.equals("null") || y.equals("null") || y.equals(null) || x.equals(null))
        {
            callStaticMapAfterConversionResponseListener.onResponse(null);
        }

        else
        {
            coordinatesConverter(x, y, new CoordinatesConverterResponseListener() {
                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(double latitude, double longitude) {
                    callStaticMap(latitude, longitude, new StaticMapResponseListener() {
                        @Override
                        public void onError(String message) {

                        }

                        @Override
                        public void onResponse(Bitmap bitmap) {
                            callStaticMapAfterConversionResponseListener.onResponse(bitmap);
                        }
                    });
                }
            });
        }

    }

    public interface DistanceOnEarthResponseListener {
        void onError(String message);

        void onResponse(double distance);
    }

    public void distanceOnEarth (Property property, Location location, DistanceOnEarthResponseListener distanceOnEarthResponseListener) {

        if (property.getxCoordinates().equals("null") || property.getyCoordinates().equals("null"))
        {
            double distanceBetween = -1.0;

            distanceOnEarthResponseListener.onResponse(distanceBetween);
        }
        else
        {
            double distanceBetween;
            coordinatesConverter(property.getxCoordinates(), property.getyCoordinates(), new CoordinatesConverterResponseListener() {
                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(double latitude, double longitude) {
                    // convert to radians

                    double propertyLatitude = Math.toRadians(latitude);
                    double propertyLongitude = Math.toRadians(longitude);
                    double locationLatitude = Math.toRadians(location.getLatitude());
                    double locationLongitude = Math.toRadians(location.getLongitude());

                    // Haversine formula
                    double dLat = locationLatitude - propertyLatitude;
                    double dLon = locationLongitude - propertyLongitude;

                    double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(propertyLatitude) * Math.cos(locationLatitude) * Math.pow(Math.sin(dLon / 2),2);
                    double c = 2 * Math.asin(Math.sqrt(a));

                    double r = 6371;

                    double distanceBetween = (c * r);

                    distanceOnEarthResponseListener.onResponse(distanceBetween);

                }
            });

        }
    }

    public interface DistanceLocationsMapResponseListener {
        void onError(String message);

        void onResponse(String name, double distance);
    }

    // use REST API to get x and y coordinate of the specific location then compute the distance between two places
    public void distanceLocationsMap (Property property, List<Location> locationList, DistanceLocationsMapResponseListener distanceLocationsMapResponseListener) {
        if (property.getxCoordinates().equals("null") || property.getyCoordinates().equals("null"))
        {
            double distanceBetween = -1.0;
            Map<String, Double> mrtHashMap = new HashMap<>();

            mrtHashMap.put("unavailable", distanceBetween);

            distanceLocationsMapResponseListener.onResponse("unavailable", distanceBetween);
        }
        else
        {
            double distanceBetween;
            coordinatesConverter(property.getxCoordinates(), property.getyCoordinates(), new CoordinatesConverterResponseListener() {
                Map<String, Double> mrtHashMap = new HashMap<>();

                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(double latitude, double longitude) {
                    // convert to radians

                    for (Location location : locationList) {
                        double propertyLatitude = Math.toRadians(latitude);
                        double propertyLongitude = Math.toRadians(longitude);
                        double locationLatitude = Math.toRadians(location.getLatitude());
                        double locationLongitude = Math.toRadians(location.getLongitude());

                        // Haversine formula
                        double dLat = locationLatitude - propertyLatitude;
                        double dLon = locationLongitude - propertyLongitude;

                        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(propertyLatitude) * Math.cos(locationLatitude) * Math.pow(Math.sin(dLon / 2),2);
                        double c = 2 * Math.asin(Math.sqrt(a));

                        double r = 6371;

                        double distanceBetween = (c * r);

                        mrtHashMap.put(location.getName(), distanceBetween);
                    }

                    String nearestLocation = "";
                    double nearestDistance = Collections.min(mrtHashMap.values());

                    for (Map.Entry<String, Double> entry : mrtHashMap.entrySet()) {
                        if (entry.getValue().equals(nearestDistance)) {
                            nearestLocation = entry.getKey();
                        }
                    }

                    distanceLocationsMapResponseListener.onResponse(nearestLocation, nearestDistance);
                }
            });

        }
    }

    public interface CallNearbyTrainStationsResponseListener {
        void onError(String message);

        void onResponse(Property property, List<Location> locationList);
    }

    public void callNearbyTrainStations (Property property, CallNearbyTrainStationsResponseListener callNearbyTrainStationsResponseListener) {
        List<Location> locationList = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, QUERY_FOR_ALL_TRAIN_STATIONS, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonLocation = response.getJSONObject(i);

                        Location location = new Location();
                        location.setName(jsonLocation.getString("name"));
                        location.setLatitude(Double.valueOf(jsonLocation.getString("latitude")));
                        location.setLongitude(Double.valueOf(jsonLocation.getString("longitude")));

                        locationList.add(location);
                    }

                    callNearbyTrainStationsResponseListener.onResponse(property, locationList);
                    JSONObject jsonObject = response.getJSONObject(0);
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

    public interface DistanceListLocationsResponseListener {
        void onError(String message);

        void onResponse(String name, double distance);
    }

    // use REST API to get the distance between
    public void distanceListLocations (Property property, DistanceListLocationsResponseListener distanceListLocationsResponseListener) {

        callNearbyTrainStations(property, new CallNearbyTrainStationsResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Property property, List<Location> locationList) {
                distanceLocationsMap(property, locationList, new DistanceLocationsMapResponseListener() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(String name, double distance) {
                        distanceListLocationsResponseListener.onResponse(name, distance);
                    }
                });
            }
        });
    }


}
