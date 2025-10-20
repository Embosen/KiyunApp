package com.example.cchen.test1.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.cchen.test1.inter.DataUi;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cchen on 2016/5/22.
 */
public class DataService extends Service {
    private DataUi mUi;
    private UsbSerialPort sPort;
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private PendingIntent mPermissionIntent;

    public void setDataUi(DataUi ui) {
        mUi = ui;
    }

    private static final String TAG = "cchen DataService";
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    if (mUi != null)
                        mUi.onNewData(data);
                }
            };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            onGetPermission(device);
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    private void onGetPermission(UsbDevice device) {
        final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        UsbDeviceConnection connection = usbManager.openDevice(device);
        Log.d(TAG, "connection " + connection);
        if (connection == null) return;

        try {
            sPort.open(connection);
            sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            Log.d(TAG, "CD  - Carrier Detect" + sPort.getCD());
            Log.d(TAG, "CTS - Clear To Send" + sPort.getCTS());
            Log.d(TAG, "DSR - Data Set Ready" + sPort.getDSR());
            Log.d(TAG, "DTR - Data Terminal Ready" + sPort.getDTR());
            Log.d(TAG, "DSR - Data Set Ready" + sPort.getDSR());
            Log.d(TAG, "RI  - Ring Indicator" + sPort.getRI());
            Log.d(TAG, "RTS - Request To Send" + sPort.getRTS());
            Log.d(TAG, "Done sPort.open");
        } catch (IOException e) {
            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
            try {
                sPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            sPort = null;
        }
        onDeviceStateChange();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind ...");
        init();
        return new MsgBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    private void init() {
        initUsb();
    }
    private void initUsb() {
        final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                Log.d(TAG, "Refreshing device list ...");
                SystemClock.sleep(1000);

//                usbManager.requestPermission();

                final List<UsbSerialDriver> drivers =
                        UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);

                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
                for (final UsbSerialDriver driver : drivers) {
                    final List<UsbSerialPort> ports = driver.getPorts();
                    Log.d(TAG, String.format("+ %s: %s port%s",
                            driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
                    result.addAll(ports);
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                if (result == null || result.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No port detected", Toast.LENGTH_LONG).show();
                    return;
                }

                sPort = result.get(0);
                Log.d(TAG, "Done refreshing, " + result + " entries found.");

                if (sPort == null) return;
                UsbDevice device = sPort.getDriver().getDevice();
                Log.d(TAG, sPort.getDriver() + " sPort " + device);

                usbManager.requestPermission(device, mPermissionIntent);


            }

        }.execute((Void) null);


    }

    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public DataService getService() {
            return DataService.this;
        }

    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.d(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.d(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        unregisterReceiver(mUsbReceiver);
    }
}
