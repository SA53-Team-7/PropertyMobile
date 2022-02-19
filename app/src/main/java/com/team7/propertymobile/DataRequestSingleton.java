package com.team7.propertymobile;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class DataRequestSingleton {
    private static DataRequestSingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private DataRequestSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized DataRequestSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new DataRequestSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


}
