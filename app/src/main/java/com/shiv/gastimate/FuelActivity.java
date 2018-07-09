package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 7/04/2018.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class FuelActivity extends AppCompatActivity
{
    TextInputLayout textInputLayout;
    TextView currentSetPriceText;
    EditText priceInput;
    TextView customFuelPriceText;

    double currentPriceAPI = 3.47;
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
        currentSetPriceText = findViewById(R.id.currentSetPrice);
        priceInput = findViewById(R.id.priceInput);
        customFuelPriceText = findViewById(R.id.customFuelPriceText);

        currentSetPrice = currentPriceAPI;
        setPriceText();

        customFuelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    textInputLayout.setVisibility(View.VISIBLE);
                    try
                    {
                        currentSetPrice = Double.parseDouble(priceInput.getText().toString());
                    }
                    catch(Exception e)
                    {
                        currentSetPrice = currentPriceAPI;
                    }
                    setPriceText();
                }
                else
                {
                    textInputLayout.setVisibility(View.GONE);
                    currentSetPrice = currentPriceAPI;
                    setPriceText();
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

        priceInput.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                // If the event is a key-down event on the "Enter" key
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    try     //hides the keyboard on enter press
                    {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }catch(Exception e)
                    {
                        Log.e("Enter Press", Arrays.toString(e.getStackTrace()));
                    }

                    currentSetPrice = Double.parseDouble(priceInput.getText().toString());
                    setPriceText();

                    return true;
                }
                return false;
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
