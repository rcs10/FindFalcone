package com.example.findfalcone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Vehicles_Selection extends AppCompatActivity {

    Boolean connected = true;
    private static ProgressDialog mProgressDialog;
    private static final String TAG = Vehicles_Selection.class.getSimpleName();
    int[] finalVehiclesSpeed = new int[4];
    String[] finalVehicle = new String[4];
    ArrayList<Vehicles> allVehicle = new ArrayList<>();
    ArrayList<String> vehiclesName = new ArrayList<>();
    ArrayList<String> planetsName = new ArrayList<>();
    ArrayList<Integer> planetDistance = new ArrayList<>();
    ArrayList<Planets> selectedPlanets = new ArrayList<>();
    TextView selectedPlanet1, selectedPlanet2, selectedPlanet3, selectedPlanet4;
    Spinner spinner1, spinner2, spinner3, spinner4;
    ArrayList<TextView> views = new ArrayList<>();
    private ListView listview;
    Button huntBegin;
    boolean b0,b1,b2,b3;
    boolean first1,first2,first3,first4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles_selection);
        connected = true;
        b0 = b1 = b2 = b3 = false;
        first1 = first2 = first3 = first4 = true;
        selectedPlanet1 = (TextView) findViewById(R.id.planet1);
        views.add(selectedPlanet1);
        selectedPlanet2 = (TextView) findViewById(R.id.planet2);
        views.add(selectedPlanet2);
        selectedPlanet3 = (TextView) findViewById(R.id.planet3);
        views.add(selectedPlanet3);
        selectedPlanet4 = (TextView) findViewById(R.id.planet4);
        views.add(selectedPlanet4);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner4 = (Spinner) findViewById(R.id.spinner4);

        selectedPlanets = (ArrayList<Planets>)getIntent().getSerializableExtra("selected_planets");
        for(int i = 0 ; i < 4 ; i++) {
            planetsName.add(selectedPlanets.get(i).getPlanetName());
            planetDistance.add(selectedPlanets.get(i).getDistance());
            views.get(i).setText(selectedPlanets.get(i).getPlanetName());
            views.get(i).setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
        }

        listview = (ListView) findViewById(R.id.vehicleListView);
        huntBegin = (Button) findViewById(R.id.huntBegin);
        huntBegin.setVisibility(View.GONE);
        huntBegin.setOnClickListener(this::onClick);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if(connected) {
            new GetVehicles().execute();
            showSimpleProgressDialog(this, "Loading...", "Fetching Vehicles Details!!", false);
        }
        else
        {
            Toast.makeText(Vehicles_Selection.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSpinner(Spinner spinner, int mposition) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int distance;
                String vehicle;
                switch (mposition) {
                    case 0:
                        spinner2.setEnabled(false);
                        spinner3.setEnabled(false);
                        spinner4.setEnabled(false);
                        distance = selectedPlanets.get(mposition).getDistance();
                        if(first1) {
                            first1 = false;
                            spinner.setTag(position);
                        }
                        else {
                            int preselected = (Integer) spinner.getTag();
                            if (preselected != 0) {
                                vehicle = spinner.getItemAtPosition(preselected).toString();
                                for (Vehicles vehicles : new ArrayList<Vehicles>(allVehicle)) {
                                    if (vehicles.getVehiclesName().matches(vehicle)) {
                                        if (vehicles.totalDispatchedCounts()>0)
                                            if(vehicles.contains(selectedPlanets.get(mposition).getPlanetName())){
                                            Vehicles updatedvehicle = vehicles;
                                            updatedvehicle.updateTotalUnit(updatedvehicle.getTotalUnits() + 1);
                                            allVehicle.remove(vehicles);
                                            allVehicle.add(updatedvehicle);
                                        }
                                    }
                                }
                            }
                        }

                        vehicle = spinner.getItemAtPosition(position).toString();
                        for (Vehicles vehicles : new ArrayList<Vehicles>(allVehicle)) {
                            if (vehicles.getVehiclesName().matches(vehicle)) {
                                if ((vehicles.getMaxDistance() >= distance) && (vehicles.getTotalUnits() > 0)) {
                                    b0 = true;
                                    selectedPlanet1.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.green));
                                    Vehicles updatedvehicle = vehicles;
                                    finalVehiclesSpeed[mposition] = vehicles.getSpeed();
                                    finalVehicle[mposition] = vehicle;
                                    updatedvehicle.updateTotalUnit(updatedvehicle.getTotalUnits() - 1);
                                    updatedvehicle.dispatchTo(selectedPlanets.get(mposition).getPlanetName());
                                    allVehicle.remove(vehicles);
                                    allVehicle.add(updatedvehicle);
                                } else {
                                    b0 = false;
                                    selectedPlanet1.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
                                }
                                break;
                            }
                            else
                            {
                                b0 = false;
                                selectedPlanet1.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
                            }
                        }
                        spinner.setTag(position);
                        spinner2.setEnabled(true);
                        spinner3.setEnabled(true);
                        spinner4.setEnabled(true);
                        break;

                    case 1:
                        spinner1.setEnabled(false);
                        spinner3.setEnabled(false);
                        spinner4.setEnabled(false);
                        distance = selectedPlanets.get(mposition).getDistance();
                        if(first2) {
                            first2 = false;
                            spinner.setTag(position);
                        }
                        else {
                            int preselected = (Integer) spinner.getTag();
                            if (preselected != 0) {
                                vehicle = spinner.getItemAtPosition(preselected).toString();
                                for (Vehicles vehicles : new ArrayList<Vehicles>(allVehicle)) {
                                    if (vehicles.getVehiclesName().matches(vehicle)) {
                                        if (vehicles.totalDispatchedCounts()>0)
                                            if(vehicles.contains(selectedPlanets.get(mposition).getPlanetName())){
                                            Vehicles updatedvehicle = vehicles;
                                            updatedvehicle.updateTotalUnit(updatedvehicle.getTotalUnits() + 1);
                                            allVehicle.remove(vehicles);
                                            allVehicle.add(updatedvehicle);
                                        }
                                    }
                                }
                            }
                        }
                        vehicle = spinner.getItemAtPosition(position).toString();
                        for (Vehicles vehicles : new ArrayList<Vehicles>(allVehicle)) {
                            if (vehicles.getVehiclesName().matches(vehicle)) {
                                if (vehicles.getMaxDistance() >= distance && vehicles.getTotalUnits() > 0) {
                                    b1 = true;
                                    selectedPlanet2.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.green));
                                    Vehicles updatedvehicle = vehicles;
                                    finalVehicle[mposition] = vehicle;
                                    finalVehiclesSpeed[mposition] = vehicles.getSpeed();
                                    updatedvehicle.updateTotalUnit(updatedvehicle.getTotalUnits() - 1);
                                    updatedvehicle.dispatchTo(selectedPlanets.get(mposition).getPlanetName());
                                    allVehicle.remove(vehicles);
                                    allVehicle.add(updatedvehicle);
                                } else {
                                    b1 = false;
                                    selectedPlanet2.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
                                }
                                break;
                            }
                            else
                            {
                                b1 = false;
                                selectedPlanet2.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
                            }
                        }
                        spinner.setTag(position);
                        spinner1.setEnabled(true);
                        spinner3.setEnabled(true);
                        spinner4.setEnabled(true);
                        break;

                    case 2:
                        spinner1.setEnabled(false);
                        spinner2.setEnabled(false);
                        spinner4.setEnabled(false);
                        distance = selectedPlanets.get(mposition).getDistance();
                        if(first3) {
                            first3 = false;
                            spinner.setTag(position);
                        }
                        else {
                            int preselected = (Integer) spinner.getTag();
                            if (preselected != 0) {
                                vehicle = spinner.getItemAtPosition(preselected).toString();
                                for (Vehicles vehicles : new ArrayList<Vehicles>(allVehicle)) {
                                    if (vehicles.getVehiclesName().matches(vehicle)) {
                                        if (vehicles.totalDispatchedCounts()>0)
                                            if(vehicles.contains(selectedPlanets.get(mposition).getPlanetName())){
                                            Vehicles updatedvehicle = vehicles;
                                            updatedvehicle.updateTotalUnit(updatedvehicle.getTotalUnits() + 1);
                                            allVehicle.remove(vehicles);
                                            allVehicle.add(updatedvehicle);
                                        }
                                    }
                                }
                            }
                        }
                        vehicle = spinner.getItemAtPosition(position).toString();
                        for (Vehicles vehicles : new ArrayList<Vehicles>(allVehicle)) {
                            if (vehicles.getVehiclesName().matches(vehicle)) {
                                if (vehicles.getMaxDistance() >= distance && vehicles.getTotalUnits() > 0) {
                                    b2 = true;
                                    selectedPlanet3.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.green));
                                    Vehicles updatedvehicle = vehicles;
                                    finalVehicle[mposition] = vehicle;
                                    finalVehiclesSpeed[mposition] = vehicles.getSpeed();
                                    updatedvehicle.updateTotalUnit(updatedvehicle.getTotalUnits() - 1);
                                    updatedvehicle.dispatchTo(selectedPlanets.get(mposition).getPlanetName());
                                    allVehicle.remove(vehicles);
                                    allVehicle.add(updatedvehicle);
                                } else {
                                    b2 = false;
                                    selectedPlanet3.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
                                }
                                break;
                            }
                            else
                            {
                                b2 = false;
                                selectedPlanet3.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
                            }
                        }
                        spinner.setTag(position);
                        spinner1.setEnabled(true);
                        spinner2.setEnabled(true);
                        spinner4.setEnabled(true);
                        break;

                    case 3:
                        spinner1.setEnabled(false);
                        spinner2.setEnabled(false);
                        spinner3.setEnabled(false);
                        distance = selectedPlanets.get(mposition).getDistance();
                        if(first4) {
                            first4 = false;
                            spinner.setTag(position);
                        }
                        else
                        {
                            int preselected = (Integer) spinner.getTag();
                            if(preselected!=0) {
                                vehicle = spinner.getItemAtPosition(preselected).toString();
                                for (Vehicles vehicles : new ArrayList<Vehicles>(allVehicle)) {
                                    if (vehicles.getVehiclesName().matches(vehicle)) {
                                        if (vehicles.totalDispatchedCounts()>0)
                                            if(vehicles.contains(selectedPlanets.get(mposition).toString())){
                                            Vehicles updatedvehicle = vehicles;
                                            updatedvehicle.updateTotalUnit(updatedvehicle.getTotalUnits() + 1);
                                            allVehicle.remove(vehicles);
                                            allVehicle.add(updatedvehicle);
                                        }
                                    }
                                }
                            }
                        }
                        vehicle = spinner.getItemAtPosition(position).toString();
                        for (Vehicles vehicles : new ArrayList<Vehicles>(allVehicle)) {
                            if (vehicles.getVehiclesName().matches(vehicle)) {
                                if (vehicles.getMaxDistance() >= distance && vehicles.getTotalUnits() > 0) {
                                    b3 = true;
                                    selectedPlanet4.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.green));
                                    Vehicles updatedvehicle = vehicles;
                                    finalVehicle[mposition] = vehicle;
                                    finalVehiclesSpeed[mposition] = vehicles.getSpeed();
                                    updatedvehicle.updateTotalUnit(updatedvehicle.getTotalUnits() - 1);
                                    updatedvehicle.dispatchTo(selectedPlanets.get(mposition).getPlanetName());
                                    allVehicle.remove(vehicles);
                                    allVehicle.add(updatedvehicle);
                                } else {
                                    b3 = false;
                                    selectedPlanet4.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
                                }
                                break;
                            }
                            else
                            {
                                b3 = false;
                                selectedPlanet4.setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
                            }
                        }
                        spinner.setTag(position);
                        spinner1.setEnabled(true);
                        spinner2.setEnabled(true);
                        spinner3.setEnabled(true);
                        break;
                }
                if(!first1 && !first2 && !first3 && !first4 && b0 && b1 && b2 && b3)
                {
                    if(huntBegin.getVisibility() == View.GONE)
                    huntBegin.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(huntBegin.getVisibility() == View.VISIBLE)
                        huntBegin.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onClick(View view)
    {
        Intent toFinalActivity = new Intent(Vehicles_Selection.this,Final_Activity.class);
        toFinalActivity.putExtra("selected_planets_names",planetsName);
        toFinalActivity.putExtra("selected_planets",selectedPlanets);
        toFinalActivity.putExtra("planet_distance",planetDistance);
        toFinalActivity.putExtra("dispatched_units_speed", finalVehiclesSpeed);
        toFinalActivity.putExtra("dispatched_unit",finalVehicle);
        startActivity(toFinalActivity);
    }


    private class GetVehicles extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://findfalcone.herokuapp.com/vehicles";
            String jsonStr = sh.makeServiceCall(url);

            if(!vehiclesName.isEmpty())
                vehiclesName.clear();

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray vehicles = new JSONArray(jsonStr);
                    for (int i = 0; i < vehicles.length(); i++) {
                        JSONObject obj = vehicles.getJSONObject(i);
                        String vehicleName = obj.getString("name");
                        vehiclesName.add(vehicleName);
                        int maxDistance = obj.getInt("max_distance");
                        int total_no = obj.getInt("total_no");
                        int speed = obj.getInt("speed");
                        Vehicles particularVehicle = new Vehicles(vehicleName, total_no, maxDistance, speed);
                        allVehicle.add(particularVehicle);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Fetching planetJSON Failed!!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            initSpinner(spinner1,0);
            initSpinner(spinner2,1);
            initSpinner(spinner3,2);
            initSpinner(spinner4,3);
            removeSimpleProgressDialog();

            VehicleAdapter adapter = new VehicleAdapter(Vehicles_Selection.this, allVehicle);
            listview.setAdapter(adapter);
        }
    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}