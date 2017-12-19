package asteroides.example.org.asteroides.views.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.SparseArray;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asteroides.example.org.asteroides.Asteroids;
import asteroides.example.org.asteroides.R;

/**
 *
 */

public class PreferencesFragment extends PreferenceFragmentCompat {
    private EditTextPreference fragments;
    private CheckBoxPreference music;
    private ListPreference scoreStorage;
    private ListPreference externalStorage;
    private CheckBoxPreference useDefaultWebservice;
    private EditTextPreference customWebservice;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_preferences);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        music = (CheckBoxPreference) findPreference("hasMusic");
        music.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ((Asteroids)getContext().getApplicationContext()).resetMusic((boolean)newValue);
                return true;
            }
        });

        externalStorage = (ListPreference) findPreference("externalStorage");

        customWebservice = (EditTextPreference) findPreference("customWebservice");

        useDefaultWebservice = (CheckBoxPreference) findPreference("useDefaultWebservice");
        useDefaultWebservice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                customWebservice.setEnabled(!(Boolean) newValue);
                return true;
            }
        });

        scoreStorage = (ListPreference) findPreference("scoreStorage");
        scoreStorage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                externalStorage.setVisible(false);
                useDefaultWebservice.setVisible(false);
                customWebservice.setVisible(false);
                if(newValue.equals("3") || newValue.equals("4")){
                    loadExternalStorageOptions(newValue.equals("3") || newValue.equals("4"));
                }else if(newValue.equals("15") || newValue.equals("16")){
                    useDefaultWebservice.setVisible(true);
                    customWebservice.setVisible(true);
                    customWebservice.setEnabled(!useDefaultWebservice.isChecked());
                }
                return true;
            }
        });

        loadExternalStorageOptions(scoreStorage.getValue().equals("3") || scoreStorage.getValue().equals("4"));

        useDefaultWebservice.setVisible(scoreStorage.getValue().equals("15") || scoreStorage.getValue().equals("16"));

        customWebservice.setVisible(scoreStorage.getValue().equals("15") || scoreStorage.getValue().equals("16"));
        customWebservice.setEnabled(!useDefaultWebservice.isChecked());
        /*
        fragments = (EditTextPreference) findPreference("fragments");
        int valor = Integer.parseInt(pref.getString("fragments", "3"));
        fragments.setSummary(getResources().getString(R.string.asteroids_fragments_summary, valor));
        fragments.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override public boolean onPreferenceChange (Preference preference, Object newValue){
                int valor;
                try {
                    valor = Integer.parseInt((String) newValue);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.fragments_error_in_input_type, Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (valor >= 0 && valor <= 9) {
                    fragments.setSummary(getResources().getString(R.string.asteroids_fragments_summary, valor));
                    return true;
                } else {
                    Toast.makeText(getActivity(), R.string.fragments_error_in_input_value, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });

        */

    }

    private void loadExternalStorageOptions(boolean canVisible) {
        externalStorage.setVisible(canVisible);
        if (canVisible && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] externalUnits = getContext().getExternalFilesDirs(null);
            Map<String, String> units = new HashMap<>();
            for (int i = 0; i < externalUnits.length; i++) {
                File file = externalUnits[i];
                if(Environment.getStorageState(file).equals(Environment.MEDIA_MOUNTED)){
                    units.put(String.valueOf(i), file.getName());
                }
            }
            if(units.size() > 0){
                externalStorage.setVisible(true);
                externalStorage.setEntries(units.values().toArray(new CharSequence[0]));
                externalStorage.setEntryValues(units.keySet().toArray(new CharSequence[0]));
            }
        }else{
            externalStorage.setVisible(false);
        }
    }
}
