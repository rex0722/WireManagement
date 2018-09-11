package com.study.application.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Owen","AutoReceive start");
        Intent i = new Intent(context,AutoService.class);
        context.startService(i);
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

    }
}
