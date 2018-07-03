package com.shiv.gastimate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class FuelActivity extends AppCompatActivity
{
    TextInputLayout textInputLayout;
    Button setPriceButton;
    TextView currentSetPriceText;
    EditText priceInput;
    TextView customFuelPriceText;
    TextView textView;

    public static double currentSetPrice;

    /**
     * Called when activity is created
     * @param savedInstanceState, previous state if exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuel_activity);

        final Switch customFuelSwitch = findViewById(R.id.customFuelSwitch);
        textInputLayout = findViewById(R.id.textInputLayout);
        setPriceButton = findViewById(R.id.setPriceButton);
        currentSetPriceText = findViewById(R.id.currentSetPrice);
        priceInput = findViewById(R.id.priceInput);
        customFuelPriceText = findViewById(R.id.customFuelPriceText);
        textView = findViewById(R.id.textView);

        Vehicle vehicle = MainActivity.currentVehicle;
        textView.setText(vehicle.toString());

        customFuelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            //TODO: After implementing API, toggle between custom and api fuel price here too
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    textInputLayout.setVisibility(View.VISIBLE);
                    setPriceButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    textInputLayout.setVisibility(View.GONE);
                    setPriceButton.setVisibility(View.GONE);
                }
            }
        });

        customFuelPriceText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                customFuelSwitch.toggle();
            }
        });

        setPriceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    setPriceText();
                }catch(Exception e) {}
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(FuelActivity.this, Gastimate.class);
                startActivity(intent);
            }
        });
    }

    //Called when set price button is checked
    @SuppressLint("DefaultLocale")
    void setPriceText()
    {
        currentSetPrice = Double.parseDouble(priceInput.getText().toString());
        if(currentSetPrice <= 100.00)
        {
            currentSetPriceText.setText(String.format("%2.2f", currentSetPrice));
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Price can't be more than 100 USD!", Toast.LENGTH_SHORT).show();
            priceInput.setText("");
        }
    }

    //Overrides the toolbar back button
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
