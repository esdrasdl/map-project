package com.gmail.esdrasdl.maps.util;

/**
 * Created by esdras on 26/07/15.
 */
public class MapEvent {
    String[] mAddress;

    public MapEvent(String[] address) {
        mAddress = address;
    }

    public String[] getAddress() {
        return mAddress;
    }

    public void setAddress(String[] address) {
        mAddress = address;
    }

    @Override
    public String toString() {
        return "MapEvent{" +
                "mAddress='" + mAddress + '\'' +
                '}';
    }
}
