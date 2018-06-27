package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import static com.shiv.gastimate.Constants.FROM_LOCATION_REQUEST;
import static com.shiv.gastimate.Constants.GEOCODE_API;
import static com.shiv.gastimate.Constants.TO_LOCATION_REQUEST;

public class LocationSearchActivity extends AppCompatActivity
{
    TextView locationSummary;
    EditText locationInput;
    String finalAddress;
    View view;
    int locationRequestType;

    boolean locationSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_search_activity);

        locationInput = findViewById(R.id.locationInput);
        locationSummary = findViewById(R.id.locationSummary);

        locationRequestType = getIntent().getIntExtra("locationRequestType", FROM_LOCATION_REQUEST);

        if(locationRequestType == FROM_LOCATION_REQUEST)
        {
            locationInput.setHint("Where from?");
        }
        else if(locationRequestType == TO_LOCATION_REQUEST)
        {
            locationInput.setHint("Where to?");
        }
        locationInput.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                // If the event is a key-down event on the "Enter" key
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    try     //hides the keyboard on enter press
                    {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }catch(Exception e)
                    {
                        Log.e("Enter Press", Arrays.toString(e.getStackTrace()));
                    }
                    view = v;
                    onSearch();     //calls the search method
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }

    void onSearch()
    {
        String address = locationInput.getText().toString();
        address = address.replace(" ", "+");

        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", address, GEOCODE_API);

        // Listener for response
        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray results = jsonResponse.getJSONArray("results");
                    finalAddress = results.getJSONObject(0).getString("formatted_address");
                    finalAddress = finalAddress.replace(", ", "\n");
                    locationSummary.setText(finalAddress);
                    locationSummary.setVisibility(View.VISIBLE);
                    locationSet = true;
                }
                catch(Exception e)
                {
                    Log.e("Geocoding", "Parsing Error");
                    Toast.makeText(getBaseContext(), "Place does not exist", Toast.LENGTH_SHORT).show();
                    locationSummary.setVisibility(View.INVISIBLE);
                }
            }
        };

        // Listener for error
        Response.ErrorListener errorListener= new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError e)
            {
                Log.e("Geocoding", "Connection Error");
                Toast.makeText(getBaseContext(), "Geocoding Connection Error", Toast.LENGTH_SHORT).show();
                locationSummary.setVisibility(View.INVISIBLE);
            }
        };

        //StringRequest which uses the method, url, responseListener and the errorListener
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);

        //Queue of the requests that are to be sent
        RequestQueue requestQueue = Volley.newRequestQueue(LocationSearchActivity.this);

        //Adding the StringRequest to the queue so that it is sent
        requestQueue.add(stringRequest);
    }

    void setLocation()
    {
        String line;
        try
        {
            line = finalAddress.substring(0, finalAddress.indexOf('\n'));
        }
        catch(Exception e)
        {
            line = finalAddress;
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("locationLine", line);
        returnIntent.putExtra("locationRequestType", locationRequestType);
        setResult(0, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        if(locationSet)
        {
            setLocation();
        }
        else
        {
            super.onBackPressed();
        }
    }
}