package com.gmail.esdrasdl.maps.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by esdras on 29/07/15.
 */
public class FavoriteEntity {


    @SerializedName("name")
    private String mName;

    @SerializedName("latitude")
    private Double mLatitude;

    @SerializedName("longitude")
    private Double mLongitude;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    @Override
    public String toString() {
        return "FavoriteEntity{" +
                "mName='" + mName + '\'' +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                '}';
    }
}
