package com.gmail.esdrasdl.maps.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;

import com.gmail.esdrasdl.maps.controller.MapController;
import com.gmail.esdrasdl.maps.util.MapEvent;
import com.google.android.gms.maps.model.LatLng;

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

            if (workIntent.getBooleanExtra(MapController.USE_LATLON, true)) {
                handleGeocoderFromLocation(workIntent);
            } else {
                handleGeocoderFromLocationName(workIntent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGeocoderFromLocation(Intent workIntent) throws IOException {
        double latitude = workIntent.getDoubleExtra(MapController.LATITUDE, 0);
        double longitude = workIntent.getDoubleExtra(MapController.LONGITUDE, 0);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> yourAddresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (yourAddresses.size() > 0) {
            String[] address = new String[]{yourAddresses.get(0).getAddressLine(0), yourAddresses.get(0).getAddressLine(1)};
            EventBus.getDefault().post(new MapEvent(address, new LatLng(latitude, longitude)));
        }
    }

    private void handleGeocoderFromLocationName(Intent workIntent) {
        String query = workIntent.getStringExtra(MapController.QUERY);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(query, 1);

            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                String[] address = new String[]{addresses.get(0).getAddressLine(0), addresses.get(0).getAddressLine(1)};
                EventBus.getDefault().post(new MapEvent(address, new LatLng(latitude,
                        longitude)));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}