package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Fade;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import static com.shiv.gastimate.Constants.FROM_LOCATION_REQUEST;
import static com.shiv.gastimate.Constants.TO_LOCATION_REQUEST;

public class LocationActivity extends AppCompatActivity
{
    TextView textView;
    TextView fromLocation;
    TextView toLocation;
    TextView fromCoordinates;
    TextView toCoordinates;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        textView = findViewById(R.id.carSummary);
        fromLocation = findViewById(R.id.fromLocation);
        toLocation = findViewById(R.id.toLocation);
        fromCoordinates = findViewById(R.id.coordinatesFrom);
        toCoordinates = findViewById(R.id.coordinatesTo);

        Intent intent = getIntent();
        double mpg = intent.getDoubleExtra("vehicleMpg", 0.0);
        textView.setText(String.format("%s \nVehicle MPG: %f", intent.getStringExtra("vehicleName"), mpg));

        CardView fromCardView = findViewById(R.id.fromCardView);
        fromCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
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
    }

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
            }
            else if(locationRequestType == TO_LOCATION_REQUEST)
            {
                toLocation.setText(line);
                toCoordinates.setText(String.format("%f, %f", latLng.latitude, latLng.longitude));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
