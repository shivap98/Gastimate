package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 8/11/2018.
 */

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import static com.shiv.gastimate.Helper.NOT_TRACKING;
import static com.shiv.gastimate.Helper.toggleVisibility;

public class EditVehicle extends AppCompatActivity
{
    TextInputEditText nameInput;
    TextInputEditText makeInput;
    TextInputEditText modelInput;
    TextInputEditText yearInput;
    TextInputEditText mpgInput;
    TextInputEditText capacityInput;

    ConstraintLayout typeSelectLayout;
    ImageView imageType;

    String oldName;
    int type = 0;

    /**
     * Called when activity is created
     *
     * @param savedInstanceState, previous state if exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_vehicle_activity);

        imageType = findViewById(R.id.imageType);
        nameInput = findViewById(R.id.nameInput);
        makeInput = findViewById(R.id.makeInput);
        modelInput = findViewById(R.id.modelInput);
        yearInput = findViewById(R.id.yearInput);
        mpgInput = findViewById(R.id.mpgInput);
        capacityInput = findViewById(R.id.capacityInput);

        setOldValues();
        setTypeSelectListeners();
        setButtons();
    }

    /**
     * Sets the actions and listeners for buttons in the activity
     */
    void setButtons()
    {
        FloatingActionButton saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: Show confirmation popup
                Vehicle v = new Vehicle(nameInput.getText().toString(), makeInput.getText().toString(),
                        modelInput.getText().toString(), Integer.parseInt(yearInput.getText().toString()),
                        Double.parseDouble(mpgInput.getText().toString()), Double.parseDouble(capacityInput.getText().toString()),
                        NOT_TRACKING, 0, type);
                Log.i("Vehicle Created", v.toString());

                try
                {
                    //Updating the data in SQL
                    SQLiteDatabase vehicleDB = openOrCreateDatabase("vehicleDB.db", MODE_PRIVATE, null);
                    String values = "name = '" + v.name + "', make = '" + v.make + "', model = '" + v.model
                            + "', year = '" + v.year + "', mpg = '" + v.mpg + "', capacity = '" + v.capacity
                            + "', trackingGas = '" + v.trackingGas + "', currGas = '" + v.currGas + "', type = '" + v.type + "'";
                    String query = "UPDATE vehicles SET " + values + " WHERE name='" + oldName + "'";
                    Log.i("Vehicle Edit Query", query);
                    vehicleDB.execSQL(query);
                    vehicleDB.close();
                    Log.i("Vehicle Updated", "");
                    MainActivity.dbChanged = true;
                    finish();
                }
                catch(Exception e)
                {
                    Log.e("Vehicle Update Failed", e.getMessage());
                }
            }
        });

    }

    /**
     * Sets the fields with the original vehicle values
     */
    @SuppressLint("SetTextI18n")
    void setOldValues()
    {
        Vehicle vehicle = MainActivity.currentVehicle;
        oldName = vehicle.name;
        nameInput.setText(vehicle.name);
        makeInput.setText(vehicle.make);
        modelInput.setText(vehicle.model);
        yearInput.setText("" + vehicle.year);
        mpgInput.setText("" + vehicle.mpg);
        capacityInput.setText("" + vehicle.capacity);

        //type = 0 and car icon is default
        if(vehicle.type == 1)
        {
            imageType.setImageResource(R.drawable.ic_motorcycle);
            type = 1;
        }
        else if(vehicle.type == 2)
        {
            imageType.setImageResource(R.drawable.ic_bus);
            type = 2;
        }
    }

    /**
     * Handles the listeners and toggles for the type selector in Custom card
     */
    void setTypeSelectListeners()
    {
        typeSelectLayout = findViewById(R.id.typeSelectLayout);

        imageType.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                toggleVisibility(typeSelectLayout);
            }
        });

        ImageView imageCar = findViewById(R.id.imageCar);
        imageCar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = 0;
                typeSelectLayout.setVisibility(View.GONE);
                imageType.setImageResource(R.drawable.ic_car);
            }
        });

        ImageView imageMotorcycle = findViewById(R.id.imageMotorcycle);
        imageMotorcycle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = 1;
                typeSelectLayout.setVisibility(View.GONE);
                imageType.setImageResource(R.drawable.ic_motorcycle);
            }
        });

        ImageView imageOther = findViewById(R.id.imageOther);
        imageOther.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = 2;
                typeSelectLayout.setVisibility(View.GONE);
                imageType.setImageResource(R.drawable.ic_bus);
            }
        });
    }

    /**
     * Overrides the toolbar back button
     *
     * @return has to return true idk why
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
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
