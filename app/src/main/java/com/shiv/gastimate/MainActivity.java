package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import static com.shiv.gastimate.Constants.CAR;
import static com.shiv.gastimate.Constants.MOTORCYCLE;
import static com.shiv.gastimate.Constants.NOT_TRACKING;
import static com.shiv.gastimate.Constants.OTHER;

public class MainActivity extends AppCompatActivity
{
    RecyclerView vehiclesList;
    VehicleListAdapter vehicleListAdapter;
    ArrayList<Vehicle> vehicles;

    public static Vehicle currentVehicle;

    /**
     * Called when activity is created
     * @param savedInstanceState, previous state if exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("Car 1", "Make 1", "Model 1", 2014, 1.1, 11.11, NOT_TRACKING, 0, CAR));
        vehicles.add(new Vehicle("Motorbike 1", "Make 2", "Model 2", 2015, 2.1, 21.11, NOT_TRACKING, 0, MOTORCYCLE));
        vehicles.add(new Vehicle("Other 1", "Make 3", "Model 3", 2016, 3.1, 31.11, 0, NOT_TRACKING, OTHER));
        vehicles.add(new Vehicle("Car 2", "Make 4", "Model 4", 2017, 4.1, 41.11, 0, NOT_TRACKING, CAR));
        vehicles.add(new Vehicle("Car 3", "Make 5", "Model 5", 2018, 5.1, 51.11, 0, NOT_TRACKING, CAR));
        vehicles.add(new Vehicle("Car 4", "Make 6", "Model 6", 2019, 6.1, 61.11, 0, NOT_TRACKING, CAR));
        vehicles.add(new Vehicle("Car 5", "Make 7", "Model 7", 2020, 7.1, 71.11, 0, NOT_TRACKING, CAR));

        vehiclesList = findViewById(R.id.vehiclesList);
        vehiclesList.setLayoutManager(new LinearLayoutManager(this));
        vehicleListAdapter = new VehicleListAdapter(vehicles);
        vehiclesList.setAdapter(vehicleListAdapter);

        vehicleListAdapter.setClickListener(new VehicleListAdapter.ItemClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }

            @Override
            public void onItemClick(View view, String vehicleName)
            {
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                for(Vehicle vehicle : vehicles)
                {
                    if(vehicle.name.equals(vehicleName))
                    {
                        currentVehicle = vehicle;
                        break;
                    }
                }
                startActivity(intent);
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, VehicleAddActivity.class);
                startActivity(intent);
            }
        });
    }

    //Called when coming back here from location activity, scrolls back up
    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        vehiclesList.scrollToPosition(0);
    }

    @Override
    public void onBackPressed() {
    }
}