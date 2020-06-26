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
        Intent resourceallocation = new Intent(MainActivity.this,resource_allocation.class);
        startActivity(resourceallocation);
    }
}