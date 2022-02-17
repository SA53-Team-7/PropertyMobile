package com.team7.propertymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ComparisonActivity extends AppCompatActivity {

    LinearLayout compareLayout;

    Property project1;
    Property project2;

    TextView leftName;
    TextView leftRegion;
    TextView leftRoad;
    TextView leftMRT;
    TextView leftTime;
    TextView leftPrice;
    TextView leftFloor;
    TextView leftTenure;
    TextView leftTOP;

    TextView rightName;
    TextView rightRegion;
    TextView rightRoad;
    TextView rightMRT;
    TextView rightTime;
    TextView rightPrice;
    TextView rightFloor;
    TextView rightTenure;
    TextView rightTOP;

    PropertyDataService propertyDataService = new PropertyDataService(this);
    TransactionDataService transactionDataService = new TransactionDataService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        setViews();

        //set test shared preferences
        SharedPreferences setPref = getSharedPreferences("compare", MODE_PRIVATE);
        SharedPreferences.Editor editor = setPref.edit();
        editor.putString("compare1", "2");
        editor.putString("compare2", "32");
        editor.commit();


        SharedPreferences pref = getSharedPreferences("compare", MODE_PRIVATE);
        String compare1 = pref.getString("compare1", "-");
        String compare2 = pref.getString("compare2", "-");

        if (compare1.equals("-")) {
            setNoneToCompare();
        }
        else if (compare2.equals("-")){
            setCompareOneOnly();
        }
        else {
            propertyDataService.getSingleProject(compare1, new PropertyDataService.SingleProjectResponseListener() {
                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(Property project) {
                    leftName.setText(project.getPropertyName());
                    leftRegion.setText(project.getRegion());
                    leftRoad.setText(project.getStreet());
                }
            });
        }

    }

    private void setViews() {
        compareLayout = findViewById(R.id.compareLayout);
        leftName = findViewById(R.id.leftNameTextView);
        leftRegion = findViewById(R.id.leftRegionTextView);
        leftRoad = findViewById(R.id.leftRoadTextView);

        TextView leftRoad;
        TextView leftMRT;
        TextView leftTime;
        TextView leftPrice;
        TextView leftFloor;
        TextView leftTenure;
        TextView leftTOP;

        TextView rightName;
        TextView rightRegion;
        TextView rightRoad;
        TextView rightMRT;
        TextView rightTime;
        TextView rightPrice;
        TextView rightFloor;
        TextView rightTenure;
        TextView rightTOP;
    }

    private void setNoneToCompare() {
        //compareLayout.setVisibility(View.GONE);
    }

    private void setCompareOneOnly() {

    }
}