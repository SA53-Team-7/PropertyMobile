package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PropertyInfoActivity extends AppCompatActivity implements View.OnClickListener{

    PropertyDataService propertyDataService = new PropertyDataService(this);

    Bitmap bitmap;
    Property selectedProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_info);

        Intent intent = getIntent();
        selectedProperty = (Property) intent.getSerializableExtra("Property");

        TextView projectInfoTextView = findViewById(R.id.projectInfoTextView);
        projectInfoTextView.setText(selectedProperty.getPropertyName());

        TextView regionInfoTextView = findViewById(R.id.regionInfoTextView);
        String region = segmentToRegion(selectedProperty.getRegion());
        regionInfoTextView.setText(region);

        TextView streetInfoTextView = findViewById(R.id.streetInfoTextView);
        streetInfoTextView.setText(selectedProperty.getStreet());

        ImageView imageView = findViewById(R.id.staticMapImageView);
        propertyDataService.createStaticMap(1.2424409850962639, 103.83675517458369, new PropertyDataService.StaticMapResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Bitmap response) {
                if (bitmap == null)
                {
                    imageView.setImageResource(R.drawable.no_map);
                }
                else
                {
                    imageView.setImageBitmap(response);
                }
            }
        });



        Button button = findViewById(R.id.transactionButton);
        button.setOnClickListener(this);


//        propertyDataService.showMap(selectedProperty.getxCoordinates(), selectedProperty.getyCoordinates(), new PropertyDataService.ShowMapResponseListener() {
//            @Override
//            public void onError(String message) {
//
//            }
//
//            @Override
//            public void onResponse(Bitmap bitmap) {
//                imageView.setImageBitmap(bitmap);
//            }
//        });
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if(id == R.id.transactionButton)
        {
            Intent intent = new Intent(this, TransactionListActivity.class);
            intent.putExtra("Property", selectedProperty);

            startActivity(intent);
        }
    }


    String segmentToRegion (String segment){
        String region = "";

        switch (segment) {
            case "CCR":
                region = "Core Central Region (CCR)";
                break;
            case "RCR":
                region = "Rest of Central Region (RCR)";
                break;
            case "OCR":
                region = "Outside Central Region (OCR)";
                break;
        }

        return region;
    }
}