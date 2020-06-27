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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Vehicles_Selection extends AppCompatActivity {

    Boolean connected = true;
    private static ProgressDialog mProgressDialog;
    private static final String TAG = Vehicles_Selection.class.getSimpleName();
    ArrayList<Vehicles> allVehicle = new ArrayList<>();
    ArrayList<String> vehiclesName = new ArrayList<>();
    ArrayList<String> planetsName = new ArrayList<>();
    ArrayList<Planets> selectedPlanets = new ArrayList<>();
    TextView selectedPlanet1, selectedPlanet2, selectedPlanet3, selectedPlanet4;
    Spinner spinner1, spinner2, spinner3, spinner4;
    ArrayList<TextView> views = new ArrayList<>();
    private ListView listview;
    Button huntBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles_selection);
        connected = true;

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
            views.get(i).setText(selectedPlanets.get(i).getPlanetName());
            views.get(i).setTextColor(Vehicles_Selection.this.getResources().getColor(R.color.red));
        }

        listview = (ListView) findViewById(R.id.vehicleListView);
        huntBegin = (Button) findViewById(R.id.huntBegin);
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

            initSpinner(spinner1,Vehicles_Selection.this);
            initSpinner(spinner2,Vehicles_Selection.this);
            initSpinner(spinner3,Vehicles_Selection.this);
            initSpinner(spinner4,Vehicles_Selection.this);

        }
        else
        {
            Toast.makeText(Vehicles_Selection.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View view)
    {
        Intent toFinalActivity = new Intent(Vehicles_Selection.this,Final_Activity.class);
        toFinalActivity.putExtra("selected_planets",planetsName);
        toFinalActivity.putExtra("dispatched_units",vehiclesName);
        startActivity(toFinalActivity);
    }

    private void initSpinner(Spinner spinner, Context context)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, (List) vehiclesName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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