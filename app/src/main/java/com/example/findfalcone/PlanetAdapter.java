package com.example.findfalcone;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PlanetAdapter extends ArrayAdapter<Planets> {
    private Context context;
    private ArrayList<Planets> planets;

    public PlanetAdapter(Context context, ArrayList<Planets> planets) {
        super(context,0,planets);
        this.context = context;
        this.planets = planets;
    }

    public ArrayList<Planets> getPlanets(){return this.planets;}

    public void setPlanets(ArrayList<Planets> planets){
        this.planets = planets;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null)
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.planet_view, parent, false);

        TextView planetName = convertView.findViewById(R.id.planetName);
        TextView planetDistance = convertView.findViewById(R.id.planetDistance);
        ImageView imageHolder = convertView.findViewById(R.id.planetImageHolder);

        Planets temp = planets.get(position);
        planetName.setText(temp.getPlanetName());
        planetDistance.setText(""+temp.getDistance() + " Megamiles");
        if(temp.getSelectionStatus())
            convertView.setBackgroundResource(R.drawable.view_selected);
        else
            convertView.setBackground(null);

        switch ((temp.getPlanetName()))
        {
            case "Enchai": imageHolder.setImageResource(R.drawable.enchai);
                break;
            case "Jebing": imageHolder.setImageResource(R.drawable.jebing);
                break;
            case "Sapir": imageHolder.setImageResource(R.drawable.sapir);
                break;
            case "Lerbin": imageHolder.setImageResource(R.drawable.lerbin);
                break;
            case "Pingasor": imageHolder.setImageResource(R.drawable.pingasor);
                break;
            default: imageHolder.setImageResource(R.drawable.donlon);
        }

        return convertView;
    }

}
