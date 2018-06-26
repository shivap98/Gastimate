package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LocationActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        TextView textView = findViewById(R.id.textView);
        Intent intent = getIntent();
        double mpg = intent.getDoubleExtra("vehicleMpg", 0.0);
        textView.setText(String.format("Vehicle Name: %s \nVehicle MPG: %f", intent.getStringExtra("vehicleName"), mpg));
    }
}
