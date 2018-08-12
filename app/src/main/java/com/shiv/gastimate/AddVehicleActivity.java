package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 7/09/2018.
 */

import android.annotation.SuppressLint;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

import static com.shiv.gastimate.Helper.CAR;
import static com.shiv.gastimate.Helper.MAKE;
import static com.shiv.gastimate.Helper.MODEL;
import static com.shiv.gastimate.Helper.NOT_TRACKING;
import static com.shiv.gastimate.Helper.TRIM;
import static com.shiv.gastimate.Helper.YEAR;
import static com.shiv.gastimate.Helper.isEditTextEmpty;
import static com.shiv.gastimate.Helper.showConfirmationPrompt;
import static com.shiv.gastimate.Helper.showPrompt;
import static com.shiv.gastimate.Helper.toggleVisibility;
import static com.shiv.gastimate.Helper.BooleanCallBack;
import static com.shiv.gastimate.Vehicle.addVehicle;

//TODO: Add loading bar for spinner loading

public class AddVehicleActivity extends AppCompatActivity
{
    ConstraintLayout dbToggle;
    ConstraintLayout cToggle;
    Switch dbSwitch;
    Switch cSwitch;
    ConstraintLayout dbMain;
    ConstraintLayout cMain;
    FloatingActionButton floatingActionButton;

    SearchableSpinner yearSpinner;
    SearchableSpinner makeSpinner;
    SearchableSpinner modelSpinner;
    SearchableSpinner trimSpinner;

    TextInputEditText nameInput;
    TextInputEditText makeInput;
    TextInputEditText modelInput;
    TextInputEditText yearInput;
    TextInputEditText mpgInput;
    TextInputEditText capacityInput;

    int[] yearsLimit = new int[2];
    ArrayList<String> years = new ArrayList<>();
    ArrayList<String> makes = new ArrayList<>();
    ArrayList<String> models = new ArrayList<>();
    ArrayList<String> trimDisplays = new ArrayList<>();

    ImageView imageType;
    ImageView imageCar;
    ImageView imageMotorcycle;
    ImageView imageOther;
    ConstraintLayout typeSelectLayout;

    int year;
    String make;
    String model;
    double mpg;
    double capacity;
    int type = 0;

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
        floatingActionButton = findViewById(R.id.floatingActionButton);

        yearSpinner = findViewById(R.id.yearSpinner);
        yearSpinner.setVisibility(View.GONE);
        makeSpinner = findViewById(R.id.makeSpinner);
        makeSpinner.setVisibility(View.GONE);
        modelSpinner = findViewById(R.id.modelSpinner);
        modelSpinner.setVisibility(View.GONE);
        trimSpinner = findViewById(R.id.trimSpinner);
        trimSpinner.setVisibility(View.GONE);

        nameInput = findViewById(R.id.nameInput);
        makeInput = findViewById(R.id.makeInput);
        modelInput = findViewById(R.id.modelInput);
        yearInput = findViewById(R.id.yearInput);
        mpgInput = findViewById(R.id.mpgInput);
        capacityInput = findViewById(R.id.capacityInput);

        imageType = findViewById(R.id.imageType);
        imageCar = findViewById(R.id.imageCar);
        imageMotorcycle = findViewById(R.id.imageMotorcycle);
        imageOther = findViewById(R.id.imageOther);
        typeSelectLayout = findViewById(R.id.typeSelectLayout);

        floatingActionButton.hide();    //Because we have yet to select from the spinners
        dbSwitch.setChecked(true);      //Because DBMain is visible

        setUiToggleListeners();
        setSpinnerListeners();
        setTypeSelectListeners();

