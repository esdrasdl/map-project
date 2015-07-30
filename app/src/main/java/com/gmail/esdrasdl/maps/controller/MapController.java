package com.gmail.esdrasdl.maps.controller;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.gmail.esdrasdl.maps.data.FavoriteEntity;
import com.gmail.esdrasdl.maps.data.net.APIClient;
import com.gmail.esdrasdl.maps.service.GPSAddressService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;


/**
 * Created by esdras on 26/07/15.
 */
public class MapController {
    private static final String LOG_TAG = MapController.class.getSimpleName();
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String USE_LATLON = "USE_LATLON";
    public static final String QUERY = "QUERY";

    private GoogleMap mMap;
    private Context mContext;
    private boolean isMoveCameraAllow = true;
    private LocationListener mLocationListener;
    private MarkerOptions mOriginalMarker;
    private LatLng mLastLatLng;
    private long lastUpdateTime = 0;
    private final long interval = 2 * 1000;


    public GoogleMap.OnCameraChangeListener setCameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                isMoveCameraAllow = false;
                long now = new Date().getTime();
                if (now - lastUpdateTime > interval) {
                    mLastLatLng = cameraPosition.target;
                    requestAddressLocation(mLastLatLng);
                    lastUpdateTime = new Date().getTime();
                }

            }
        };
    }

    public void setIsMoveCameraAllow(boolean isMoveCameraAllow) {
        this.isMoveCameraAllow = isMoveCameraAllow;
    }

    public boolean isMoveCameraAllow() {
        return isMoveCameraAllow;
    }

    public MapController(Context context, GoogleMap map) {
        mContext = context;
        mMap = map;
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
        Intent intent = new Intent(mContext, GPSAddressService.class);
        intent.putExtra(LATITUDE, location.getLatitude());
        intent.putExtra(LONGITUDE, location.getLongitude());
        intent.putExtra(USE_LATLON, true);
        mContext.startService(intent);
    }

    public void requestAddressLocation(LatLng location) {
        Intent intent = new Intent(mContext, GPSAddressService.class);
        intent.putExtra(LATITUDE, location.latitude);
        intent.putExtra(LONGITUDE, location.longitude);
        intent.putExtra(USE_LATLON, true);
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

    public void moveCamera(LatLng coordinates) {

        CameraUpdate center =
                CameraUpdateFactory.newLatLng(coordinates);
        moveCamera(center);

    }

    public void moveCamera(Location location) {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                        location.getLongitude()));
        moveCamera(center);

    }

    public void getCurrentPosition() {
        isMoveCameraAllow = true;
        if (mOriginalMarker != null) {
            requestAddressLocation(mOriginalMarker.getPosition());
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
        isMoveCameraAllow = false;
        Intent intent = new Intent(mContext, GPSAddressService.class);
        intent.putExtra(QUERY, query);
        intent.putExtra(USE_LATLON, false);
        mContext.startService(intent);
    }

    public LatLng getLastPosition() {
        return mLastLatLng;
    }


    public void getFavoriteList() {
        APIClient client = new APIClient(mContext);
        client.getFavoriteList("M9e1vpTd");
    }


    public FavoriteEntity buildFavoriteLocation(String address) {
        FavoriteEntity favoriteEntity = new FavoriteEntity();
        favoriteEntity.setName(address);
        if (mLastLatLng != null) {
            favoriteEntity.setLatitude(mLastLatLng.latitude);
            favoriteEntity.setLongitude(mLastLatLng.longitude);
        }
        return favoriteEntity;
    }
}

