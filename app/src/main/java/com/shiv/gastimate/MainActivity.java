package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import com.shiv.gastimate.Helper.VoidCallBack;

import static com.shiv.gastimate.Vehicle.readDB;

public class MainActivity extends AppCompatActivity
{
    RecyclerView vehiclesList;

    public static ArrayList<Vehicle> vehicles;
    public static Vehicle currentVehicle;

    VoidCallBack onListClick;

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

        vehiclesList = findViewById(R.id.vehiclesList);

        onListClick = new VoidCallBack()
        {
            @Override
            public void execute()
            {
                Intent intent;
                if(!editMode)
                {
                    intent = new Intent(MainActivity.this, LocationActivity.class);
                }
                else
                {
                    intent = new Intent(MainActivity.this, EditVehicle.class);
                }
                startActivity(intent);
            }
        };

        vehicles = new ArrayList<>();
        readVehicles();

        vehiclesList.setLayoutManager(new LinearLayoutManager(this));
        vehiclesList.setAdapter(new VehicleListAdapter(vehicles, onListClick));

        setFabListeners();

        editModeDisplay = findViewById(R.id.editModeDisplay);
    }

    /**
     * Reads the vehicles from the database to ArrayList
     */
    void readVehicles()
    {
        vehicles.clear();

        readDB(MainActivity.this);

        vehiclesList.setAdapter(null);
        vehiclesList.setAdapter(new VehicleListAdapter(vehicles, onListClick));

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
                //close the mainButton first
                fabOpen = false;
                addButton.hide();
                editButton.hide();
                mainButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorAccent)));

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
                    mainButton.setImageResource(R.drawable.ic_unfold_open);
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
                        mainButton.setImageResource(R.drawable.ic_unfold_open);
                    }
                    else
                    {
                        fabOpen = true;
                        addButton.show();
                        editButton.show();
                        mainButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorAccentDark)));
                        mainButton.setImageResource(R.drawable.ic_unfold_close);
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
        if(fabOpen || editMode)
        {
            mainButton.callOnClick();
        }
        else
        {
            finish();
        }
    }
}