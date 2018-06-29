package com.shiv.gastimate;

import android.annotation.SuppressLint;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class FuelActivity extends AppCompatActivity
{
    TextInputLayout textInputLayout;
    Button setPriceButton;
    TextView currentSetPriceText;
    EditText priceInput;

    double currentSetPrice;

    /**
     * Called when activity is created
     * @param savedInstanceState, previous state if exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuel_activity);

        Switch customFuelSwitch = findViewById(R.id.customFuelSwitch);
        textInputLayout = findViewById(R.id.textInputLayout);
        setPriceButton = findViewById(R.id.setPriceButton);
        currentSetPriceText = findViewById(R.id.currentSetPrice);
        priceInput = findViewById(R.id.priceInput);

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

        setPriceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setPriceText();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    void setPriceText()
    {
        currentSetPrice = Double.parseDouble(priceInput.getText().toString());
        if(currentSetPrice < 100.00)
        {
            currentSetPriceText.setText(String.format("%2.2f", currentSetPrice));
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
