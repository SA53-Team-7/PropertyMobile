package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    List<Property> propertyList;

    PropertyDataService propertyDataService = new PropertyDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView propertyListView = findViewById(R.id.propertyListView);
        propertyList = Arrays.asList(
                new Property("London"),
                new Property("Singapore"),
                new Property("Kuala Lumpur"),
                new Property("Paris")
        );

        if (propertyListView != null)
        {
            PropertyAdapter adapter = new PropertyAdapter(this, propertyList);
            propertyListView.setAdapter(adapter);
            propertyListView.setOnItemClickListener(this);
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id)
    {
        TextView textView = view.findViewById(R.id.propertyTextView);
        Property selectedProperty = propertyList.get(position);
        String caption = selectedProperty.getPropertyName();

//        Toast toast = Toast.makeText(this, caption, Toast.LENGTH_SHORT);
//        toast.show();

        propertyDataService.getCityID(caption, new PropertyDataService.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast toast = Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onResponse(String cityID) {
                Toast toast = Toast.makeText(MainActivity.this, "Returned an ID of " + cityID, Toast.LENGTH_SHORT);
                toast.show();
            }
        });




    }
}