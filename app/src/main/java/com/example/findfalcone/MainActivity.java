package com.example.findfalcone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button decision = (Button) findViewById(R.id.decision);
        decision.setOnClickListener(this::decisionClicked);

    }

    public void decisionClicked(View v)
    {
        Intent toPlanetsSelection = new Intent(MainActivity.this, Planets_Selection.class);
        startActivity(toPlanetsSelection);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}