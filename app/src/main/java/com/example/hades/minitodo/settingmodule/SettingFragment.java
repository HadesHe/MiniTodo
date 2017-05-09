package com.example.hades.minitodo.settingmodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

import com.example.hades.minitodo.R;
import com.example.hades.minitodo.beans.PreferenceKeys;
import com.example.hades.minitodo.mainmodule.MainActivity;

/**
 * Created by Hades on 2017/5/9.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_layout);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        PreferenceKeys preferenceKeys=new PreferenceKeys(getResources());

        if(key.equals(preferenceKeys.night_mode_pref_key)){
            SharedPreferences themePreferences=getActivity().getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor themeEditor=themePreferences.edit();
            themeEditor.putBoolean(MainActivity.RECREATE_ACTIVITY,true);

            CheckBoxPreference checkBoxPreference= (CheckBoxPreference) findPreference(preferenceKeys.night_mode_pref_key);
            if(checkBoxPreference.isChecked()){
                themeEditor.putString(MainActivity.THEME_SAVED,MainActivity.DARKTHEME);
            }else{
                themeEditor.putString(MainActivity.THEME_SAVED,MainActivity.LIGHTTHEME);
            }
            themeEditor.apply();
            getActivity().recreate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
