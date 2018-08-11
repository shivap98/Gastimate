package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    public static boolean editMode = false;
    public static boolean dbChanged;    //true if list needs to be refreshed
    boolean fabOpen = false;      //true if mainButton is expanded

    FloatingActionButton mainButton;
    TextView editModeDisplay;
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

        setFabListeners();

        editModeDisplay = findViewById(R.id.editModeDisplay);
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
            Log.e("Reading DB", e.getMessage());
            return;
        }

        dbChanged = false;
    }

    /**
     * Sets the actions and listeners for the FABs
     */
    void setFabListeners()
    {
        final FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, AddVehicleActivity.class);
                startActivity(intent);
            }
        });

        final FloatingActionButton editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mainButton.callOnClick();       //collapses the mainButton
                editMode = true;
                mainButton.setImageResource(R.drawable.ic_close);
                editModeDisplay.setVisibility(View.VISIBLE);
            }
        });

        addButton.hide();
        editButton.hide();

        mainButton = findViewById(R.id.mainButton);
        mainButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(editMode)
                {
                    editMode = false;
                    mainButton.setImageResource(R.drawable.ic_unfold);
                    editModeDisplay.setVisibility(View.GONE);
                }
                else
                {
                    if(fabOpen)
                    {
                        fabOpen = false;
                        addButton.hide();
                        editButton.hide();
                        mainButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorAccent)));
                    }
                    else
                    {
                        fabOpen = true;
                        addButton.show();
                        editButton.show();
                        mainButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorAccentDark)));
                    }
                }
            }
        });
    }

    /**
     * Called when coming back here from location activity, scrolls back up
     */
    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        if(dbChanged)
        {
            readVehicles();
        }
        if(fabOpen)
        {
            mainButton.callOnClick();
        }
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