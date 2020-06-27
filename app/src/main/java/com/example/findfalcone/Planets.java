package com.example.findfalcone;

import java.io.Serializable;

public class Planets implements Serializable {
    private String mplanetName;
    private int mdistance;
    private boolean misSelected;
    public Planets(String planetName, int distance)
    {
        this.mplanetName = planetName;
        this.mdistance = distance;
        this.misSelected = false;
    }

    public Planets()
    {
        this.mplanetName = "Planet Name";
        this.mdistance = 100;
    }

    public String getPlanetName() {
        return this.mplanetName;
    }

    public int getDistance() {
        return this.mdistance;
    }

    public boolean getSelectionStatus(){ return this.misSelected;}

    public void setSelectionStatus(boolean status){this.misSelected = status;}

    public void setPlanetName(String planetName) {
        this.mplanetName = planetName;
    }

    public void setDistance(int distance) {
        this.mdistance = distance;
    }

}
