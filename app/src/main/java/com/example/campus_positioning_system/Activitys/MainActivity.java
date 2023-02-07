package com.example.campus_positioning_system.Activitys;

// Standard Activity Library's

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

// Wifi and Compass Manager


// View

// Room Database
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.example.campus_positioning_system.Database.AppDatabase;
import com.example.campus_positioning_system.Database.NNObjectDao;
import com.example.campus_positioning_system.LocationNavigation.PathfindingControl;
import com.example.campus_positioning_system.Map.DrawingAssistant;
import com.example.campus_positioning_system.R;
import com.example.campus_positioning_system.RoomList.RoomListConverter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //Room Database Object
    private static AppDatabase db;
    private static Context thisContext;
    private BottomNavigationView navigationView;

    private static FragmentManager supportFragmentManager;

    //Sensor Activity Variables
    private SensorManager sensorManager;
    private Sensor accelerometer, magneticField;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    boolean isCopied = false;
    boolean isCopied2 = false;

    long lastUpdatedTime = 0;

    private static int angle;


    //Static height and width of the display
    public static int height;
    public static int width;
    public static int navigationBarHeight;
    public static int statusBarHeight;

    //Wifi Manager Variables
    private static WifiManager wifiManager;
    private static List<ScanResult> availableNetworks;

    //popup
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private PopupWindow popupWindowButton;
    private FragmentContainerView fragmentContainerView;
    private static ImageButton stop_path;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        //LocationFakerThread
        //new LocationFakerThread().start();
        //----------------------------------------------------------------------------

        //hier kommen die Schnellwahl hin also adden der nodes.
        /*
        RoomItem item = new RoomItem("A3.01", "bib","37/61/1");
        TreeNode tnode = new TreeNode(item,R.id.room_list_quick_layout);
        if(tnode != null) {
            RoomListConverter.addQuickDial(tnode);
        }

         */

        System.out.println("On Create Main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        thisContext = getApplicationContext();


        supportFragmentManager = getSupportFragmentManager();

        //------------------------------------------------------------------------------
        //Sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        LocationSensorActivity locationSensorActivity = new LocationSensorActivity();


        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //------------------------------------------------------------------------------
        //Display height and width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels + getNavigationBarHeight();
        width = displayMetrics.widthPixels;
        navigationBarHeight = getNavigationBarHeight();
        statusBarHeight = getStatusBarHeight();
        //------------------------------------------------------------------------------



        navigationView = findViewById(R.id.bottom_navigation);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                System.out.println("Item is: " + item.getItemId());
                switch (item.getItemId()) {
                    case R.id.nav_room_list:
                        switchActivities(RoomSelectionActivity.class);
                        break;

                    case R.id.nav_settings:
                        switchActivities(SettingsActivity.class);
                        break;

                    case R.id.nav_favorites:
                        switchActivities(FavoritesActivity.class);
                        break;
                    case R.id.nav_quickdial:
                        switchActivities(QuickDialActivity.class);
                        break;
                    case R.id.nav_qrcode:
                        switchActivities(QRcodeActivity.class);
                        break;
                }
                return false;
            }
        });

        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result ->
        {
            Boolean fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
            if (fineLocationGranted != null && fineLocationGranted) {
                // Precise location access granted.
            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                // Only approximate location access granted.
            } else {
                // No location access granted.
            }
        });
        //Sets the Points of Interests so they appear as soon as the app starts
        DrawingAssistant.setPointsOfInterestsNodes(RoomListConverter.generatePOINodes(this));

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
        });
        ;
        //Onboarding here
        if(!readOnboardingData()){
            setOnboardingDataTrue();
            switchActivities(IntroSequenceActivity.class);
        }



        ArrayList<String> unfound_sensors = locationSensorActivity.getUnfoundSensors();
        //unfound_sensors.add("Magnetometer");
        //unfound_sensors.add("Pedometer");
        final ImageButton button = (ImageButton) findViewById(R.id.sensor_popup_button);
        button.setVisibility(View.GONE);

        if(!unfound_sensors.isEmpty()){
            //POPUP_WINDOW IF SENSOR MISSING
            fragmentContainerView = (FragmentContainerView) findViewById(R.id.nav_host_fragment);
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.sensor_info_popup,null);
            popupWindow = new PopupWindow(container,400,650,true);
            button.startAnimation(AnimationUtils.loadAnimation(mainContext(),R.anim.heartbeat_anim));
            String resulting_info = "";
            if (unfound_sensors.size() == 1){
                resulting_info += "Folgender Sensor wurde an ihrem Gerät nicht erkannt: \n";
            } else if (unfound_sensors.size() > 1){
                resulting_info += "Folgende Sensoren wurden an ihrem Gerät nicht erkannt: \n";
            }
            for (String s : unfound_sensors){
                resulting_info += " >" + s + "\n";
            }
            resulting_info += "Es könnte also zu Ungenauigkeiten ihrer Position in der App kommen.";

            TextView infoText = (TextView) container.findViewById(R.id.info_of_sensors_popup);

            infoText.setText(resulting_info);

            findViewById(R.id.nav_host_fragment).post(new Runnable() {
                public void run() {
                    popupWindow.showAtLocation(fragmentContainerView, Gravity.CENTER, 0, 0);
                }
            });
            popupWindow.setElevation(10);

            container.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popupWindow.dismiss();
                    return false;
                }
            });


            button.setVisibility(View.VISIBLE);
            button.setElevation(3);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.showAtLocation(fragmentContainerView, Gravity.CENTER, 0, 0);
                }
            });
        }
        final ImageButton quick_exit = (ImageButton) findViewById(R.id.quick_Exit);
        quick_exit.setElevation(3);
        quick_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread (() -> {
                    PathfindingControl.calculatePathForExits();
                    DrawingAssistant.setPathToDestination(PathfindingControl.calculatePath());
                }).start();
            }
        });

        stop_path = (ImageButton) findViewById(R.id.stop_path);
        stop_path.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DrawingAssistant.stopPath();
            }
        });

        System.out.println("On Create Ende Main Activity");
    }

    private void switchActivities(Class<?> activityClass) {
        Intent switchActivityIntent = new Intent(this, activityClass);
        startActivity(switchActivityIntent);
    }


    public static NNObjectDao getNNObjectDaoFromDB(){
        return db.nnObjectDao();
    }

    public static AppDatabase getDb(){
        return AppDatabase.getInstance();
    }

    public static FragmentManager getSupportFragmentManagerMain() {
        return supportFragmentManager;
    }

    public static Context mainContext() {
        return thisContext;
    }


    // Ab hier alles ehemalige Sensor Activity

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch(sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD :
                switch(accuracy) {
                    case SensorManager.SENSOR_STATUS_ACCURACY_LOW :
                        System.out.println("Sensor is not accurate");
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM :
                        System.out.println("Sensor is mediumly accurate");
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH :
                        System.out.println("Sensor is accurate");
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magneticField);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
            isCopied = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
            isCopied2 = true;
        }

        if (isCopied && isCopied2 && System.currentTimeMillis() - lastUpdatedTime > 250) {
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            float azimuthInRadians = orientationAngles[0];
            float azimuthInDegree = (float) Math.toDegrees(azimuthInRadians);

            lastUpdatedTime = System.currentTimeMillis();

            angle = (int) azimuthInDegree;
        }
    }

    public static int getAngle() {
        return angle;
    }

    //Function to get Navigation Bar Height
    private int getNavigationBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setOnboardingDataTrue(){
        try (ObjectOutputStream oos = new ObjectOutputStream(MainActivity.mainContext().openFileOutput("onboarding.data", Context.MODE_PRIVATE))) {
            oos.writeBoolean(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean readOnboardingData() {
        try (ObjectInputStream oos = new ObjectInputStream(MainActivity.mainContext().openFileInput("onboarding.data"))) {
            boolean isTrue = oos.readBoolean();
            return isTrue;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void showStopPath(){

        stop_path.post(new Runnable() {
            @Override
            public void run() {
                stop_path.setVisibility(View.VISIBLE);
            }
        });
    }
    public static void removeStopPath(){
        stop_path.post(new Runnable() {
            @Override
            public void run() {
                stop_path.setVisibility(View.GONE);
            }
        });
    }
}


/*
        DrawingAssistant drawingAssistant = new DrawingAssistant();

        Intent wifiRights = new Intent(Settings.Panel.ACTION_WIFI);
        startActivity(wifiRights);


        new Thread(() -> db = Room.inMemoryDatabaseBuilder(getApplicationContext(), AppDatabase.class).build()).start();

        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);

        WifiScanner nearestWifiScanner = new WifiScanner(getApplicationContext());
        Thread scannerThread = new Thread(nearestWifiScanner);
       // scannerThread.start();
        */
