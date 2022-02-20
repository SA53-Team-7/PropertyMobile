package com.team7.propertymobile;

import android.content.Context;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionDataService {

    Context context;

    public static final String QUERY_FOR_TRANSACTIONS_BY_ID = "http://10.0.2.2:8080/api/mobile/transactions/";
    public static final String LIVE_QUERY_FOR_TRANSACTIONS_BY_ID = "https://propertypredict-propertypredictweb.azuremicroservices.io/api/mobile/transactions/";

    public TransactionDataService(Context context) {
        this.context = context;
    }

    public interface TransactionResponseListener {
        void onError(String message);

        void onResponse(List<Transaction> transactions);
    }

    // use REST API to get transaction info by project(property) id
    public void callTransactionsById (int id, TransactionResponseListener transactionResponseListener)
    {
        List<Transaction> transactionList = new ArrayList<>();

        String url = LIVE_QUERY_FOR_TRANSACTIONS_BY_ID + id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject jsonTransaction = response.getJSONObject(i);
                        Transaction transaction = new Transaction();
                        transaction.setTransactionId(jsonTransaction.getInt("txnId"));
                        transaction.setContractDate(jsonTransaction.getString("contractDate"));
                        transaction.setFloorArea(jsonTransaction.getDouble("floorArea"));
                        transaction.setPrice(jsonTransaction.getDouble("price"));
                        transaction.setPropertyType(jsonTransaction.getString("propType"));
                        transaction.setAreaType(jsonTransaction.getString("areaType"));
                        transaction.setTenure(jsonTransaction.getString("tenure"));
                        transaction.setFloorRange(jsonTransaction.getString("floorRange"));
                        transaction.setDistrict(jsonTransaction.getString("district"));
                        transaction.setUnitsSold(jsonTransaction.getInt("noOfUnits"));

                        transactionList.add(transaction);
                    }

                    transactionResponseListener.onResponse(transactionList);

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


}
