package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 7/04/2018.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;

import static com.shiv.gastimate.Constants.animateTextView;

public class FuelActivity extends AppCompatActivity
{
    TextInputLayout textInputLayout;
    TextView currentSetPriceText;
    TextInputEditText priceInput;
    ConstraintLayout cToggle;
    ConstraintLayout cInput;
    Switch customFuelSwitch;
    TextView currentApiPriceText;

    FrameLayout progressBarLayout;

    double currentPriceAPI;
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

        progressBarLayout = findViewById(R.id.frameLayout);
        customFuelSwitch = findViewById(R.id.customFuelSwitch);
        textInputLayout = findViewById(R.id.textInputLayout);
        currentSetPriceText = findViewById(R.id.currentSetPrice);
        priceInput = findViewById(R.id.priceInput);
        cToggle = findViewById(R.id.customInputToggle);
        cInput = findViewById(R.id.customInputLayout);
        currentApiPriceText = findViewById(R.id.currentApiPrice);

        fuelAPI();

        cInput.setVisibility(View.GONE);
        customFuelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    cInput.setVisibility(View.VISIBLE);
                    try
                    {
                        currentSetPrice = Double.parseDouble(priceInput.getText().toString());
                    }
                    catch(NumberFormatException e)
                    {
                        currentSetPrice = currentPriceAPI;
                    }
                    setPriceText();
                }
                else
                {
                    cInput.setVisibility(View.GONE);
                    currentSetPrice = currentPriceAPI;
                    setPriceText();
                }
            }
        });

        cToggle.setOnClickListener(new View.OnClickListener()
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
                        Log.e("Enter Press Error", Arrays.toString(e.getStackTrace()));
                    }

                    currentSetPrice = Double.parseDouble(priceInput.getText().toString());
                    if(currentSetPrice > 20.0)
                    {
                        Toast.makeText(getApplicationContext(), "Price can't be more than 20 USD!", Toast.LENGTH_SHORT).show();
                        priceInput.setText("");
                        currentSetPrice = currentPriceAPI;
                    }
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

    /**
     * Gets the html code of the AAA website and sends to parse
     */
    @SuppressLint("DefaultLocale")
    void fuelAPI()
    {
        String url = "https://gasprices.aaa.com/";

        // Listener for response
        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(String response)
            {
                try
                {
                    parse(response);
                }
                catch(Exception e)
                {
                    Log.e("Fuel API", "Parsing Error");
                    Toast.makeText(getBaseContext(), "Parsing", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Listener for error
        Response.ErrorListener errorListener= new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError e)
            {
                Log.e("Fuel API", "Connection Error");
                Toast.makeText(getBaseContext(), "Connection", Toast.LENGTH_SHORT).show();
            }
        };

        //StringRequest which uses the method, url, responseListener and the errorListener
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);

        //Queue of the requests that are to be sent
        RequestQueue requestQueue = Volley.newRequestQueue(FuelActivity.this);

        //Adding the StringRequest to the queue so that it is sent
        requestQueue.add(stringRequest);
    }

    /**
     * Gets the gasprice from the html code of the AAA website
     * @param response, html code
     * @throws Exception, if cannot be passed, never happens most of the time
     */
    @SuppressLint("SetTextI18n")
    void parse(String response)
    {
        int index = response.indexOf("$");
        index++;
        currentPriceAPI = Double.parseDouble(response.substring(index, index+4));
        currentSetPrice = currentPriceAPI;
        animateTextView((float) 0.00, (float) currentSetPrice, currentApiPriceText);
        progressBarLayout.setVisibility(View.GONE);
        setPriceText();
    }

    /**
     * Called when set price button is checked
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    void setPriceText()
    {
        animateTextView(Float.parseFloat(currentSetPriceText.getText().toString()), (float) currentSetPrice, currentSetPriceText);
    }

    /**
     * Overrides the toolbar back button
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
