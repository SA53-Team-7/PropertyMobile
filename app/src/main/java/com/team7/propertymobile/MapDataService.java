package com.team7.propertymobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MapDataService {
    Context context;

    public static final String STATIC_MAP1 = "https://developers.onemap.sg/commonapi/staticmap/getStaticImage?layerchosen=default&lat=";
    public static final String STATIC_MAP2 = "&zoom=17&height=512&width=512";



    public static final String STATIC_MAP_TEST = "https://developers.onemap.sg/commonapi/staticmap/getStaticImage?layerchosen=default&lat=1.2424409850962639&lng=103.83675517458369&zoom=17&height=512&width=512" +
            "&points=[1.2424409850962639,103.83675517458369,\"168,228,160\", \"A\"]";

    public MapDataService(Context context) {
        this.context = context;
    }


    public interface StaticMapResponseListener {
        void onError(String message);

        void onResponse(Bitmap bitmap);
    }

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

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface CoordinatesConverterResponseListener {
        void onError(String message);

        void onResponse(double latitude, double longitude);
    }

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

        DataRequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface CallStaticMapAfterConversionResponseListener {
        void onError(String message);

        void onResponse(Bitmap bitmap);
    }

    public void callStaticMapAfterConversion (String x, String y, CallStaticMapAfterConversionResponseListener callStaticMapAfterConversionResponseListener) {
        if (x.isEmpty() || y.isEmpty())
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





}
