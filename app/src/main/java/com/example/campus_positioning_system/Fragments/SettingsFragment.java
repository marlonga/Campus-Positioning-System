package com.example.campus_positioning_system.Fragments;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.example.campus_positioning_system.Activitys.LocationSensorActivity;
import com.example.campus_positioning_system.Activitys.MainActivity;
import com.example.campus_positioning_system.Activitys.OnBoardingActivity;
import com.example.campus_positioning_system.BuildConfig;
import com.example.campus_positioning_system.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SeekBarPreference s = findPreference("seekbar");
        Preference buildnumber = findPreference("buildnumber");
        buildnumber.setTitle("Buildnumber");
        CharSequence text = BuildConfig.VERSION_NAME;
        buildnumber.setSummary(text);
        s.setMin(10);
        s.setMax(30);
        s.setAdjustable(true);
        int test = readStepLenghtFromData();
        s.setValue(test);
        s.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                s.setValue((int)newValue);
                System.out.println(newValue);
                System.out.println(preference);
                int value = (int) newValue;
                double valueToChange = (double) value/10;
                LocationSensorActivity.setPathLenght(valueToChange);
                setStepLenghtOfData(value);
                return false;
            }
        });
        Preference tutorial = findPreference("onBoarding");
        tutorial.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                switchActivities();
                return false;
            }
        });
    }

    private void setStepLenghtOfData(int data){
        try (ObjectOutputStream oos = new ObjectOutputStream(MainActivity.mainContext().openFileOutput("steps.data", Context.MODE_PRIVATE))) {
            oos.writeInt(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int readStepLenghtFromData() {
        try (ObjectInputStream oos = new ObjectInputStream(MainActivity.mainContext().openFileInput("steps.data"))) {
            int step = oos.readInt();
            return step;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void onBackPressed() {
        System.out.println("User wants to go back from Settings");

        NavHostFragment navHostFragment = (NavHostFragment) MainActivity.getSupportFragmentManagerMain().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.mainFragment);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        //MainActivity.setOnlyNavigateOnceTrue();

        System.out.println("Navigating from Room List back to Main");
    }

    private void switchActivities() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), OnBoardingActivity.class);
        getActivity().startActivity(intent);
    }
}