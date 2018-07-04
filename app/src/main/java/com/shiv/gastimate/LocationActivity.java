package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Fade;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import static com.shiv.gastimate.Constants.FROM_LOCATION_REQUEST;
import static com.shiv.gastimate.Constants.TO_LOCATION_REQUEST;

public class LocationActivity extends AppCompatActivity
{
    TextView fromLocation;
    TextView toLocation;
    TextView fromCoordinates;
    TextView toCoordinates;
    ImageView fromImageView;
    ImageView toImageView;

    public static String currentFrom;
    public static String currentTo;
    public static LatLng currentFromLatLng;
    public static LatLng currentToLatLng;

    /**
     * Called when activity is created
     * @param savedInstanceState, previous state if exists
     */
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        fromLocation = findViewById(R.id.fromLocation);
        toLocation = findViewById(R.id.toLocation);
        fromCoordinates = findViewById(R.id.coordinatesFrom);
        toCoordinates = findViewById(R.id.coordinatesTo);
        fromImageView= findViewById(R.id.fromImageView);
        toImageView = findViewById(R.id.toImageView);

        CardView fromCardView = findViewById(R.id.fromCardView);
        fromCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LocationActivity.this, LocationSearchActivity.class);
                intent.putExtra("locationRequestType", FROM_LOCATION_REQUEST);
                startActivityForResult(intent, 0);
            }
        });

        CardView toCardView = findViewById(R.id.toCardView);
        toCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LocationActivity.this, LocationSearchActivity.class);
                intent.putExtra("locationRequestType", Constants.TO_LOCATION_REQUEST);
                startActivityForResult(intent, 0);
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
     * Called when location search activity is done
     * @param requestCode, used to identify activity sending the info
     * @param resultCode
     * @param intent, contains the data from location search activity
     */
    @SuppressLint("DefaultLocale")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if((requestCode == 0) && (intent != null))
        {
            int locationRequestType = intent.getIntExtra("locationRequestType", FROM_LOCATION_REQUEST);
            String line = intent.getStringExtra("locationLine");
            LatLng latLng = intent.getParcelableExtra("latLng");
            if(locationRequestType == FROM_LOCATION_REQUEST)
            {
                fromLocation.setText(line);
                fromCoordinates.setText(String.format("%f, %f", latLng.latitude, latLng.longitude));
                currentFrom = line;
                currentFromLatLng = latLng;
                Picasso.get()
                        .load(String.format("http://maps.google.com/maps/api/staticmap?center=%f,%f&zoom=18&size=400x400&sensor=false", latLng.latitude, latLng.longitude))
                        .error(R.drawable.ic_location)
                        .fit()
                        .into(fromImageView);

            }
            else if(locationRequestType == TO_LOCATION_REQUEST)
            {
                toLocation.setText(line);
                toCoordinates.setText(String.format("%f, %f", latLng.latitude, latLng.longitude));
                currentTo = line;
                currentToLatLng = latLng;
                Picasso.get()
                        .load(String.format("http://maps.google.com/maps/api/staticmap?center=%f,%f&zoom=18&size=400x400&sensor=false", latLng.latitude, latLng.longitude))
                        .error(R.drawable.ic_location)
                        .fit()
                        .into(toImageView);
            }
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
