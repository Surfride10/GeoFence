package com.jddldesign.GeoFence;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;


public class Constants {

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 15000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();
    static {
        // Lestats
        LANDMARKS.put("Lestats", new LatLng(32.7589366,-117.1463945));
        // Sprouts
        LANDMARKS.put("Sprouts", new LatLng(32.7533799,-117.1458968));
        //Boulevard Fitness
        LANDMARKS.put("BlvdFit", new LatLng(32.755707,-117.14191));
    }
}