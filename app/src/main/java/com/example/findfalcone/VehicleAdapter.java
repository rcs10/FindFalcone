package com.example.findfalcone;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class VehicleAdapter extends ArrayAdapter<Vehicles> {

    private Context context;
    private ArrayList<Vehicles> vehicles;

    public VehicleAdapter(@NonNull Context context, ArrayList<Vehicles> vehicles) {
        super(context, 0,vehicles);
        this.context = context;
        this.vehicles = vehicles;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null)
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.vehicle_view, parent, false);

        TextView vehicleName = convertView.findViewById(R.id.vehicleName);
        TextView unitsAvailable = convertView.findViewById(R.id.available_units);
        TextView maxDistance = convertView.findViewById(R.id.vehicleDistance);
        TextView topSpeed = convertView.findViewById(R.id.vehicleSpeed);
        ImageView imageHolder = convertView.findViewById(R.id.vehicleImageHolder);

        Vehicles temp = vehicles.get(position);
        vehicleName.setText(temp.getVehiclesName());
        unitsAvailable.setText(""+temp.getTotalUnits());
        maxDistance.setText(""+temp.getMaxDistance()+" Megamiles");
        topSpeed.setText(""+temp.getSpeed()+" megamiles/hour");

        switch ((temp.getVehiclesName()))
        {
            case "Space rocket": imageHolder.setImageResource(R.drawable.space_rocket);
                break;
            case "Space shuttle": imageHolder.setImageResource(R.drawable.space_shuttle);
                break;
            case "Space ship": imageHolder.setImageResource(R.drawable.space_ship);
                break;
            default: imageHolder.setImageResource(R.drawable.space_pod);
        }

        return convertView;
    }

}
