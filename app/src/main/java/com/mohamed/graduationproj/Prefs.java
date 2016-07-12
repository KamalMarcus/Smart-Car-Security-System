package com.mohamed.graduationproj;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by nour on 31-Jan-16.
 */
public class Prefs extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
