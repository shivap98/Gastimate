package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 7/04/2018.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
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

import static com.shiv.gastimate.Constants.animateTextView;
import static com.shiv.gastimate.LocationActivity.currentFromLatLng;
import static com.shiv.gastimate.LocationActivity.currentToLatLng;

public class Gastimate extends AppCompatActivity
{
    TextView gasValue;
    TextView moneyValue;
    TextView distanceValue;
    TextView timeValue;
    TextView timeUnit;

    ConstraintLayout summaryLayout;
    ConstraintLayout summaryDetails;
    TextView fromText;
    TextView toText;
    TextView vehicleText;
    TextView gasText;
    ImageView dropDown;

    View helpView;      //Just a view to animate in the CardView2 so that animate layout changes works there

    FrameLayout progressBarLayout;

    ConstraintLayout directionsLayout;
    Switch directionsToggle;

    double distance;        //in miles
    double time;            //in minutes
    double gas;
    double money;

    /**
     * Called when activity is created
     * @param savedInstanceState, previous state if exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gastimate_activity);
        progressBarLayout = findViewById(R.id.frameLayout);

        gasValue = findViewById(R.id.gasValue);
        moneyValue = findViewById(R.id.moneyValue);
        distanceValue = findViewById(R.id.distanceValue);
        timeValue = findViewById(R.id.timeValue);
        timeUnit = findViewById(R.id.unit4);

        summaryLayout = findViewById(R.id.summaryLayout);
        summaryDetails = findViewById(R.id.summaryDetails);
        fromText = findViewById(R.id.fromText);
        toText = findViewById(R.id.toText);
        vehicleText = findViewById(R.id.vehicleText);
        gasText = findViewById(R.id.gasText);
        dropDown = findViewById(R.id.dropDownSymbol);

        helpView = findViewById(R.id.view);

        directionsLayout = findViewById(R.id.directionsLayout);
        directionsToggle = findViewById(R.id.directionsToggle);

        summaryDetails.setVisibility(View.GONE);

        distanceMatrixAPI();
        setSummary();

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!directionsToggle.isChecked())
                {
                    Intent intent = new Intent(Gastimate.this, MainActivity.class);
                    //Clearing the entire back stack and going back to MainActivity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finishAndRemoveTask();
                }
                else if(directionsToggle.isChecked())
                {
                    @SuppressLint("DefaultLocale") Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(String.format("http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                                    currentFromLatLng.latitude, currentFromLatLng.longitude,
                                    currentToLatLng.latitude, currentToLatLng.longitude)));
                    startActivity(intent);
                    directionsToggle.toggle();
                }
            }
        });

        summaryLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(summaryDetails.getVisibility() == View.VISIBLE)
                {
                    summaryDetails.setVisibility(View.GONE);
                    helpView.setVisibility(View.GONE);
                    dropDown.setRotation(0);
                }
                else if(summaryDetails.getVisibility() == View.GONE)
                {
                    summaryDetails.setVisibility(View.VISIBLE);
                    helpView.setVisibility(View.VISIBLE);
                    dropDown.setRotation(180);
                }
            }
        });

        directionsLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                directionsToggle.toggle();
            }
        });
    }

    /**
     * Calls the distance matrix api and sets the corresponding values
     */
    @SuppressLint("DefaultLocale")
    void distanceMatrixAPI()
    {
        String url = String.format("%s&origins=%f,%f&destinations=%f,%f", getResources().getString(R.string.distance_matrix_url),
                currentFromLatLng.latitude, currentFromLatLng.longitude,
                currentToLatLng.latitude, currentToLatLng.longitude);

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
                    if(response.contains("ZERO_RESULTS"))
                    {
                        notDrivable();
                    }
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

    /**
     * If not drivable, then shows popup and goes back to LocationActivity, clearing the back stack
     */
    void notDrivable()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Error getting the driving distance!");
        alertDialogBuilder.setMessage("There is no driving path available for the given locations.\nPlease go back and try again.");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent intent = new Intent(Gastimate.this, LocationActivity.class);
                //Clearing the back stack after LocationActivity, this still allows it to go back to the MainActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try
        {
            alertDialog.show();
        }
        catch(WindowManager.BadTokenException e){}        //Called when Gastimate activity is closed
    }

    /**
     * Sets the values on screen
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    void setValues()
    {
        progressBarLayout.setVisibility(View.GONE);

        gas = distance/MainActivity.currentVehicle.mpg;
        animateTextView((float)0.00, (float) gas, gasValue);

        money = gas * FuelActivity.currentSetPrice;
        animateTextView((float)0.00, (float) money, moneyValue);

        animateTextView((float)0.00, (float) distance, distanceValue);

        if(time > 60)
        {
            int hours = (int) time/60;
            int minutes = (int) time%60;
            timeValue.setText(String.format("%d:%02d", hours, minutes));
        }
        else
        {
            String minutes = String.format("%d", (int) time);
            timeValue.setText(minutes.replace(".", ":"));
            timeUnit.setText("minutes");
        }
    }

    /**
     * Sets the trip summary
     */
    @SuppressLint("DefaultLocale")
    void setSummary()
    {
        fromText.setText(String.format("From: %s", LocationActivity.currentFromName));
        toText.setText(String.format("To: %s", LocationActivity.currentToName));
        vehicleText.setText(String.format("Vehicle: %s", MainActivity.currentVehicle.name));
        gasText.setText(String.format("Gas price: %.2f USD/gal", FuelActivity.currentSetPrice));
    }
}
