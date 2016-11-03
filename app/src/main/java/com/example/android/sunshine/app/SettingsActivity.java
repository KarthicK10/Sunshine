package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

/**
 * Created by KarthicK on 5/25/2016.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        /*Add 'general' preferences, defined in the xml file */
        addPreferencesFromResource(R.xml.pref_general);

        /*For all preferences, attach an OnPreferenceChangeListener
         * so the UI summary can update when the preference changes */
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_unit_key)));


    }

    private void bindPreferenceSummaryToValue(Preference preference){
        //Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        //Trigger the listener immediately with the preference's current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public void setPreferenceSummary(Preference preference, Object value){
        String stringValue = value.toString();

        if(preference instanceof ListPreference){
            //For List preferences, look up the correct display value in
            //the preference's 'entries' list (since they have seperate values/labels).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex >= 0){
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }else{
            //For other preferences set the summary to the value's simple string representation
            preference.setSummary(stringValue);
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        setPreferenceSummary(preference, value);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_location_status_key))){
            //location changed.
            //First clear locationStatus.
            Utility.resetLocationStatus(this);
            SunshineSyncAdapter.syncImmediately(this);
        } else if (key.equals(getString(R.string.pref_unit_key))){
            //units have changed. update list of weather entries accordingly.
            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
