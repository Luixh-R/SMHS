package com.fyp.smhs.Navigation;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.fyp.smhs.R;
import com.fyp.smhs.Activities.NotepadEntry;

import com.fyp.smhs.Database.AppDatabase;
import com.fyp.smhs.Database.Journal;
import com.fyp.smhs.Database.JournalDao;


public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
