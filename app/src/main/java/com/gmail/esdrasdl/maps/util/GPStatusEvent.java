package com.gmail.esdrasdl.maps.util;

/**
 * Created by esdras on 27/07/15.
 */
public class GPStatusEvent {
    private boolean mStatus;

    public GPStatusEvent(boolean status) {
        mStatus = status;
    }

    public boolean getStatus() {
        return mStatus;
    }

    @Override
    public String toString() {
        return "GPStatusEvent{" +
                "mStatus=" + mStatus +
                '}';
    }
}
