package com.example.campus_positioning_system.Activitys;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_positioning_system.R;
import com.example.campus_positioning_system.RoomList.RoomListConverter;

/**
 * Handles the starting of the Settings fragment after user navigated to settings
 * @author Robin Neumaier
 * @version 1.0
 */

public class ViewPointOfInterestActivity extends AppCompatActivity {

    RecyclerView list;
    /**
     * Method called onCreate of ViewPointOfInterestActivity activity
     * @param savedInstanceState State to restore if there is one
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_points_of_interest);
        RoomListConverter.setMyContext(MainActivity.mainContext());

        TextView roomName;
        roomName = findViewById(R.id.poi_titel);
        //converter.getPOIforView(a.1.25) - > strings
        String x = getIntent().getStringExtra("Origins_QuickDial");
        roomName.setText(x);

        TextView roomDescription = findViewById(R.id.poi_description);
        roomDescription.setText(RoomListConverter.getPOI_Info(MainActivity.mainContext(),x));
    }

    /**
     * Handles going back to the last activity when the user presses the back button
     */
    @Override
    public void onBackPressed() {
        finish();
    }

}

