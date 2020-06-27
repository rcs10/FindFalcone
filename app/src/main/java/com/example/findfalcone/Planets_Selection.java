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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Planets_Selection extends AppCompatActivity {

    private static final String TAG = Planets_Selection.class.getSimpleName();
    private static ProgressDialog mProgressDialog;
    ArrayList<Planets> allPlanets = new ArrayList<>();
    ArrayList<Planets> temp;
    ArrayList<Planets> selectedPlanets = new ArrayList<>();
    private ListView listview;
    Boolean connected = true;
    Button letsgo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet_selection);
        connected = true;

        listview = (ListView) findViewById(R.id.listView);
        letsgo = (Button) findViewById(R.id.Next);

        letsgo.setOnClickListener(this::letsGoSelected);

        if(letsgo.getVisibility()== View.VISIBLE)
        letsgo.setVisibility(View.GONE);


        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if(connected) {
            new GetPlanets().execute();
            showSimpleProgressDialog(this, "Loading...", "Fetching Enemy Hideouts!!", false);
        }
        else
        {
            Toast.makeText(Planets_Selection.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void letsGoSelected(View view)
    {
        Intent toVehiclesSelection = new Intent(Planets_Selection.this,Vehicles_Selection.class);
        toVehiclesSelection.putExtra("selected_planets",selectedPlanets);
        startActivity(toVehiclesSelection);
    }

    private class GetPlanets extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://findfalcone.herokuapp.com/planets";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray planets = new JSONArray(jsonStr);
                    for (int i = 0; i < planets.length(); i++) {
                        JSONObject obj = planets.getJSONObject(i);
                        String planetName = obj.getString("name");
                        int planetDistance = obj.getInt("distance");
                        Planets particularPlanet = new Planets(planetName, planetDistance);
                        allPlanets.add(particularPlanet);
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

            PlanetAdapter adapter = new PlanetAdapter(Planets_Selection.this, allPlanets);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Planets clickedPlanet = (Planets) listview.getItemAtPosition(position);

                if(clickedPlanet.getSelectionStatus())
                {
                    clickedPlanet.setSelectionStatus(false);
                    selectedPlanets.remove(clickedPlanet);
                }
                else
                {
                    clickedPlanet.setSelectionStatus(true);
                    selectedPlanets.add(clickedPlanet);
                }

                if(selectedPlanets.size() == 4)
                {
                    letsgo.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(letsgo.getVisibility() == View.VISIBLE)
                        letsgo.setVisibility(View.GONE);
                }
                    temp = adapter.getPlanets();
                    temp.remove(position);
                    temp.add(position ,clickedPlanet);
                    adapter.setPlanets(temp);
                }
            });
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