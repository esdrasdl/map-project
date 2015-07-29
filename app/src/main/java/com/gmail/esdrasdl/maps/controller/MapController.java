package com.gmail.esdrasdl.maps.controller;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.gmail.esdrasdl.maps.service.GPSAddressService;
import com.gmail.esdrasdl.maps.util.MapEvent;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;


/**
 * Created by esdras on 26/07/15.
 */
public class MapController {
    private static final String LOG_TAG = MapController.class.getSimpleName();
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    private GoogleMap mMap;
    private Context mContext;
    private boolean isMoveCameraAllow = true;
    private LocationListener mLocationListener;
    private MarkerOptions mOriginalMarker;
    private long lastUpdateTime = 0;
    private final long interval = 2 * 1000;


    public MapController(Context context, GoogleMap map) {
        mContext = context;
        mMap = map;
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                isMoveCameraAllow = false;
                final LatLng target = cameraPosition.target;
                Log.d(LOG_TAG, target.toString());
                long now = new Date().getTime();
                if (now - lastUpdateTime > interval) {
                    requestAddressLocation(target);
                    lastUpdateTime = new Date().getTime();
                }

            }
        });
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                Log.d(LOG_TAG, location.getLatitude() + " - " + location.getLongitude());
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                                location.getLongitude()));
                mOriginalMarker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
                if (isMoveCameraAllow) {
                    moveCamera(center);
                    requestAddressLocation(location);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    public void requestAddressLocation(Location location) {
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> yourAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (yourAddresses.size() > 0) {
                String[] address = new String[]{yourAddresses.get(0).getAddressLine(0), yourAddresses.get(0).getAddressLine(1)};
                EventBus.getDefault().post(new MapEvent(address));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestAddressLocation(LatLng location) {
        Intent intent = new Intent(mContext, GPSAddressService.class);
        intent.putExtra(LATITUDE, location.latitude);
        intent.putExtra(LONGITUDE, location.longitude);
        mContext.startService(intent);
    }

    public LocationListener getLocationListener() {
        return mLocationListener;
    }

    public void moveCamera(double latitude, double longitude) {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(latitude,
                        longitude));
        moveCamera(center);

    }

    public void moveCamera(LatLng coordenates) {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(coordenates);
        moveCamera(center);
    }

    public void moveCamera(Location location) {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                        location.getLongitude()));
        moveCamera(center);


    }

    public void putMarkerAtCenter() {
        isMoveCameraAllow = true;
        if (mOriginalMarker != null) {
            moveCamera(mOriginalMarker.getPosition());
        }
    }

    private void moveCamera(CameraUpdate center) {
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        Log.d(LOG_TAG, mMap.getCameraPosition().toString());
    }

    public void getLocationThroughAddress(String query) {
        List<Address> addresses;
        isMoveCameraAllow = false;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocationName(query, 1);

            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                String[] address = new String[]{addresses.get(0).getAddressLine(0), addresses.get(0).getAddressLine(1)};
                EventBus.getDefault().post(new MapEvent(address));
                LatLng latLng = new LatLng(latitude,
                        longitude);

                moveCamera(latLng);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MarkerOptions getPosition() {
        return mOriginalMarker;
    }
}

