package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 7/09/2018.
 */

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class AddVehicleActivity extends AppCompatActivity
{
    ConstraintLayout dbToggle;
    ConstraintLayout cToggle;
    Switch dbSwitch;
    Switch cSwitch;
    ConstraintLayout dbMain;
    ConstraintLayout cMain;

    /**
     * Called when activity is created
     *
     * @param savedInstanceState, previous state if exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehicle_activity);

        dbToggle = findViewById(R.id.dbToggle);
        cToggle = findViewById(R.id.cToggle);
        dbSwitch = findViewById(R.id.dbSwitch);
        cSwitch = findViewById(R.id.cSwitch);
        dbMain = findViewById(R.id.dbMain);
        cMain = findViewById(R.id.cMain);

        dbSwitch.setChecked(true);
        cMain.setVisibility(View.GONE);

        dbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    dbMain.setVisibility(View.VISIBLE);
                    cSwitch.setChecked(false);
                }
                else
                {
                    dbMain.setVisibility(View.GONE);
                    cSwitch.setChecked(true);
                }
            }
        });

        cSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    cMain.setVisibility(View.VISIBLE);
                    dbSwitch.setChecked(false);
                }
                else
                {
                    cMain.setVisibility(View.GONE);
                    dbSwitch.setChecked(true);
                }
            }
        });

        dbToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dbSwitch.toggle();
            }
        });

        cToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                cSwitch.toggle();
            }
        });
    }

    /**
     * Overrides the toolbar back button
     * @return has to return true idk why
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
