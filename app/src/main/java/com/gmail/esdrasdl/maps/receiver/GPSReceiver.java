package com.gmail.esdrasdl.maps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.gmail.esdrasdl.maps.util.GPStatusEvent;

import de.greenrobot.event.EventBus;

public class GPSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean status;
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            status = true;
        } else {
            status = false;
        }
        EventBus.getDefault().post(new GPStatusEvent(status));
    }
}