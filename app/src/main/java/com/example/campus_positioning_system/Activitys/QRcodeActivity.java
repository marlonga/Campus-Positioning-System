package com.example.campus_positioning_system.Activitys;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_positioning_system.Fragments.SettingsFragment;

public class QRcodeActivity extends AppCompatActivity {

    /**
     * Method called onCreate of Settings activity
     * @param savedInstanceState State to restore if there is one
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        System.out.println("On Create QRcode Activity");
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new SettingsFragment()).commit();
        }
    }

    /**
     * Handles going back to the map when the user presses the back button
     */
    @Override
    public void onBackPressed() {
        //System.out.println("User wants to go back from Room list");
        //System.out.println("Navigating from Room List back to Main");
        finish();
    }
}
