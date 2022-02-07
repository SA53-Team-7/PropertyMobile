package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

//        ListView propertyListView = findViewById(R.id.propertyListView);
//        propertyList = Arrays.asList(
//                new Property("London", "test"),
//                new Property("Singapore", "test"),
//                new Property("Kuala Lumpur", "test"),
//                new Property("Paris", "test")
//        );

        ProgressBar progressBar = findViewById(R.id.propertyProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        propertyDataService.callProjects(new PropertyDataService.ProjectsResponseListener() {
            @Override
            public void onError(String message) {
                Toast toast = Toast.makeText(MainActivity.this, "something wrong", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onResponse(List<Property> projects) {
                progressBar.setVisibility(View.GONE);

                ListView propertyListView = findViewById(R.id.propertyListView);
                propertyList = projects;

                if (propertyListView != null)
                {
                    PropertyAdapter adapter = new PropertyAdapter(MainActivity.this, propertyList);
                    propertyListView.setAdapter(adapter);
                    propertyListView.setOnItemClickListener(MainActivity.this);
                }
            }
        });


    }


    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id)
    {

        Property selectedProperty = propertyList.get(position);
        Intent intent = new Intent(this, PropertyInfoActivity.class);
        intent.putExtra("Property", selectedProperty);

        startActivity(intent);


//        TextView textView = view.findViewById(R.id.propertyTextView);
//        String caption = selectedProperty.getPropertyName();

//        Toast toast = Toast.makeText(this, caption, Toast.LENGTH_SHORT);
//        toast.show();

//        propertyDataService.getCityID(caption, new PropertyDataService.VolleyResponseListener() {
//            @Override
//            public void onError(String message) {
//                Toast toast = Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//
//            @Override
//            public void onResponse(String cityID) {
//                Toast toast = Toast.makeText(MainActivity.this, "Returned an ID of " + cityID, Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        });


    }
}