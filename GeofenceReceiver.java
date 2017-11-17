package com.jddldesign.GeoFence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        com.jddldesign.GeoFence.ObservableObject.getInstance().updateValue(intent);

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        StringBuilder sb=new StringBuilder();
        if (event.hasError()) {
            sb.append("geoFenceEvent has error: " + GeofenceStatusCodes.getStatusCodeString(event.getErrorCode()));
        }
        else {

            sb.append("Location ");
            for (Geofence geofence : event.getTriggeringGeofences()) {
                sb.append(geofence.getRequestId()+" ");
            }
            if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_DWELL)
                sb.append("DWELLL");
            else if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER)
                sb.append("ENTER");
            else if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_EXIT)
                sb.append("EXIT");
            else if (event.getGeofenceTransition() == Geofence.NEVER_EXPIRE)
                sb.append("NEVER EXPIRE");
            else
                sb.append("UNKNOWN EVENT:" + Integer.toString(event.getGeofenceTransition()));
        }

        final Toast toast;
        toast=Toast.makeText(context, sb.toString(),Toast.LENGTH_LONG);
        toast.show();

        new CountDownTimer(9000, 1000)
        {
            public void onTick(long millisUntilFinished) {toast.show();}
            public void onFinish() {toast.show();}

        }.start();
    }
}