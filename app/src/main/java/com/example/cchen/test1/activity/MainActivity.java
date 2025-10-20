package com.example.cchen.test1.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cchen.test1.R;
import com.example.cchen.test1.adapter.LightItemAdapter;
import com.example.cchen.test1.inter.DataUi;
import com.example.cchen.test1.model.ToggleItem;
import com.example.cchen.test1.service.LocalDataService2;
import com.example.cchen.test1.util.BitUtil;
import com.example.cchen.test1.view.KyChartView;
import com.example.cchen.test1.view.RpmView;
import com.example.cchen.test1.view.SpeedView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static com.example.cchen.test1.model.ToggleItem.LightKey;
import static com.example.cchen.test1.model.ToggleItem.ToggleKey;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener, DataUi {

    private static final String FONT_DIGITAL_7 = "fonts" + File.separator + "digital_7.ttf";
    private static final String TAG = "MainActivity cchen";
    private static final int LIGHT_COUNT = 10;
    private static final int TOGGLE_COUNT = 9;
    private static final float DISTANCE_MAX = 1000f;
    private static final float CONSUME_MAX = 50f;

    LinkedHashMap<LightKey, ToggleItem> lights = new LinkedHashMap<>(LIGHT_COUNT);
    LinkedHashMap<ToggleKey, ToggleItem> toggles = new LinkedHashMap<>(TOGGLE_COUNT);

    LinkedList<LightKey> lightKeys = new LinkedList<>();
    LinkedList<ToggleKey> toggleKeys = new LinkedList<>();

    private static final int MAIN_LOOP = 0x2;

    private RecyclerView grid_lights;
    private RecyclerView grid_toggles;

    Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MAIN_LOOP:
//                    refresh(new byte[]{});
                    mainHandler.sendEmptyMessageDelayed(MAIN_LOOP, 100);
                    break;
            }
        }
    };

    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            LocalDataService2 msgService = ((LocalDataService2.MsgBinder) service).getService();
            msgService.addUi(MainActivity.this);
        }
    };
    private TextView txt_fuel_consume;
    private TextView txt_distance;
    private static int clickCount;
    private KyChartView gear_oil_pressure;
    private KyChartView fuel_height;
    private KyChartView torque_converter_oil_temperature;

    private void initLights() {
        //ui
        lights.put(LightKey.TOP, new ToggleItem(LightKey.TOP, "顶灯"));
        lights.put(LightKey.LEFT, new ToggleItem(LightKey.LEFT, "左转向灯"));
        lights.put(LightKey.RIGHT, new ToggleItem(LightKey.RIGHT, "右转向灯"));
        lights.put(LightKey.SHOW, new ToggleItem(LightKey.SHOW, "开关照明灯"));
        lights.put(LightKey.WIDTH, new ToggleItem(LightKey.WIDTH, "示宽灯"));
        lights.put(LightKey.FAR, new ToggleItem(LightKey.FAR, "远光灯"));
        lights.put(LightKey.NEAR, new ToggleItem(LightKey.NEAR, "近光灯"));
        lights.put(LightKey.BREAK, new ToggleItem(LightKey.BREAK, "制动灯"));
        lights.put(LightKey.ANTI_AIR_BREAK, new ToggleItem(LightKey.ANTI_AIR_BREAK, "防空制动灯"));
        lights.put(LightKey.ANTI_AIR_SHOW, new ToggleItem(LightKey.ANTI_AIR_SHOW, "防空照明灯"));

        //data
        lightKeys.add(LightKey.TOP);
        lightKeys.add(LightKey.FAR);
        lightKeys.add(LightKey.NEAR);
        lightKeys.add(LightKey.LEFT);
        lightKeys.add(LightKey.RIGHT);
        lightKeys.add(LightKey.WIDTH);
        lightKeys.add(LightKey.SHOW);
        lightKeys.add(LightKey.BREAK);
        lightKeys.add(LightKey.ANTI_AIR_BREAK);
        lightKeys.add(LightKey.ANTI_AIR_SHOW);

    }

    private void initToggles() {
        toggles.put(ToggleKey.IGN, new ToggleItem(ToggleKey.IGN, "IGN"));
        toggles.put(ToggleKey.LEFT, new ToggleItem(ToggleKey.LEFT, "左转向"));
        toggles.put(ToggleKey.RIGHT, new ToggleItem(ToggleKey.RIGHT, "右转向"));
        toggles.put(ToggleKey.WARNING, new ToggleItem(ToggleKey.WARNING, "警报灯"));
        toggles.put(ToggleKey.WIDTH, new ToggleItem(ToggleKey.WIDTH, "示宽灯"));
        toggles.put(ToggleKey.FAR, new ToggleItem(ToggleKey.FAR, "远光"));
        toggles.put(ToggleKey.NEAR, new ToggleItem(ToggleKey.NEAR, "近光"));
        toggles.put(ToggleKey.BREAK, new ToggleItem(ToggleKey.BREAK, "制动"));
        toggles.put(ToggleKey.ANTI_AIR, new ToggleItem(ToggleKey.ANTI_AIR, "防空"));

        toggleKeys.add(ToggleKey.IGN);
        toggleKeys.add(ToggleKey.LEFT);
        toggleKeys.add(ToggleKey.RIGHT);
        toggleKeys.add(ToggleKey.WARNING);
        toggleKeys.add(ToggleKey.WIDTH);
        toggleKeys.add(ToggleKey.FAR);
        toggleKeys.add(ToggleKey.NEAR);
        toggleKeys.add(ToggleKey.BREAK);
        toggleKeys.add(ToggleKey.ANTI_AIR);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLights();
        initToggles();
        setContentView(R.layout.content_main);
        initViews();
        bindService(new Intent().setClass(MainActivity.this, LocalDataService2.class), conn, Context.BIND_AUTO_CREATE);
        //        mainHandler.sendEmptyMessage(MAIN_LOOP);

    }

    byte[] testFrame = new byte[]{
            -86, 85,//AA 55
            19,//length
            -5,//fb
            (byte) 0xcc, (byte) 0xcc,//oil pressure  0xcccc
            0, 0,
            0, 0,
            0, 0,
            0, 0,
            0, 0,
            4, 27,//0x1b04
            3, 0,
            9, 0,
            1, 26,//check sum
            -86, 85//AA 55
    };

    private float get2ByteValue(byte low, byte high, float min, float max) {
        int hInt = high & 0xFF;
        int lInt = low & 0xFF;
        int moved = hInt << 8;
        int rltI = moved | lInt;
        float rlt = moved | lInt;

        float x = min + (rlt) * (max - min) / 65535f;
        Log.d(TAG, high + " hl " + low);
        Log.d(TAG, rltI + " t " + rlt + " x " + x);

        return x;
    }


    private void initViews() {
        initLightItemViews();

        initBars();
        initFonts();

        bindClick();
//        testRefresh();
//        get2ByteValue(testFrame[4], testFrame[5], 0f, 4f);
//        get2ByteValue(testFrame[16], testFrame[17], 0f, 2000f);
    }


    private void bindClick() {
        SpeedView chart_speed = (SpeedView) findViewById(R.id.chart_speed);
        chart_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " click count " + clickCount);
                clickCount++;
                clickHandler.sendEmptyMessageDelayed(0, 1000);
                if (clickCount == 3) {
                    startActivity(new Intent().setClass(MainActivity.this, RawDataActivity.class));
                    clickCount = 0;
                }
            }
        });
    }

    private void initFonts() {
        AssetManager assets = getAssets();
        final Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);

        txt_fuel_consume = (TextView) findViewById(R.id.txt_fuel_consume);
        if (txt_fuel_consume != null) txt_fuel_consume.setTypeface(font);

        txt_distance = (TextView) findViewById(R.id.txt_distance);
        if (txt_distance != null) txt_distance.setTypeface(font);

        if (txt_distance != null)
            txt_distance.setText(getString(R.string.distance, 0f));
        if (txt_fuel_consume != null)
            txt_fuel_consume.setText(getString(R.string.fuel_consume, 0f));

    }

    private void initBars() {
        gear_oil_pressure = (KyChartView) findViewById(R.id.gear_oil_pressure);
        gear_oil_pressure.setTitle("变速器油压");
        gear_oil_pressure.MAX_VALUE = 4;
        gear_oil_pressure.valueTextId = R.string.mpa;

        fuel_height = (KyChartView) findViewById(R.id.fuel_height);
        fuel_height.setTitle("燃油液位");
        fuel_height.MAX_VALUE = 100f;
        fuel_height.valueTextId = R.string.fuel_height;


        torque_converter_oil_temperature = (KyChartView) findViewById(R.id.torque_converter_oil_temperature);
        torque_converter_oil_temperature.setTitle("变矩器油温");
        torque_converter_oil_temperature.MAX_VALUE = 150;
        torque_converter_oil_temperature.valueTextId = R.string.tcot;

    }

    private void initLightItemViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        grid_lights = (RecyclerView) findViewById(R.id.grid_lights);
        grid_lights.setAdapter(new LightItemAdapter(this, lights.values().toArray(new ToggleItem[LIGHT_COUNT]), R.drawable.status_light));
        grid_lights.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);

        grid_toggles = (RecyclerView) findViewById(R.id.grid_toggles);
        grid_toggles.setAdapter(new LightItemAdapter(this, toggles.values().toArray(new ToggleItem[TOGGLE_COUNT]), R.drawable.status_toggle));
        grid_toggles.setLayoutManager(linearLayoutManager2);
    }

    public void onFrameGet(ArrayList<Byte> frame) {
//        frame.clear();
//        for(byte b:testFrame){
//            frame.add(b);
//        }
        try {
            refreshBar(frame);
            refreshCircleView(frame);
            refreshStatusLight(frame);
            refreshToggle(frame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshToggle(ArrayList<Byte> frame) {
        Byte toggleLow = frame.get(18);
        Byte toggleHigh = frame.get(19);
        final byte[] low = BitUtil.getBooleanArray(toggleLow);
        final byte[] high = BitUtil.getBooleanArray(toggleHigh);
        Log.d(TAG, toggleLow + " refreshTogglerrrr " + toggleHigh);
        Log.d(TAG, BitUtil.byteToBit(toggleLow) + " refreshToggle " + BitUtil.byteToBit(toggleHigh));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 7; i >= 0; i--) {
                    int index = 7 - i;
                    toggles.get(toggleKeys.get(index)).isOn = low[i] == 1;
//                    Log.d(TAG, (index) + " setItemChecked " + grid_toggles.isItemChecked(index) + " " + (checked));
                }
                for (int i = 7; i >= 7; i--) {
                    int index = 7 - i;
                    toggles.get(toggleKeys.get(index + 8)).isOn = high[i] == 1;
//                    Log.d(TAG, (index + 8) + " setItemChecked " + grid_toggles.isItemChecked(index + 8) + " " + (checked));
                }
                refreshToggles();
            }
        });
    }

    private void refreshStatusLight(ArrayList<Byte> frame) {
        Byte statusLow = frame.get(20);
        Byte statusHigh = frame.get(21);
        final byte[] low = BitUtil.getBooleanArray(statusLow);
        final byte[] high = BitUtil.getBooleanArray(statusHigh);
        Log.d(TAG, BitUtil.byteToBit(statusLow) + " refreshStatusLight " + BitUtil.byteToBit(statusHigh));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 7; i >= 0; i--) {
                    int index = 7 - i;
                    lights.get(lightKeys.get(index)).isOn = low[i] == 1;
//                    Log.d(TAG, (index) + " setItemChecked " + lightKeys.get(index) + " " + lights.get(lightKeys.get(index)).name);
                }
                for (int i = 7; i >= 6; i--) {
                    int index = 7 - i;
                    lights.get(lightKeys.get(index + 8)).isOn = high[i] == 1;
//                    Log.d(TAG, (index + 8) + " setItemChecked " + grid_lights.isItemChecked(index + 8) + " " + (checked));
                }
                refreshStatusLight();
            }
        });
    }

    private void refreshStatusLight() {
        grid_lights.getAdapter().notifyDataSetChanged();
    }

    private void refreshToggles() {
        grid_toggles.getAdapter().notifyDataSetChanged();
    }


    private void refreshCircleView(ArrayList<Byte> frame) {
        Byte rpmLow = frame.get(10);
        Byte rpmHigh = frame.get(11);

        final float rpm = get2ByteValue(rpmLow, rpmHigh, 0f, RpmView.MAX_VALUE);

        Log.d(TAG, " refresh rpm " + " " + rpm);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RpmView chart_rpm = (RpmView) findViewById(R.id.chart_rpm);
                if (chart_rpm != null) {
                    chart_rpm.setCurrentStatus(rpm / (RpmView.MAX_VALUE * 1f));
                    chart_rpm.refreshChart();
                }
            }
        });

        Byte speedLow = frame.get(12);
        Byte speedHigh = frame.get(13);
        final float spd = get2ByteValue(speedLow, speedHigh, 0f, SpeedView.MAX_VALUE);
        Log.d(TAG, " refresh speed " + " " + spd);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SpeedView chart_speed = (SpeedView) findViewById(R.id.chart_speed);
                if (chart_speed != null) {
                    chart_speed.setCurrentStatus(spd / SpeedView.MAX_VALUE);
                    chart_speed.refreshChart();
                }
            }
        });

        final Byte distanceLow = frame.get(14);
        final Byte distanceHigh = frame.get(15);
        final float dst = get2ByteValue(distanceLow, distanceHigh, 0f, DISTANCE_MAX);
        Log.d(TAG, " refresh distance " + " " + dst);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (txt_distance != null) {
                    txt_distance.setText(getString(R.string.distance, dst));
                }
            }
        });


        Byte consumeLow = frame.get(16);
        Byte consumeHigh = frame.get(17);

        final float csmI = get2ByteValue(consumeLow, consumeHigh, 0, CONSUME_MAX);
        Log.d(TAG, " refresh consume " + " " + csmI);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (txt_fuel_consume != null) {
                    txt_fuel_consume.setText(getString(R.string.fuel_consume, csmI));
                }
            }
        });
    }

    private void refreshBar(ArrayList<Byte> frame) {
        Byte gearOilPressureLow = frame.get(4);
        Byte gearOilPressureHigh = frame.get(5);

        Byte fuelHeightLow = frame.get(6);
        Byte fuelHeightHigh = frame.get(7);

        Byte torqueConverterOilTemperatureLow = frame.get(8);
        Byte torqueConverterOilTemperatureHigh = frame.get(9);

        final float gop = get2ByteValue(gearOilPressureLow, gearOilPressureHigh,
                gear_oil_pressure.MIN_VALUE, gear_oil_pressure.MAX_VALUE);
        final float fh = get2ByteValue(fuelHeightLow, fuelHeightHigh,
                fuel_height.MIN_VALUE, fuel_height.MAX_VALUE);
        final float tcot = get2ByteValue(torqueConverterOilTemperatureLow, torqueConverterOilTemperatureHigh,
                torque_converter_oil_temperature.MIN_VALUE, torque_converter_oil_temperature.MAX_VALUE);
        Log.d(TAG, gop + " refreshBar " + fh + " " + tcot);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gear_oil_pressure.setValue(gop);
                fuel_height.setValue(fh);
                torque_converter_oil_temperature.setValue(tcot);
            }
        });
    }


    private void testRefresh() {
        RpmView chart_rpm = (RpmView) findViewById(R.id.chart_rpm);
        if (chart_rpm != null) {
            chart_rpm.setCurrentStatus(2250 / (RpmView.MAX_VALUE * 1f));
            chart_rpm.refreshChart();
        }

        SpeedView chart_speed = (SpeedView) findViewById(R.id.chart_speed);
        if (chart_speed != null) {
            chart_speed.setCurrentStatus(54 / SpeedView.MAX_VALUE);
            chart_speed.refreshChart();

        }

        gear_oil_pressure.setValue(4f);
        fuel_height.setValue(66f);
        torque_converter_oil_temperature.setValue(20f);

        if (txt_distance != null)
            txt_distance.setText(getString(R.string.distance, 888f));
        if (txt_fuel_consume != null)
            txt_fuel_consume.setText(getString(R.string.fuel_consume, 5f));
    }

    Handler clickHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                clickCount = 0;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeMessages(MAIN_LOOP);
        mainHandler = null;
        unbindService(conn);
        stopService(new Intent().setClass(this, LocalDataService2.class));
        System.exit(0);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String tag = buttonView.getTag().toString();
        Toast.makeText(MainActivity.this, tag + (isChecked ? "打开" : "关闭"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNewData(byte[] bytes) {
    }

}