package com.gmail.esdrasdl.maps.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.gmail.esdrasdl.maps.controller.MapController;
import com.gmail.esdrasdl.maps.util.MapEvent;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class GPSAddressService extends IntentService {

    public GPSAddressService() {
        super("GPSAddressService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            Log.d(GPSAddressService.class.getSimpleName(), "onHandleIntent");

            double latitude = workIntent.getDoubleExtra(MapController.LATITUDE, 0);
            double longitude = workIntent.getDoubleExtra(MapController.LONGITUDE, 0);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> yourAddresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (yourAddresses.size() > 0) {
                String[] address = new String[]{yourAddresses.get(0).getAddressLine(0), yourAddresses.get(0).getAddressLine(1)};
                EventBus.getDefault().post(new MapEvent(address));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}