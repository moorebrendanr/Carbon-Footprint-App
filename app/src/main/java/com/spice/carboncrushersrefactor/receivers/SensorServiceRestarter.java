package com.spice.carboncrushersrefactor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spice.carboncrushersrefactor.services.AirportService;

public class SensorServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Location Service","Background service stopped, restarting again");
        context.startService(new Intent(context, AirportService.class));
    }
}
