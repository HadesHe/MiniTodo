package com.example.hades.minitodo.beans;

import android.content.res.Resources;

import com.example.hades.minitodo.R;

/**
 * Created by Hades on 2017/5/9.
 */
public class PreferenceKeys {

    public final String night_mode_pref_key;

    public PreferenceKeys(Resources resources) {
        night_mode_pref_key=resources.getString(R.string.night_mode_pref_key);
    }
}
