package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;
import static com.shiv.gastimate.Helper.showPrompt;
import com.shiv.gastimate.Helper.BooleanCallBack;
import com.shiv.gastimate.Helper.VoidCallBack;

public class Vehicle
{
    String name;    //customizable name for vehicle

    String make;
    String model;
    int year;

    double mpg;      //miles per gallon
    double capacity;  //maximum gallons of fuel

    int trackingGas;    //1 if tracking gas in this vehicle, 0 if not, see Helper.java
    double currGas;      //current gas

    int type;   //0 if car, 1 if motorcycle, 2 if other, see Helper.java

    public Vehicle(String name, String make, String model, int year, double mpg, double capacity, int trackingGas, double currGas, int type)
    {
        this.name = name;
        this.make = make;
        this.model = model;
        this.year = year;
        this.mpg = mpg;
        this.capacity = capacity;
        this.trackingGas = trackingGas;
        this.currGas = currGas;
        this.type = type;
    }

    public Vehicle()
    {
    }

    @Override
    public String toString()
    {
        return "Vehicle{" +
                "name='" + name + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", mpg=" + mpg +
                ", capacity=" + capacity +
                ", trackingGas=" + trackingGas +
                ", currGas=" + currGas +
                ", type=" + type +
                '}';
    }

    /**
     * Reads the database into the ArrayList in MainActivity
     * @param context, used to call openOrCreateDatabase from ContextWrapper class
     */
    public static void readDB(Context context)
    {
        try
        {
            //Getting all the vehicles from database list
            SQLiteDatabase vehicleDB = context.openOrCreateDatabase("vehicleDB.db", MODE_PRIVATE, null);
            //Adding the vehicles to ArrayList by moving cursor through them all
            Cursor cursor = vehicleDB.rawQuery("SELECT name, make, model, year, mpg, capacity, trackingGas, currGas, type FROM vehicles", null);
            while(cursor.moveToNext())
            {
                Vehicle v = new Vehicle(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                        cursor.getDouble(4), cursor.getDouble(5), cursor.getInt(6), cursor.getDouble(7), cursor.getInt(8));

                Log.i("Reading Vehicle", v.toString());
                MainActivity.vehicles.add(v);
            }
            cursor.close();
            vehicleDB.close();

        }
        catch(Exception e)
        {
            Log.e("Reading DB Error", e.getMessage());
        }
    }

    /**
     * Adds a vehicle to the database
     *
     * @param context, used to call openOrCreateDatabase from ContextWrapper class
     * @param vehicle, vehicle to be added
     * @param booleanCallBack, executes true callback if added
     */
    public static void addVehicle(Context context, Vehicle vehicle, final BooleanCallBack booleanCallBack)
    {
        try
        {
            //Adding object to SQL
            SQLiteDatabase vehicleDB = context.openOrCreateDatabase("vehicleDB.db", MODE_PRIVATE, null);
            vehicleDB.execSQL("CREATE TABLE IF NOT EXISTS vehicles (name VARCHAR(80) PRIMARY KEY, make VARCHAR(80), model VARCHAR(80)," +
                    "year INT, mpg REAL, capacity REAL, trackingGas INT, currGas REAL, type INT)");
            //Setting up string values to add to the table
            String values = " ('" + vehicle.name + "', '" + vehicle.make + "', '" + vehicle.model + "', '" + vehicle.year + "', '" + vehicle.mpg
                    + "', '" + vehicle.capacity + "', '" + vehicle.trackingGas + "', '" + vehicle.currGas + "', '" + vehicle.type + "')";
            String query = "INSERT INTO vehicles (name, make, model, year, mpg, capacity, trackingGas, currGas, type) VALUES" + values;
            Log.i("Vehicle Add Query", query);
            vehicleDB.execSQL(query);
            vehicleDB.close();

            Log.i("VehicleC", "Added to DB");
            booleanCallBack.execute(true);
        }
        catch(SQLiteConstraintException e)
        {
            Log.e("VehicleC SQL", e.getMessage());
            showPrompt(context, "Name already exists!",
                    "Please change the name of the vehicle", false, null);  //replace null with VoidCallBack if action is needed
        }
    }

    /**
     * Edits a vehicle from the database
     *
     * @param context, used to call openOrCreateDatabase from ContextWrapper class
     * @param oldName, original name of vehicle to be edited, original because its the primary key
     * @param vehicle, vehicle object with new values
     * @param voidCallBack, executes callback when edited
     */
    public static void editVehicle(Context context, String oldName, Vehicle vehicle, VoidCallBack voidCallBack)
    {
        SQLiteDatabase vehicleDB = context.openOrCreateDatabase("vehicleDB.db", MODE_PRIVATE, null);
        String values = "name = '" + vehicle.name + "', make = '" + vehicle.make + "', model = '" + vehicle.model
                + "', year = '" + vehicle.year + "', mpg = '" + vehicle.mpg + "', capacity = '" + vehicle.capacity
                + "', trackingGas = '" + vehicle.trackingGas + "', currGas = '" + vehicle.currGas + "', type = '" + vehicle.type + "'";
        String query = "UPDATE vehicles SET " + values + " WHERE name='" + oldName + "'";
        Log.i("Vehicle Edit Query", query);
        vehicleDB.execSQL(query);
        vehicleDB.close();
        Log.i("Vehicle Updated", "");
        voidCallBack.execute();
    }

    /**
     * Edits a vehicle from the database
     *
     * @param context, used to call openOrCreateDatabase from ContextWrapper class
     * @param oldName, original name of vehicle to be edited, original because its the primary key
     * @param voidCallBack, executes callback when edited
     */
    public static void deleteVehicle(Context context, String oldName, VoidCallBack voidCallBack)
    {
        SQLiteDatabase vehicleDB = context.openOrCreateDatabase("vehicleDB.db", MODE_PRIVATE, null);
        String query = "DELETE FROM vehicles WHERE name='" + oldName + "'";
        Log.i("Vehicle Delete Query", query);
        vehicleDB.execSQL(query);
        /*
        IMPORTANT: The above command removes the row but leaves empty space instead of it
        It doesn't reorganise the whole list, so we have to call the vacuum function in sqlite
        which removes all this extra useless space
        */
        vehicleDB.execSQL("VACUUM");
        vehicleDB.close();
        Log.i("Vehicle Deleted", "");
        voidCallBack.execute();
    }

}
