package com.example.weitan.mega.datamanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.kanmanus.kmutt.sit.ijoint.models.PatientProfileViewModel;

/**
 * Created by Semicolon07 on 7/5/2016 AD.
 */

public class SessionManager {

    private SharedPreferences userPreferences;
    private SharedPreferences.Editor userPrefsEditor;

    public static final String USER_PREF_NAME = "PROFILE_PREF";
    private static final String PROPERTY_PROFILE = "profile";

    public SessionManager(Context context) {
        userPreferences = context.getSharedPreferences(USER_PREF_NAME, context.MODE_PRIVATE);
        userPrefsEditor = userPreferences.edit();
    }

    public void setSessionData(PatientProfileViewModel profileModel) {
        Gson gson = new Gson();
        String json = gson.toJson(profileModel);
        userPrefsEditor.putString(PROPERTY_PROFILE, json);
        userPrefsEditor.commit();
        Log.d("SessionManager",profileModel.toString());
    }

    public PatientProfileViewModel getSessionData() {
        Gson gson = new Gson();
        String json = userPreferences.getString(PROPERTY_PROFILE, "");
        PatientProfileViewModel user = gson.fromJson(json, PatientProfileViewModel.class);
        return user;
    }

    public void clear() {
        userPrefsEditor.putString(PROPERTY_PROFILE,null);
        userPrefsEditor.commit();
    }
}
