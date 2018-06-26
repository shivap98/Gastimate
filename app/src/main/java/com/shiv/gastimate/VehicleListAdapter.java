package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

public class VehicleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    ArrayList<Vehicle> vehiclesBackup = new ArrayList<>();

    private ItemClickListener itemClickListener;

    /**
     * Function to set the itemClickListener object
     */
    public void setClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener extends View.OnClickListener
    {
//        @Override
//        void onClick(View view);

        void onItemClick(View view, String vehicleName, double mpg);
    }

    /**
     * Helper class which assigns all the views within the list object
     * Extends RecyclerView.ViewHolder which has View so we can do this
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView vehicleName;
        TextView mpg;
        CardView cardView;

        public MyViewHolder(View view)
        {
            super(view);
            vehicleName = view.findViewById(R.id.vehicleName);
            mpg = view.findViewById(R.id.mpg);
            cardView = view.findViewById(R.id.cardView);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            itemClickListener.onItemClick(view, vehicleName.getText().toString(), Double.parseDouble(mpg.getText().toString()));
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicles_list_layout, parent, false);
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
        String vehicleName = vehicles.get(position).name;
        double mpg = vehicles.get(position).mpg;
        MyViewHolder view = (MyViewHolder) holder;

        view.vehicleName.setText(vehicleName);
        view.mpg.setText(String.format("%f", mpg));
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
    public VehicleListAdapter(ArrayList<Vehicle> vehicles)
    {
        this.vehicles.addAll(vehicles);
        this.vehiclesBackup.addAll(vehicles);
    }
}
