package asteroides.example.org.asteroides.views.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import asteroides.example.org.asteroides.Asteroids;
import asteroides.example.org.asteroides.R;

/**
 * Created by jmtt_ on 10/21/2017.
 *
 */

public class PreferencesFragment extends PreferenceFragmentCompat {
    private EditTextPreference fragments;
    private CheckBoxPreference music;

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


}
