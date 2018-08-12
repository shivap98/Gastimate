package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/27/2018.
 */

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

public class Helper
{

    //used for location and location search activities
    public static final int FROM_LOCATION_REQUEST = 0;
    public static final int TO_LOCATION_REQUEST = 1;

    //used to identify vehicle type
    public static final int CAR = 0;
    public static final int MOTORCYCLE = 1;
    public static final int OTHER = 2;

    //used to see if vehicle fuel is being tracked or not
    public static final int TRACKING = 1;
    public static final int NOT_TRACKING = 0;

    //Duration for ValueAnimator
    static final int animationDuration = 1170;

    //used for carQuery type
    public static int YEAR = 0;
    public static int MAKE = 1;
    public static int MODEL = 2;
    public static int TRIM = 3;

    /**
     * Animates the given TextView
     *
     * @param initialValue, the starting value of animation
     * @param finalValue,   the ending value of animation
     * @param textView,     the TextView to be animated
     */
    @SuppressLint("DefaultLocale")
    public static void animateTextView(float initialValue, float finalValue, final TextView textView)
    {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(initialValue, finalValue);
        valueAnimator.setDuration(animationDuration);
        valueAnimator.setInterpolator(new DecelerateInterpolator((float) 10));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                textView.setText(String.format("%2.2f", (Float) valueAnimator.getAnimatedValue()));
            }
        });
        valueAnimator.start();
    }

    /**
     * @return true if editText is empty, false if otherwise
     */
    public static boolean isEditTextEmpty(EditText editText)
    {
        try
        {
            return TextUtils.isEmpty(editText.getText().toString());
        }
        catch(NullPointerException e)
        {
            return true;
        }
    }

    /**
     * Shows prompt with an input field based on preferences
     *
     * @param context,        where the dialog is shown
     * @param title,          dialog title
     * @param description,    dialog description
     * @param hint,           input text hint
     * @param cancelable,     doh
     * @param numberInput,    doh
     * @param stringCallBack, object with the function to be called after hitting ok
     */
    public static void showInputPrompt(Context context, String title, String description, String hint, boolean cancelable, boolean numberInput, final StringCallBack stringCallBack)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.input_prompt, null);
        final TextInputEditText input;
        if(numberInput)
        {
            input = promptsView.findViewById(R.id.inputNumbers);
            TextInputLayout toHide = promptsView.findViewById(R.id.inputTextLayout);
            toHide.setVisibility(View.GONE);
        }
        else
        {
            input = promptsView.findViewById(R.id.inputText);
            TextInputLayout toHide = promptsView.findViewById(R.id.inputNumberLayout);
            toHide.setVisibility(View.GONE);
        }
        input.setHint(hint);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if(title != null)
        {
            alertDialogBuilder.setTitle(title);
        }
        alertDialogBuilder
                .setView(promptsView)
                .setMessage(description)
                .setCancelable(cancelable)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                stringCallBack.execute(input.getText().toString());
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Shows a yes/no prompt based on preferences
     *
     * @param context,         where the dialog is shown
     * @param title,           dialog title
     * @param description,     dialog description
     * @param cancelable,      doh
     * @param booleanCallBack, object with the function to be called after pressing the buttons
     */
    public static void showConfirmationPrompt(Context context, String title, String description, boolean cancelable, final BooleanCallBack booleanCallBack)
    {
        //Creating alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if(title != null)
        {
            alertDialogBuilder.setTitle(title);
        }
        alertDialogBuilder.setMessage(description);
        alertDialogBuilder.setCancelable(cancelable);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                booleanCallBack.execute(true);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                booleanCallBack.execute(false);
            }
        });
        if(cancelable)
        {
            alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialogInterface)
                {
                    booleanCallBack.execute(false);
                }
            });
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Shows dialog with simple title and message
     *
     * @param context, where th dialog is displayed
     * @param title,   title of dialog
     * @param message, message of dialog
     */
    public static void showPrompt(Context context, String title, String message, boolean cancelable, final VoidCallBack voidCallBack)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        if(title != null)
        {
            alert.setTitle(title);
        }
        alert.setMessage(message);
        alert.setCancelable(cancelable);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(voidCallBack != null)
                {
                    voidCallBack.execute();
                }
            }
        });
        try
        {
            alert.show();
        }
        catch(WindowManager.BadTokenException e)    //Called when Gastimate activity is closed
        {
        }
    }

    /**
     * Toggles visibility
     *
     * @param view, view whose visibility needs to be toggled
     */
    public static void toggleVisibility(View view)
    {
        if(view.getVisibility() == View.VISIBLE)
        {
            view.setVisibility(View.GONE);
        }
        else if(view.getVisibility() == View.GONE)
        {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Interface so that object with function can be sent as parameter
     */
    public interface VoidCallBack
    {
        void execute();
    }

    /**
     * Interface for string so that object with function can be sent as parameter
     */
    public interface StringCallBack
    {
        void execute(String answer);
    }

    /**
     * Interface for boolean so that object with function can be sent as parameter
     */
    public interface BooleanCallBack
    {
        void execute(boolean confirm);
    }
}
