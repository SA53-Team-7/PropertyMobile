package com.team7.propertymobile;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PropertyDataService{
    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    Context context;
    String cityID;

    public PropertyDataService(Context context) {
        this.context = context;
    }

    // async callback listener
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityID);
    }

    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener)
    {
        String url = QUERY_FOR_CITY_ID + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                cityID = "";

                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // works
//                Toast toast = Toast.makeText(context, "City ID = " + cityID, Toast.LENGTH_SHORT);
//                toast.show();
                volleyResponseListener.onResponse(cityID);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast toastError = Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT);
//                toastError.show();
                volleyResponseListener.onError("Error occurred");
            }
        });


        DataRequestSingleton.getInstance(context).addToRequestQueue(request);

//        return cityID;
    }
}
