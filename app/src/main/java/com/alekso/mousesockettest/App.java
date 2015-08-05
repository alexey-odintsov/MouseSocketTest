package com.alekso.mousesockettest;

import android.app.Application;
import android.hardware.Sensor;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.capability.MouseControl;

/**
 * Created by alexey.odintsov on 8/5/2015.
 */
public class App extends Application {

    public static ConnectableDevice tv;
    public static Sensor sensor;

    @Override
    public void onCreate() {
        DiscoveryManager.init(getApplicationContext());

        super.onCreate();
    }

    public static MouseControl getMouse() {
        if (tv != null && tv.isConnected()) {
            return tv.getCapability(MouseControl.class);
        } else {
            return null;
        }
    }
}
