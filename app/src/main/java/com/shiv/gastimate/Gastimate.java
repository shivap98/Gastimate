package com.shiv.gastimate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static com.shiv.gastimate.LocationActivity.currentFromLatLng;
import static com.shiv.gastimate.LocationActivity.currentToLatLng;

public class Gastimate extends AppCompatActivity
{
    TextView gasValue;
    TextView moneyValue;
    TextView distanceValue;
    TextView timeValue;
    TextView timeUnit;

    double distance;        //in miles
    double time;            //in minutes
    double gas;
    double money;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gastimate_activity);

        gasValue = findViewById(R.id.gasValue);
        moneyValue = findViewById(R.id.moneyValue);
        distanceValue = findViewById(R.id.distanceValue);
        timeValue = findViewById(R.id.timeValue);
        timeUnit = findViewById(R.id.unit4);

        distanceMatrixAPI();

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Gastimate.this, MainActivity.class);
                startActivity(intent);
                finishAndRemoveTask();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    void distanceMatrixAPI()
    {
        String url = String.format("http://maps.google.com/maps/api/distancematrix/json?units=imperial&origins=%f,%f&destinations=%f,%f",
                currentFromLatLng.latitude, currentFromLatLng.longitude, currentToLatLng.latitude, currentToLatLng.longitude);

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
                    JSONArray rows = jsonResponse.getJSONArray("rows");
                    JSONArray elements = rows.getJSONObject(0).getJSONArray("elements");

                    JSONObject distanceObject = elements.getJSONObject(0).getJSONObject("distance");
                    distance = (distanceObject.getDouble("value"))/1609.344;    //converting metres to miles

                    JSONObject durationObject = elements.getJSONObject(0).getJSONObject("duration");
                    time = (durationObject.getDouble("value"))/60;    //converting seconds to minutes

                    setValues();
                }
                catch(Exception e)
                {
                    Log.e("Distance Matrix", "Parsing Error: " + e.getMessage());
                }
            }
        };

        // Listener for error
        Response.ErrorListener errorListener= new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError e)
            {
                Log.e("Distance Matrix", "Connection Error");
                Toast.makeText(getBaseContext(), "Connection", Toast.LENGTH_SHORT).show();

            }
        };

        //StringRequest which uses the method, url, responseListener and the errorListener
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);

        //Queue of the requests that are to be sent
        RequestQueue requestQueue = Volley.newRequestQueue(Gastimate.this);

        //Adding the StringRequest to the queue so that it is sent
        requestQueue.add(stringRequest);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    void setValues()
    {
        gas = distance/MainActivity.currentVehicle.mpg;
        gasValue.setText(String.format("%.2f", gas));

        money = gas * FuelActivity.currentSetPrice;
        moneyValue.setText(String.format("%.2f", money));

        distanceValue.setText((String.format("%.2f", distance)));

        if(time > 60)
        {
            int hours = (int) time/60;
            int minutes = (int) time%60;
            timeValue.setText(String.format("%d:%2d", hours, minutes));
        }
        else
        {
            timeValue.setText((String.format("%d", (int) time)));
            timeUnit.setText("minutes");
        }
    }
}
