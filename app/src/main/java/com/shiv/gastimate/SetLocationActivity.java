package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/25/2018.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class SetLocationActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_location_activity);

        Intent intent = getIntent();
        int locationType = intent.getIntExtra("locationType", 0);

        EditText locationInput = findViewById(R.id.locationInput);
        if(locationType == 0)
        {
            locationInput.setHint("Where from?");
        }
        else
        {
            locationInput.setHint("Where to?");
        }
    }
}
