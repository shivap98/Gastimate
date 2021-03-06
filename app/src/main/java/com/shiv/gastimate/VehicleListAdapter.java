package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.shiv.gastimate.Helper.MOTORCYCLE;
import static com.shiv.gastimate.Helper.OTHER;
import com.shiv.gastimate.Helper.VoidCallBack;

public class VehicleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<Vehicle> vehiclesBackup = new ArrayList<>();
    private VoidCallBack voidCallBack;

    /**
     * Helper class which assigns all the views within the list object
     * Extends RecyclerView.ViewHolder which has View so we can do this
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView vehicleNameText;
        TextView carText;
        TextView mpgText;
        TextView capacityText;
        ImageView imageView;
        TextView vehicleName;
        CardView cardView;

        public MyViewHolder(View view)
        {
            super(view);
            vehicleNameText = view.findViewById(R.id.vehicleNameText);
            carText = view.findViewById(R.id.vehicleText);
            mpgText = view.findViewById(R.id.mpgText);
            capacityText = view.findViewById(R.id.capacityText);
            imageView = view.findViewById(R.id.imageView1);
            vehicleName = view.findViewById(R.id.vehicleName);
            cardView = view.findViewById(R.id.cardView);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            for(int i=0; i<MainActivity.vehicles.size(); i++)
            {
                if(vehicles.get(i).name.equals(vehicleName.getText().toString()))
                {
                    MainActivity.currentVehicle = MainActivity.vehicles.get(i);
                    break;
                }
            }

            voidCallBack.execute();
        }
    }

    /**
     * @param parent, container used to get the context
     * @param viewType, unused
     * @return MyViewHolder view
     */
    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicles_list, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Sets the values of the list object
     * @param holder, casted to our helper class, sets the views
     * @param position, position of object in list
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Vehicle vehicle = vehicles.get(position);
        MyViewHolder view = (MyViewHolder) holder;

        view.vehicleName.setVisibility(View.GONE);

        view.vehicleNameText.setText(String.format("Name: %s", vehicle.name));
        view.carText.setText(String.format("%s %s %d", vehicle.make, vehicle.model, vehicle.year));
        view.mpgText.setText(String.format("Miles per Gallon: %.2f mpg", vehicle.mpg));
        view.capacityText.setText(String.format("Capacity: %.2f gal", vehicle.capacity));

        if(vehicle.type == MOTORCYCLE)
        {
            view.imageView.setImageResource(R.drawable.ic_motorcycle);
        }
        else if(vehicle.type == OTHER)
        {
            view.imageView.setImageResource(R.drawable.ic_bus);
        }

        view.vehicleName.setText(vehicle.name);
    }

    /**
     * @return size of the vehicles array
     */
    @Override
    public int getItemCount()
    {
        return vehicles.size();
    }

    /**
     * Constructor which sets the ArrayList
     * @param vehicles, ArrayList of the vehicles
     */
    public VehicleListAdapter(ArrayList<Vehicle> vehicles, VoidCallBack voidCallBack)
    {
        this.vehicles.addAll(vehicles);
        this.vehiclesBackup.addAll(vehicles);
        this.voidCallBack = voidCallBack;
    }
}
