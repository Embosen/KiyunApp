package com.example.cchen.test1.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cchen.test1.activity.MainActivity;

/**
 * Created by cchen on 2016/6/20.
 */
public class BootBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("cchen", Intent.ACTION_BOOT_COMPLETED);
        /* 开机启动的应用 */
        context.startActivity(new Intent().setClass(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}