package com.harry.demooutfitterx.Settings;

import android.os.Bundle;

import com.harry.demooutfitterx.R;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /// Set up fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsFrame, new SettingsFragment())
                .commit();
    }
}
