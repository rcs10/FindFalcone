package com.example.findfalcone;

public class Vehicles{
    private String mvehiclesName;
    private int mtotalUnits;
    private int mmaxDistance;
    private int mspeed;

    public Vehicles(String vehicleName, int totalUnits, int maxDistance, int topSpeed)
    {
        this.mvehiclesName = vehicleName;
        this.mtotalUnits = totalUnits;
        this.mmaxDistance = maxDistance;
        this.mspeed = topSpeed;
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

    public int getSpeed() {
        return mspeed;
    }
}
