package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

public class LocationActivity extends AppCompatActivity
{
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        TextView textView = findViewById(R.id.carSummary);
        Intent intent = getIntent();
        double mpg = intent.getDoubleExtra("vehicleMpg", 0.0);
        textView.setText(String.format("Vehicle Name: %s \nVehicle MPG: %f", intent.getStringExtra("vehicleName"), mpg));

        CardView fromCardView = findViewById(R.id.fromCardView);
        fromCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LocationActivity.this, SetLocationActivity.class);
                intent.putExtra("locationType", 0); //0 if from, 1 if to
                startActivity(intent);
            }
        });

        CardView toCardView = findViewById(R.id.toCardView);
        toCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LocationActivity.this, SetLocationActivity.class);
                intent.putExtra("locationType", 1); //0 if from, 1 if to
                startActivity(intent);
            }
        });
    }
}
