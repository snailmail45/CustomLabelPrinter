package com.brotherhackathon.smartlabels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String                   APP_NAME = "HAPPY_NOTES";
    private static       SharedPrefManager        sharedPrefManager;
    private static       SharedPreferences        sharedPreferences;
    private static       SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    private SharedPrefManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPrefManager getInstance(Context context) {
        if (sharedPrefManager == null) {
            sharedPrefManager = new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }

    public boolean getImageCheckboxStatus(){
        return sharedPreferences.getBoolean(Constants.KEY_SHAREDPREF_IMAGE, false);
    }

    public void setImageCheckBoxStatus(boolean b){
        editor.putBoolean(Constants.KEY_SHAREDPREF_IMAGE, b);
        editor.commit();
    }

    public boolean getCustomMsgCheckboxStatus(){
        return sharedPreferences.getBoolean(Constants.KEY_SHAREDPREF_CUSTOM_MSG, false);
    }

    public void setCustomMsgCheckboxStatus(boolean b){
        editor.putBoolean(Constants.KEY_SHAREDPREF_CUSTOM_MSG, b);
        editor.commit();
    }
}
