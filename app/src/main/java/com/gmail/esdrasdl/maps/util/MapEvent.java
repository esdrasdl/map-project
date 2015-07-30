package com.gmail.esdrasdl.maps.util;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by esdras on 26/07/15.
 */
public class MapEvent {
    String[] mAddress;
    LatLng mLatLng;

    public MapEvent(String[] address, LatLng latLng) {
        mAddress = address;
        mLatLng = latLng;
    }


    public String[] getAddress() {
        return mAddress;
    }

    public void setAddress(String[] address) {
        mAddress = address;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    @Override
    public String toString() {
        return "MapEvent{" +
                "mAddress='" + mAddress + '\'' +
                '}';
    }
}
