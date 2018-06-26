package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    RecyclerView vehiclesList;
    VehicleListAdapter vehicleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ArrayList<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("Car 1", "Make 1", "Model 1", 2014,1.1, 11.11, 0, 0, 0));
        vehicles.add(new Vehicle("Motorbike 1", "Make 2", "Model 2", 2015,2.1, 21.11, 0, 0, 1));
        vehicles.add(new Vehicle("Other 1", "Make 3", "Model 3", 2016,3.1, 31.11, 0, 0, 2));
        vehicles.add(new Vehicle("Car 2", "Make 3", "Model 4", 2017,4.1, 41.11, 0, 0, 0));


        vehiclesList = findViewById(R.id.vehiclesList);
        vehiclesList.setLayoutManager(new LinearLayoutManager(this));
        vehicleListAdapter = new VehicleListAdapter(vehicles);
        vehiclesList.setAdapter(vehicleListAdapter);
    }
}
