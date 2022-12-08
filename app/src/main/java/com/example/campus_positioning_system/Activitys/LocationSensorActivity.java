package com.example.campus_positioning_system.Activitys;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_positioning_system.Map.DrawingAssistant;

import java.util.ArrayList;
import java.util.List;

public class LocationSensorActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor step_sensor;
    private Sensor magnetic_sensor;
    private Sensor accelerometer_sensor;
    private final static int shift_to_northpole = 135;
    private final float[] magnetometerReading = new float[3];
    private final float[] accelerometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private float angle;
    private long lastUpdatedTime = 0;
    private static ArrayList<String> unfound_sensors = new ArrayList<>();
    private static boolean wait_for_sensors = true;

    private boolean isCopied,isCopied2 = false;

    public LocationSensorActivity(){
        this.sensorManager = (SensorManager) MainActivity.mainContext().getSystemService(Context.SENSOR_SERVICE);
        this.step_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        this.magnetic_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.accelerometer_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (step_sensor != null){
            sensorManager.registerListener(this,step_sensor,sensorManager.SENSOR_DELAY_NORMAL);
            System.out.println("#Listener for stepsensor done");
        } else {
            unfound_sensors.add("Pedometer");
        }
        if (magnetic_sensor != null){
            sensorManager.registerListener(this,magnetic_sensor,sensorManager.SENSOR_DELAY_NORMAL);
            System.out.println("#Listener for magneticsensor done");
        } else {
            unfound_sensors.add("Magnetometer");
        }

        if(accelerometer_sensor != null) {
            sensorManager.registerListener(this,accelerometer_sensor,sensorManager.SENSOR_DELAY_NORMAL);
            System.out.println("#Listener for acceleromatersensor done");
        } else {
            unfound_sensors.add("Accelerometer");
        }
        //use if u want to find out which sensors are avaible on device
        //List<Sensor> allSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);
        //System.out.println("all avaible sensors on device:");
        //System.out.println(allSensor);
        wait_for_sensors = false;
    }
    public static boolean isWait_for_sensors() {
        return wait_for_sensors;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        /**
         * TODO: SMOOTH VALUES / FILTERING OF SENSORS
         */

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, accelerometerReading, 0, accelerometerReading.length);
            isCopied = true;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(sensorEvent.values, 0, magnetometerReading, 0, magnetometerReading.length);
            isCopied2 = true;
        }


        if (isCopied && isCopied2 && System.currentTimeMillis() - lastUpdatedTime > 250) {
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);
            float azimuthInRadians = orientationAngles[0];
            float azimuthInDegree = (float) Math.toDegrees(azimuthInRadians);
            lastUpdatedTime = System.currentTimeMillis();
            angle = (int) azimuthInDegree;
            //System.out.println(azimuthInDegree);
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR && isCopied && isCopied2) { // Hier nochmal überprüfen ob man das nicht anders lösen kann mit isCopied und isCopied2
            Pair<Float,Float> pair = changedPositionBasedOnSensors(angle,1.3d * DrawingAssistant.getMapView().getCurrentZoom());
            //System.out.println(DrawingAssistant.getDotMoverMapPosition().getX() + "||1");
            DrawingAssistant.addToDotMoverMapPosition(pair);
            //System.out.println(DrawingAssistant.getDotMoverMapPosition().getY()+ "||2");

            DrawingAssistant.getInstanceMover().animationStart();
            System.out.println("#Step +" + pair);

            // System.out.println(DrawingAssistant.getInstanceMover().getX() +"||" +DrawingAssistant.getInstanceMover().getY());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy) {

        switch(sensor.getType()){
            case Sensor.TYPE_STEP_DETECTOR:
                switch(accuracy) {
                    case SensorManager.SENSOR_STATUS_ACCURACY_LOW :
                        System.out.println("StepSensor is not accurate");
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM :
                        System.out.println("StepSensor is mediumly accurate");
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH :
                        System.out.println("StepSensor is accurate");
                        break;
                }
                break;
            default:
                break;
        }
    }

    public Pair<Float,Float> changedPositionBasedOnSensors(float angle,double pathLenght){
        //TODO winkel hochschule zum norden
        angle = MainActivity.getAngle();
        angle = (angle +360);
        angle = angle % 360;
        angle = angle +53;
        if(angle < 0){
            angle = 360 - (Math.abs(angle));
        }



        Pair<Float,Float> result = new Pair<>(0f,0f);
        float angleForCalculations;

        float x = (float)(cos(toRadians(angle)) * pathLenght);
        float y = (float)(sin(toRadians(angle)) * pathLenght);
        result = new Pair<>(-x,-y);

/*
        if(angle>0 && angle<=90){ //positive x and positive y coordinate
            angleForCalculations = 90 - angle;
            float x = (float)(cos(toRadians(angleForCalculations)) * pathLenght);
            float y = (float)(sin(toRadians(angleForCalculations)) * pathLenght);
            result = new Pair<>(-x,y);
        }

        if(angle>90 && angle<=180){ //positive x and negative y coordinate
            angleForCalculations = 180 - angle;
            float x = (float)(cos(toRadians(angleForCalculations)) * pathLenght);
            float y = (float)(sin(toRadians(angleForCalculations)) * pathLenght);
            result = new Pair<>(-y,-x);
        }

        if(angle<0 && angle>=-90){ //negative x and negative y coordinaten
            angleForCalculations = 90 + angle;
            float x = (float)(cos(toRadians(angleForCalculations)) * pathLenght);
            float y = (float)(sin(toRadians(angleForCalculations)) * pathLenght);
            result = new Pair<>(x,y);
        }

        if(angle<-90 && angle>=-179){ //negative x and positive y coordinaten
            angleForCalculations = 180 + angle;
            float x = (float)(cos(toRadians(angleForCalculations)) * pathLenght);
            float y = (float)(sin(toRadians(angleForCalculations)) * pathLenght);
            result = new Pair<>(y,-x);
        }
*/
        System.out.println( result);
        return result;
    }

    public static ArrayList<String> getUnfoundSensors(){
        return unfound_sensors;
    }


}
