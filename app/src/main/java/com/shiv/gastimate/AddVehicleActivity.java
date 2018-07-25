package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 7/09/2018.
 */

import android.annotation.SuppressLint;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.shiv.gastimate.Constants.CAR;
import static com.shiv.gastimate.Constants.MAKE;
import static com.shiv.gastimate.Constants.MODEL;
import static com.shiv.gastimate.Constants.NOT_TRACKING;
import static com.shiv.gastimate.Constants.TRIM;
import static com.shiv.gastimate.Constants.YEAR;
import static com.shiv.gastimate.MainActivity.vehicles;

public class AddVehicleActivity extends AppCompatActivity
{
    ConstraintLayout dbToggle;
    ConstraintLayout cToggle;
    Switch dbSwitch;
    Switch cSwitch;
    ConstraintLayout dbMain;
    ConstraintLayout cMain;

    TextInputEditText vehicleNameDB;
    SearchableSpinner yearSpinner;
    SearchableSpinner makeSpinner;
    SearchableSpinner modelSpinner;
    SearchableSpinner trimSpinner;

    int[] yearsLimit = new int[2];
    ArrayList<Integer> years = new ArrayList<>();
    ArrayList<String> makes = new ArrayList<>();
    ArrayList<String> models = new ArrayList<>();
    ArrayList<String> trimDisplays = new ArrayList<>();

    int year;
    String make;
    String model;
    String trim;
    double mpg;
    double capacity;

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

        vehicleNameDB = findViewById(R.id.vehicleNameDB);
        yearSpinner = findViewById(R.id.yearSpinner);
        yearSpinner.setVisibility(View.GONE);
        makeSpinner = findViewById(R.id.makeSpinner);
        makeSpinner.setVisibility(View.GONE);
        modelSpinner = findViewById(R.id.modelSpinner);
        modelSpinner.setVisibility(View.GONE);
        trimSpinner = findViewById(R.id.trimSpinner);
        trimSpinner.setVisibility(View.GONE);

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

        carQueryAPI(YEAR);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                makeSpinner.setVisibility(View.GONE);
                modelSpinner.setVisibility(View.GONE);
                trimSpinner.setVisibility(View.GONE);

                year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                carQueryAPI(MAKE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                modelSpinner.setVisibility(View.GONE);
                trimSpinner.setVisibility(View.GONE);

                make = makeSpinner.getSelectedItem().toString();
                carQueryAPI(MODEL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                trimSpinner.setVisibility(View.GONE);

                model = modelSpinner.getSelectedItem().toString();
                carQueryAPI(TRIM);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        trimSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                try
                {
                    JSONObject trimObject = trimsArray.getJSONObject(trimSpinner.getSelectedItemPosition());
                    trim = trimObject.getString("model_trim");
                    mpg = (2.35214583 * trimObject.getDouble("model_lkm_mixed"));       //km per litres into miles per gallon
                    capacity = (0.264172 * trimObject.getDouble("model_fuel_cap_l"));   //litres to gallons
                }
                catch(JSONException e)
                {
                    trim = null;
                    mpg = 0;
                    Log.e("Spinner Parsing", e.getMessage());
                }

                Vehicle v = new Vehicle(vehicleNameDB.getText().toString(),  make, model, year, mpg,capacity, NOT_TRACKING, 0, CAR);
                Log.i("Vehicle Created", v.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });


    }

    /**
     * Gets the results from the carQueryAPI
     *
     * @param type, tells if year, make, model or trim, check Constants
     */
    void carQueryAPI(final int type)
    {
        StringBuilder url = new StringBuilder(getResources().getString(R.string.car_query_url));

        if(type == YEAR)
        {
            url.append("&cmd=getYears");
        }
        else if(type == MAKE)
        {
            url.append("&cmd=getMakes&year=").append(year);
        }
        else if(type == MODEL)
        {
            url.append("&cmd=getModels&make=").append(make.replace(" ", "-"))
                    .append("&year=").append(year);
        }
        else if(type == TRIM)
        {
            url.append("&cmd=getTrims&year=").append(year).append("&make=")
                    .append(make.replace(" ", "-")).append("&model=").append(model.replace(" ", "%20"));
        }

        // Listener for response
        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(String response)
            {
                try
                {
                    response = response.substring(response.indexOf("{"), (response.lastIndexOf("}") + 1));    //start is inclusive, end is exclusive
                    if(type == YEAR)
                    {
                        parseYears(response);
                    }
                    else if(type == MAKE)
                    {
                        parseMakes(response);
                    }
                    else if(type == MODEL)
                    {
                        parseModels(response);
                    }
                    else if(type == TRIM)
                    {
                        parseTrims(response);
                    }
                }
                catch(JSONException e)
                {
                    Log.e("CarQuery " + type, "Parsing Error: " + e.getMessage());
                }
            }
        };

