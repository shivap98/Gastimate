package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

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

    public Vehicle() {}

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
}
