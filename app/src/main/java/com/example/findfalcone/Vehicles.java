package com.example.findfalcone;

import java.util.ArrayList;

public class Vehicles{
    private String mvehiclesName;
    private int mtotalUnits;
    private int mmaxDistance;
    private int mspeed;
    private ArrayList<String> dispatchedTo;

    public Vehicles(String vehicleName, int totalUnits, int maxDistance, int topSpeed)
    {
        this.mvehiclesName = vehicleName;
        this.mtotalUnits = totalUnits;
        this.mmaxDistance = maxDistance;
        this.mspeed = topSpeed;
        this.dispatchedTo = new ArrayList<>();
    }

    public String getVehiclesName() {
        return mvehiclesName;
    }

    public int getTotalUnits() {
        return mtotalUnits;
    }

    public int getMaxDistance() {
        return mmaxDistance;
    }

    public void dispatchTo(String planetName){this.dispatchedTo.add(planetName);}

    public boolean contains(String planetName){return this.dispatchedTo.contains(planetName);}

    public void removeFrom(String planetName){
            this.dispatchedTo.remove(planetName);
    }

    public int totalDispatchedCounts(){return this.dispatchedTo.size();}

    public void updateTotalUnit(int unit)
    {
        this.mtotalUnits = unit;
    }

    public int getSpeed() {
        return mspeed;
    }
}
