package com.example.weitan.mega.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.datamanager.MyAlarmManager;
import com.kanmanus.kmutt.sit.ijoint.view.TimePreference;

/**
 * Created by Semicolon07 on 7/6/2016 AD.
 */

public class ApplicationSettingActivity extends BasePreferenceActivity {
    public static final String KEY_PREF_TIME = "pref_key_exercise_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUpButton();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, newValue) -> {
        String key = preference.getKey();

        if(key.equals(KEY_PREF_TIME)){
            String time = newValue.toString();
            int hour = TimePreference.getHour(time);
            int minute = TimePreference.getMinute(time);
            MyAlarmManager.getInstance().setExerciseNotificationTime(hour,minute,preference.getSummary().toString());
        }
        return true;
    };

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_application_setting);

            bindPreferenceSummaryToValue(findPreference(KEY_PREF_TIME));
        }
    }
}
