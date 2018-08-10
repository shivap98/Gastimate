package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import static com.shiv.gastimate.Helper.CAR;
import static com.shiv.gastimate.Helper.MOTORCYCLE;
import static com.shiv.gastimate.Helper.NOT_TRACKING;
import static com.shiv.gastimate.Helper.OTHER;

public class MainActivity extends AppCompatActivity
{
    RecyclerView vehiclesList;
    VehicleListAdapter vehicleListAdapter;

    public static ArrayList<Vehicle> vehicles;
    public static Vehicle currentVehicle;

    /**
     * Called when activity is created
     *
     * @param savedInstanceState, previous state if exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        vehicles = new ArrayList<>();
        readVehicles();

        vehiclesList = findViewById(R.id.vehiclesList);
        vehiclesList.setLayoutManager(new LinearLayoutManager(this));
        vehiclesList.setAdapter(new VehicleListAdapter(vehicles));

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, AddVehicleActivity.class);
                startActivity(intent);
            }
        });
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                vehicles.add(new Vehicle("Car 1", "Make 1", "Model 1", 2014, 1.1, 11.11, NOT_TRACKING, 0, CAR));
                vehicles.add(new Vehicle("Motorbike 1", "Make 2", "Model 2", 2015, 2.1, 21.11, NOT_TRACKING, 0, MOTORCYCLE));
                vehicles.add(new Vehicle("Other 1", "Make 3", "Model 3", 2016, 3.1, 31.11, 0, NOT_TRACKING, OTHER));

                vehiclesList.setAdapter(null);
                vehiclesList.setAdapter(new VehicleListAdapter(vehicles));

                Log.i("FAB LongPress", "Added sample vehicles");
                return true;
            }
        });
    }

    /**
     * Reads the vehicles from the database to ArrayList
     */
    void readVehicles()
    {
        vehicles.clear();
        //In try catch because when database doesn't exist, app crashes
        try
        {
            //Getting all the vehicles from database list
            SQLiteDatabase vehicleDB = openOrCreateDatabase("vehicleDB.db", MODE_PRIVATE, null);
            //Adding the vehicles to ArrayList by moving cursor through them all
            Cursor cursor = vehicleDB.rawQuery("SELECT name, make, model, year, mpg, capacity, trackingGas, currGas, type FROM vehicles", null);
            while(cursor.moveToNext())
            {
                Vehicle v = new Vehicle(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                        cursor.getDouble(4), cursor.getDouble(5), cursor.getInt(6), cursor.getDouble(7), cursor.getInt(8));

                Log.i("Reading Vehicle", v.toString());
                vehicles.add(v);
            }
            cursor.close();
            vehicleDB.close();

            vehiclesList.setAdapter(null);
            vehiclesList.setAdapter(new VehicleListAdapter(vehicles));
        }
        catch(Exception e)
        {
            return;
        }
    }

    /**
     * Called when coming back here from location activity, scrolls back up
     */
    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        readVehicles();
        vehiclesList.scrollToPosition(0);
    }

    /**
     * Prevent mishandling of the back button
     */
    @Override
    public void onBackPressed()
    {
        finish();
    }
}