        carQueryAPI(YEAR);

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(dbSwitch.isChecked())
                {
                    spinnerToEdit();
                    dbSwitch.toggle();
                }
                else
                {
                    addCar();
                }
            }
        });
    }

    /**
     * Handles the listeners and toggles for DB and Custom cards
     */
    void setUiToggleListeners()
    {
        dbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    dbMain.setVisibility(View.VISIBLE);
                    cSwitch.setChecked(false);
                    floatingActionButton.setImageResource(R.drawable.ic_arrow_forward);
                }
                else
                {
                    dbMain.setVisibility(View.GONE);
                    cSwitch.setChecked(true);
                    floatingActionButton.setImageResource(R.drawable.ic_check);
                }
                fabHandler();
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
                fabHandler();
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
     * Handles the listeners and toggles for the DB spinners
     */
    void setSpinnerListeners()
    {
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                makeSpinner.setVisibility(View.GONE);
                modelSpinner.setVisibility(View.GONE);
                trimSpinner.setVisibility(View.GONE);
                fabHandler();

                if(!(yearSpinner.getSelectedItem().toString()).equals("Set year"))
                {
                    year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                    carQueryAPI(MAKE);
                }

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
                fabHandler();

                if(!(makeSpinner.getSelectedItem().toString()).equals("Set make"))
                {
                    make = makeSpinner.getSelectedItem().toString();
                    carQueryAPI(MODEL);
                }
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
                fabHandler();

                if(!(modelSpinner.getSelectedItem().toString()).equals("Set model"))
                {
                    model = modelSpinner.getSelectedItem().toString();
                    carQueryAPI(TRIM);
                }
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
                if(!(trimSpinner.getSelectedItem().toString()).equals("Set variant"))
                {
                    JSONObject trimObject = null;
                    try
                    {
                        trimObject = trimsArray.getJSONObject(trimSpinner.getSelectedItemPosition() - 1);     //-1 cause of Select Variant selection

                        try
                        {
                            mpg = (2.35214583 * trimObject.getDouble("model_lkm_mixed"));       //km per litres into miles per gallon
                        }
                        catch(JSONException e)
                        {
                            Log.e("Spinner Parsing", "MPG Error " + e.getMessage());
                            mpg = 0.0;
                        }

                        try
                        {
                            capacity = (0.264172 * trimObject.getDouble("model_fuel_cap_l"));   //litres to gallons
                        }
                        catch(JSONException e)
                        {
                            Log.e("Spinner Parsing", "Capacity Error " + e.getMessage());
                            capacity = 0.0;
                        }
                    }
                    catch(JSONException e)
                    {
                        Log.e("Spinner Parsing", "Trim Error " + e.getMessage());
                        mpg = 0.0;
                        capacity = 0.0;
                    }

                    fabHandler();
                }
                else
                {
                    fabHandler();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }

    /**
     * Handles the listeners and toggles for the type selector in Custom card
     */
    void setTypeSelectListeners()
    {
        imageType.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                toggleVisibility(typeSelectLayout);
            }
        });

        imageCar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = 0;
                typeSelectLayout.setVisibility(View.GONE);
                imageType.setImageResource(R.drawable.ic_car);
            }
        });

        imageMotorcycle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = 1;
                typeSelectLayout.setVisibility(View.GONE);
                imageType.setImageResource(R.drawable.ic_motorcycle);
            }
        });

        imageOther.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = 2;
                typeSelectLayout.setVisibility(View.GONE);
                imageType.setImageResource(R.drawable.ic_bus);
            }
        });
    }

    /**
     * Gets the results from the carQueryAPI
     *
     * @param type, tells if year, make, model or trim, check Helper
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
        Log.i("Volley URL: ", url.toString());
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
        years.add("Set year");
        JSONObject mainObject = new JSONObject(response);
        JSONObject yearsObject = mainObject.getJSONObject("Years");
        yearsLimit[0] = yearsObject.getInt("min_year");
        yearsLimit[1] = yearsObject.getInt("max_year");

        for(int i = yearsLimit[1]; i >= yearsLimit[0]; i--)
        {
            years.add("" + i);
        }

        ArrayAdapter<String> yearsSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearSpinner.setAdapter(yearsSpinnerAdapter);
        yearSpinner.setVisibility(View.VISIBLE);
        yearSpinner.setSelection(0, false);

        makeSpinner.setVisibility(View.GONE);
        modelSpinner.setVisibility(View.GONE);
        trimSpinner.setVisibility(View.GONE);
        fabHandler();
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
        makes.add("Set make");
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
        makeSpinner.setSelection(0, false);

        modelSpinner.setVisibility(View.GONE);
        trimSpinner.setVisibility(View.GONE);
        fabHandler();
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
        models.add("Set model");
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
        modelSpinner.setSelection(0, false);

        trimSpinner.setVisibility(View.GONE);
        fabHandler();
    }

    JSONArray trimsArray;       //Outside function because used later to get trim and mpg value

    /**
     * Gets the range of the models available
     *
     * @param response, response with the data
     * @throws JSONException, if can't be parsed
     */
    void parseTrims(String response) throws JSONException
    {
        trimDisplays.clear();
        trimDisplays.add("Set variant");
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
        trimSpinner.setSelection(0, false);

        fabHandler();
    }

    /**
     * Adds vehicle to database
     */
    @SuppressLint("SetTextI18n")
    void addCar()
    {
        if(isEditTextEmpty(nameInput) || isEditTextEmpty(makeInput) || isEditTextEmpty(modelInput)
                || isEditTextEmpty(yearInput) || isEditTextEmpty(mpgInput) || isEditTextEmpty(capacityInput))
        {
            showPrompt(AddVehicleActivity.this, "Fields not set!", "One or more fields are empty!\nPlease fill them.", true, null);
        }
        else
        {
            if(Double.parseDouble(mpgInput.getText().toString()) == 0.0)
            {
                showPrompt(AddVehicleActivity.this, "MPG is is zero", "If you set the MPG as 0, the app can't track the " +
                        "gas needed for any trip where this vehicle is used.\nPlease set the MPG value manually.", false, null);
            }
            else
            {
                final Vehicle v = new Vehicle(nameInput.getText().toString(), makeInput.getText().toString(),
                        modelInput.getText().toString(), Integer.parseInt(yearInput.getText().toString()),
                        Double.parseDouble(mpgInput.getText().toString()), Double.parseDouble(capacityInput.getText().toString()),
                        NOT_TRACKING, 0, type);
                Log.i("VehicleC Created", v.toString());

                showConfirmationPrompt(AddVehicleActivity.this, null, "Add Vehicle?", false, new BooleanCallBack()
                {
                    @Override
                    public void execute(boolean confirm)
                    {
                        if(confirm)
                        {

                            addVehicle(AddVehicleActivity.this, v, new BooleanCallBack()
                            {
                                @Override
                                public void execute(boolean added)
                                {
                                    if(added)
                                    {
                                        MainActivity.dbChanged = true;
                                        onBackPressed();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    /**
     * Handles the visibility of the floatingActionButton
     */
    void fabHandler()
    {
        if(dbSwitch.isChecked())
        {
            if((trimSpinner.getVisibility() == View.VISIBLE) && (trimSpinner.getSelectedItemPosition() != 0))
            {
                floatingActionButton.show();
            }
            else
            {
                floatingActionButton.hide();
            }
        }
        //cSwitch.isChecked() is true if dbSwitch.isChecked() is not
        else
        {
            floatingActionButton.show();
        }
    }

    /**
     * Sends the values from spinner to the edit text
     */
    void spinnerToEdit()
    {
        makeInput.setText(makeSpinner.getSelectedItem().toString());
        modelInput.setText(modelSpinner.getSelectedItem().toString());
        yearInput.setText(yearSpinner.getSelectedItem().toString());
        mpgInput.setText("" + mpg);
        capacityInput.setText("" + capacity);
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

    /**
     * Prevent mishandling of the back button
     */
    @Override
    public void onBackPressed()
    {
        finish();
    }
}
