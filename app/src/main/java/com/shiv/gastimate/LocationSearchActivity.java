package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.annotation.SuppressLint;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import static com.shiv.gastimate.Constants.FROM_LOCATION_REQUEST;
import static com.shiv.gastimate.Constants.TO_LOCATION_REQUEST;

public class LocationSearchActivity extends AppCompatActivity
{
    TextView locationSummary;
    EditText locationInput;
    FloatingActionButton floatingActionButton;

    String finalAddress;
    LatLng latLng;

    View view;
    int locationRequestType;

    boolean locationSet = false;

    /**
     * Called when activity is created
     * @param savedInstanceState, previous state if exists
     */
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
                    geocodeAPI();     //calls the search method
                    return true;
                }
                return false;
            }
        });

        //TODO: If location searched then onBackPressed, otherwise search
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(locationSet)
                {
                    onBackPressed();
                }
                else
                {
                    geocodeAPI();
                }
            }
        });
    }

    //Called by enter key in EditText or fab in search mode to search for location
    void geocodeAPI()
    {
        String address = locationInput.getText().toString();
        address = address.replace(" ", "+");
        String key = getResources().getString(R.string.maps_api_key);

        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", address, key);

        // Listener for response
        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray results = jsonResponse.getJSONArray("results");
                    finalAddress = results.getJSONObject(0).getString("formatted_address");
                    JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");

                    latLng = new LatLng(lat, lng);

                    finalAddress = finalAddress.replace(", ", "\n");

                    finalAddress += String.format("\n%f, %f", lat, lng);
                    locationSummary.setText(finalAddress);
                    locationSummary.setVisibility(View.VISIBLE);
                    locationSet = true;
                    setFABicon();
                }
                catch(Exception e)
                {
                    Log.e("Geocoding", "Parsing Error: " + e.getMessage());
                    Toast.makeText(getBaseContext(), "Place does not exist", Toast.LENGTH_SHORT).show();
                    locationSummary.setVisibility(View.INVISIBLE);
                    locationSet=false;
                    setFABicon();
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
                Toast.makeText(getBaseContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                locationSummary.setVisibility(View.INVISIBLE);
                locationSet = false;
                setFABicon();
            }
        };

        //StringRequest which uses the method, url, responseListener and the errorListener
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);

        //Queue of the requests that are to be sent
        RequestQueue requestQueue = Volley.newRequestQueue(LocationSearchActivity.this);

        //Adding the StringRequest to the queue so that it is sent
        requestQueue.add(stringRequest);
    }

    //Sets the intent with the location information to be sent back
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
        returnIntent.putExtra("latLng", latLng);
        setResult(0, returnIntent);
        finish();
    }

    void setFABicon()
    {
        if(locationSet)
        {
            floatingActionButton.setImageResource(R.drawable.ic_check);
        }
        else
        {
            floatingActionButton.setImageResource(R.drawable.ic_search);
        }
    }

    //Override because have to set intent first
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

    //Overrides the toolbar back button
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}