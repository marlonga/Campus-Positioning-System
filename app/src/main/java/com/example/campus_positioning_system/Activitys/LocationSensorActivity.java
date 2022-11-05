package com.example.campus_positioning_system.Activitys;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_positioning_system.Map.DrawingAssistant;

public class LocationSensorActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor step_sensor;
    private Sensor magnetic_sensor;

    public LocationSensorActivity(){
        this.sensorManager = (SensorManager) MainActivity.mainContext().getSystemService(Context.SENSOR_SERVICE);
        this.step_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        this.magnetic_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this,step_sensor,sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,magnetic_sensor,sensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
       // if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
            DrawingAssistant.getInstanceMover().addToPosition((float)0.1,(float)0.1);
            DrawingAssistant.getInstanceMover().animationStart();
        System.out.println("XXXXXXXXXXX");
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

    public Pair<Float,Float> changedPositionBasedOnSensors(){


        return null;
    }


}
