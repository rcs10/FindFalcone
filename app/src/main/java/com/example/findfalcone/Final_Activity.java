package com.example.findfalcone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Final_Activity extends AppCompatActivity {

    private static final String TAG = Final_Activity.class.getSimpleName();
    private static ProgressDialog mProgressDialog;
    Boolean connected = true;
    ArrayList<String> planetsName = new ArrayList<>();
    ArrayList<Integer> planetDistance;
    ArrayList<Planets> selectedPlanets ;
    int[] vehiclesSpeed;
    String[] vehiclesName;
    ArrayList<String> finalAnswer = new ArrayList<>();
    TextView finalMessagePos;
    TextView finalMessageNeg;
    NestedScrollView posScrollView;
    TextView planetFound;
    TextView timeTaken;
    Button finalBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_);

        finalMessagePos = (TextView) findViewById(R.id.final_message_pos);
        finalMessageNeg = (TextView) findViewById(R.id.final_message_neg);
        posScrollView = (NestedScrollView) findViewById(R.id.finalScrollView);
        planetFound = (TextView) findViewById(R.id.final_planet_name);
        timeTaken = (TextView) findViewById(R.id.final_time_taken);
        finalBtn = (Button) findViewById(R.id.finalButton);

        finalBtn.setOnClickListener(this::OnClick);
        finalMessagePos.setVisibility(View.GONE);
        finalMessageNeg.setVisibility(View.GONE);
        posScrollView.setVisibility(View.GONE);


        planetsName = (ArrayList<String>) getIntent().getSerializableExtra("selected_planets_names");
        selectedPlanets = (ArrayList<Planets>)getIntent().getSerializableExtra("selected_planets");
        planetDistance = (ArrayList<Integer>)getIntent().getSerializableExtra("planet_distance");

        vehiclesSpeed = getIntent().getIntArrayExtra("dispatched_units_speed");
        vehiclesName = getIntent().getStringArrayExtra("dispatched_unit");

        int time = 0;
        for(int i = 0 ; i < 4 ; i++)
        {
            time+= planetDistance.get(i)/vehiclesSpeed[i] ;
        }
        timeTaken.setText(String.valueOf(time)+" hours");

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if(connected) {
            new letTheHuntBegin().execute();
            showSimpleProgressDialog(this, "Loading...", "Hunting Al Falcone....", false);
        }
        else
        {
            Toast.makeText(Final_Activity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void OnClick(View v)
    {
        Intent toMainActivity = new Intent(Final_Activity.this,MainActivity.class);
        startActivity(toMainActivity);
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

    private class letTheHuntBegin extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... arg0) {
            String data = "";
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://findfalcone.herokuapp.com/token";
            String jsonStr = sh.makeServiceCall(url, "POST");

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    // Extracting Token
                    JSONObject tokenObject = new JSONObject(jsonStr);
                    String token = tokenObject.getString("token");

                    String findUrl = "https://findfalcone.herokuapp.com/find";

                    JSONObject postObject = new JSONObject();
                    JSONArray planetArray = new JSONArray();
                    JSONArray vehicleArray = new JSONArray();

                    try {

                        planetArray.put(planetsName.get(0));
                        planetArray.put(planetsName.get(1));
                        planetArray.put(planetsName.get(2));
                        planetArray.put(planetsName.get(3));

                        vehicleArray.put(vehiclesName[0]);
                        vehicleArray.put(vehiclesName[1]);
                        vehicleArray.put(vehiclesName[2]);
                        vehicleArray.put(vehiclesName[3]);

                        postObject.put("token", token);
                        postObject.put("planet_names", planetArray);
                        postObject.put("vehicle_names", vehicleArray);


                        HttpURLConnection httpURLConnection = null;
                        try {

                            httpURLConnection = (HttpURLConnection) new URL(findUrl).openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setRequestProperty("Accept", "application/json");
                            httpURLConnection.setRequestProperty("Content-Type", "application/json");
                            httpURLConnection.setDoOutput(true);
                            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                            wr.writeBytes(postObject.toString());
                            Log.e(TAG, "doInBackground: sent object is " + postObject.toString());
                            wr.flush();
                            wr.close();

                            InputStream in = httpURLConnection.getInputStream();
                            InputStreamReader inputStreamReader = new InputStreamReader(in);

                            int inputStreamData = inputStreamReader.read();
                            while (inputStreamData != -1) {
                                char current = (char) inputStreamData;
                                inputStreamData = inputStreamReader.read();
                                data += current;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                        }

                        return data;

                    } catch (JSONException e) {
                        e.printStackTrace();
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
                                "Fetching Token Failed!!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e(TAG, "onPostExecute: response from server is " + result);

            if (result != null) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.has("error")) {
                        String error = resObj.getString("error");
                        Log.e(TAG, "Error is " + error);
                        removeSimpleProgressDialog();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Final_Activity.this, "Token Failed!! Try Again", Toast.LENGTH_LONG).show();
                                Intent backToVehiclesSelection = new Intent(Final_Activity.this,Vehicles_Selection.class);
                                backToVehiclesSelection.putExtra("selected_planets",selectedPlanets);
                                startActivity(backToVehiclesSelection);
                            }
                        });
                    } else {
                        String status = resObj.getString("status");
                        if(status.equals("false"))
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(finalMessagePos.getVisibility() == View.VISIBLE)
                                        finalMessagePos.setVisibility(View.GONE);
                                    if(posScrollView.getVisibility() == View.VISIBLE)
                                        finalMessagePos.setVisibility(View.GONE);

                                    finalMessageNeg.setVisibility(View.VISIBLE);
                                    planetFound.setText("N/A");
                                }
                            });
                        }
                        else
                        {
                            String planetFoundText = resObj.getString("planet_name");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(finalMessageNeg.getVisibility() == View.VISIBLE)
                                        finalMessageNeg.setVisibility(View.GONE);

                                    finalMessagePos.setVisibility(View.VISIBLE);
                                    posScrollView.setVisibility(View.VISIBLE);
                                    planetFound.setText(planetFoundText);
                                }
                            });
                        }
                        removeSimpleProgressDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    removeSimpleProgressDialog();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Final_Activity.this, "Unknown Error Occurred Try Again ", Toast.LENGTH_LONG).show();
                            Intent backToVehiclesSelection = new Intent(Final_Activity.this,Vehicles_Selection.class);
                            backToVehiclesSelection.putExtra("selected_planets",selectedPlanets);
                            startActivity(backToVehiclesSelection);
                        }
                    });
                }
            }
        }
    }
}