        // Listener for error
        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError e)
            {
                Log.e("CarQuery " + type, "Connection Error " + e.getMessage());
                Toast.makeText(getBaseContext(), "Connection", Toast.LENGTH_SHORT).show();

            }
        };

        //StringRequest which uses the method, url, responseListener and the errorListener
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(), responseListener, errorListener);

        //Queue of the requests that are to be sent
        RequestQueue requestQueue = Volley.newRequestQueue(AddVehicleActivity.this);

        //Adding the StringRequest to the queue so that it is sent
        requestQueue.add(stringRequest);
        Log.i("Volley URL: " , url.toString());
    }

    /**
     * Gets the range for the years available
     *
     * @param response, response with the data
     * @throws JSONException, if can't be parsed
     */
    void parseYears(String response) throws JSONException
    {
        years.clear();
        JSONObject mainObject = new JSONObject(response);
        JSONObject yearsObject = mainObject.getJSONObject("Years");
        yearsLimit[0] = yearsObject.getInt("min_year");
        yearsLimit[1] = yearsObject.getInt("max_year");

        for(int i = yearsLimit[1]; i >= yearsLimit[0]; i--)
        {
            years.add(i);
        }

        ArrayAdapter<Integer> yearsSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearSpinner.setAdapter(yearsSpinnerAdapter);
        yearSpinner.setVisibility(View.VISIBLE);

        makeSpinner.setVisibility(View.GONE);
        modelSpinner.setVisibility(View.GONE);
        trimSpinner.setVisibility(View.GONE);
    }

    /**
     * Gets the range of the makes available
     *
     * @param response, response with the data
     * @throws JSONException, if can't be parsed
     */
    void parseMakes(String response) throws JSONException
    {
        makes.clear();
        JSONObject mainObject = new JSONObject(response);
        JSONArray makesArray = mainObject.getJSONArray("Makes");
        for(int i = 0; i < makesArray.length(); i++)
        {
            JSONObject makeObject = makesArray.getJSONObject(i);
            makes.add(makeObject.getString("make_display"));
        }

        ArrayAdapter<String> makesSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, makes);
        makeSpinner.setAdapter(makesSpinnerAdapter);
        makeSpinner.setVisibility(View.VISIBLE);

        modelSpinner.setVisibility(View.GONE);
        trimSpinner.setVisibility(View.GONE);
    }

    /**
     * Gets the range of the models available
     *
     * @param response, response with the data
     * @throws JSONException, if can't be parsed
     */
    void parseModels(String response) throws JSONException
    {
        models.clear();
        JSONObject mainObject = new JSONObject(response);
        JSONArray modelsArray = mainObject.getJSONArray("Models");
        for(int i = 0; i < modelsArray.length(); i++)
        {
            JSONObject modelObject = modelsArray.getJSONObject(i);
            models.add(modelObject.getString("model_name"));
        }

        ArrayAdapter<String> modelsSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
        modelSpinner.setAdapter(modelsSpinnerAdapter);
        modelSpinner.setVisibility(View.VISIBLE);

        trimSpinner.setVisibility(View.GONE);
    }

    //Outside function because used later to get trim and mpg value
    JSONArray trimsArray;
    /**
     * Gets the range of the models available
     *
     * @param response, response with the data
     * @throws JSONException, if can't be parsed
     */
    void parseTrims(String response) throws JSONException
    {
        trimDisplays.clear();
        JSONObject mainObject = new JSONObject(response);
        trimsArray = mainObject.getJSONArray("Trims");
        for(int i = 0; i < trimsArray.length(); i++)
        {
            JSONObject trimObject = trimsArray.getJSONObject(i);
            trimDisplays.add(String.format("%s %s %s %s",
                    trimObject.getString("make_display"), trimObject.getString("model_name"),
                    trimObject.getString("model_year"), trimObject.getString("model_trim")));
        }

        ArrayAdapter<String> trimSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trimDisplays);
        trimSpinner.setAdapter(trimSpinnerAdapter);
        trimSpinner.setVisibility(View.VISIBLE);
    }

    /**
     * Overrides the toolbar back button
     *
     * @return has to return true idk why
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
