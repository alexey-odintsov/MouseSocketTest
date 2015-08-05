package com.alekso.mousesockettest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by alexey.odintsov on 8/5/2015.
 */
public class MySensorsListener implements SensorEventListener {

    private float[] gyro = new float[3];

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyro = event.values.clone();

            if (App.getMouse() != null) { // && App.getMouse().isMouseConnected())
                App.getMouse().move(-gyro[2] * 20.f, -gyro[0] * 20.f);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
