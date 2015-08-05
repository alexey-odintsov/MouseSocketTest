package com.alekso.mousesockettest;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.CapabilityFilter;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.MouseControl;
import com.connectsdk.service.command.ServiceCommandError;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MouseSocketTest";

    private DevicePicker devicePicker;
    private Button btnConnect;
    private MySensorsListener sensorListener;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(mLaunchClickListener);

        CapabilityFilter webAppFilter = new CapabilityFilter(MouseControl.Move);
        DiscoveryManager.getInstance().setCapabilityFilters(webAppFilter);
        DiscoveryManager.getInstance().setPairingLevel(DiscoveryManager.PairingLevel.ON);
        DiscoveryManager.getInstance().start();

        devicePicker = new DevicePicker(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorListener = new MySensorsListener();
        App.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private View.OnClickListener mLaunchClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (App.tv != null)
                disconnectDevice();

            AlertDialog alertDialog = devicePicker.getPickerDialog("Select a device", new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    App.tv = (ConnectableDevice) parent.getItemAtPosition(position);
                    App.tv.addListener(mDeviceListener);
                    App.tv.connect();
                }
            });
            alertDialog.show();
        }
    };


    private void disconnectDevice() {
        App.tv.removeListener(mDeviceListener);
        App.tv.disconnect();
        App.tv = null;
    }


    private ConnectableDeviceListener mDeviceListener = new ConnectableDeviceListener() {
        @Override
        public void onDeviceReady(ConnectableDevice device) {
            Log.d(TAG, "ConnectableDeviceListener.onDeviceReady: " + device);
            if (App.getMouse() != null) {
                App.getMouse().connectMouse();
            }
        }

        @Override
        public void onDeviceDisconnected(ConnectableDevice device) {
            Log.d(TAG, "ConnectableDeviceListener.onDeviceDisconnected: " + device);
            disconnectDevice();
        }

        @Override
        public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {
            Log.d(TAG, "ConnectableDeviceListener.onPairingRequired: " + device);
        }

        @Override
        public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {
            Log.d(TAG, "ConnectableDeviceListener.onCapabilityUpdated: " + device);
        }

        @Override
        public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
            Log.d(TAG, "ConnectableDeviceListener.onConnectionFailed: " + device);
            disconnectDevice();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (App.sensor != null) {
            sensorManager.registerListener(sensorListener, App.sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null)
            sensorManager.unregisterListener(sensorListener, App.sensor);
    }
}
