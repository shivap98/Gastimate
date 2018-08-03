package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 7/04/2018.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import static com.shiv.gastimate.Helper.FROM_LOCATION_REQUEST;
import static com.shiv.gastimate.Helper.TO_LOCATION_REQUEST;

public class LocationActivity extends AppCompatActivity
{
    TextView fromLocation;
    TextView toLocation;
    TextView fromCoordinates;
    TextView toCoordinates;
    ImageView fromImageView;
    ImageView toImageView;
    ImageView fromIcon;
    ImageView toIcon;

    public static String currentFromName;
    public static String currentToName;
    public static LatLng currentFromLatLng;
    public static LatLng currentToLatLng;

    GoogleApiClient googleApiClient;

    /**
     * Called when activity is created
     *
     * @param savedInstanceState, previous state if exists
     */
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, null)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        fromLocation = findViewById(R.id.fromLocation);
        toLocation = findViewById(R.id.toLocation);
        fromCoordinates = findViewById(R.id.coordinatesFrom);
        toCoordinates = findViewById(R.id.coordinatesTo);
        fromImageView = findViewById(R.id.fromImageView);
        toImageView = findViewById(R.id.toImageView);
        fromIcon = findViewById(R.id.fromIcon);
        toIcon = findViewById(R.id.toIcon);

        fromImageView.setVisibility(View.GONE);
        toImageView.setVisibility(View.GONE);

        CardView fromCardView = findViewById(R.id.fromCardView);
        fromCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openPlacePicker(FROM_LOCATION_REQUEST);
            }
        });

        CardView toCardView = findViewById(R.id.toCardView);
        toCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openPlacePicker(TO_LOCATION_REQUEST);
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LocationActivity.this, FuelActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Opens up the PlacePicker UI
     * @param requestCode, tells if from or to location
     */
    private void openPlacePicker(int requestCode)
    {
        if((googleApiClient == null) || (!googleApiClient.isConnected()))
        {
            return;
        }

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try
        {
            startActivityForResult(builder.build(LocationActivity.this), requestCode);
        }
        catch(GooglePlayServicesRepairableException e)
        {
            Log.e("Place Picker SDK", e.getMessage());
        }
        catch(GooglePlayServicesNotAvailableException e)
        {
            Log.e("Place Picker SDK", "Place Services not available " + e.getMessage());
        }
    }

    /**
     * Called when location search activity is done
     *
     * @param requestCode, used to identify activity sending the info
     * @param resultCode, not used
     * @param intent, contains the data from location search activity
     */
    @SuppressLint("DefaultLocale")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
//        TODO: When number of lines in texts (name or coordinates) are changed then static map image rendering is weird

        if(intent != null)
        {
            if(requestCode == FROM_LOCATION_REQUEST)
            {
                Place place = PlacePicker.getPlace(this, intent);
                fromLocation.setText(place.getName());
                LatLng latLng = place.getLatLng();
                fromCoordinates.setText(String.format("%f, %f", latLng.latitude, latLng.longitude));
                currentFromName = (String) place.getName();
                currentFromLatLng = latLng;
                fromImageView.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(String.format("%s?center=%f,%f&zoom=18&size=400x400&sensor=false",
                                getResources().getString(R.string.static_map_url), latLng.latitude, latLng.longitude))
                        .fit()
                        .centerCrop()
                        .into(fromImageView);
                fromIcon.setVisibility(View.GONE);
            }
            else if(requestCode == TO_LOCATION_REQUEST)
            {
                Place place = PlacePicker.getPlace(this, intent);
                toLocation.setText(place.getName());
                LatLng latLng = place.getLatLng();
                toCoordinates.setText(String.format("%f, %f", latLng.latitude, latLng.longitude));
                currentToName = (String) place.getName();
                currentToLatLng = latLng;
                toImageView.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(String.format("%s?center=%f,%f&zoom=18&size=400x400&sensor=false",
                                getResources().getString(R.string.static_map_url), latLng.latitude, latLng.longitude))
                        .fit()
                        .centerCrop()
                        .into(toImageView);
                toIcon.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Overrides the toolbar back button
     * @return
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    /**
     * Starts the googleApiClient with onStart
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        if(googleApiClient != null)
        {
            googleApiClient.connect();
        }
    }


    /**
     * Stops the googleApiClient with onStop
     */
    @Override
    protected void onStop()
    {
        if((googleApiClient != null) && (googleApiClient.isConnected()))
        {
            googleApiClient.disconnect();
        }
        super.onStop();
    }
}
