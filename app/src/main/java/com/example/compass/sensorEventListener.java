package com.example.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

interface sensorEventListener {
    void onSensorChanged(SensorEvent sensorEvent);

    void onAccuracyChanged(Sensor sensor, int i);
}
