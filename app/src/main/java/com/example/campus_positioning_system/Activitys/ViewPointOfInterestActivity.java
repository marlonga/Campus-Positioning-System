package com.example.campus_positioning_system.Activitys;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles the starting of the Settings fragment after user navigated to settings
 * @author Robin Neumaier
 * @version 1.0
 */

public class ViewPointOfInterestActivity extends AppCompatActivity {

    /**
     * Method called onCreate of ViewPointOfInterestActivity activity
     * @param savedInstanceState State to restore if there is one
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        System.out.println("On Create ViewPointsOInterestActivity");
        super.onCreate(savedInstanceState);
    }

    /**
     * Handles going back to the last activity when the user presses the back button
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}

