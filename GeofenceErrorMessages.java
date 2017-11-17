package com.jddldesign.angie;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Geofence error codes mapped to error messages.
 */
public class GeofenceErrorMessages {
    /**
     * Prevents instantiation.
     */
    private GeofenceErrorMessages() {}

    /**
     * Returns the error string for a geofencing error code.
     */
    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence Not Available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Geofence, too many pending intents";
            default:
                return "Unknown geofence error: "+Integer.toString(errorCode);
        }
    }
}
