package com.example.popularmoviesstage2.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.popularmoviesstage2.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualizer);
    }
}
