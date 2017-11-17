package com.jddldesign.GeoFence;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.jddldesign.angie.GeofenceErrorMessages;

import java.util.ArrayList;
import java.util.Map;

public class GeofenceService extends Service
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private GeofencingRequest mRequest;
    protected ArrayList<Geofence> mGeofenceList=null;
    protected GoogleApiClient mGoogleApiClient=null;
    protected PendingIntent mGeofencePendingIntent=null;
    public final static String BUNDLED_LISTENER = "listener";
    private ResultReceiver receiver=null;

    public GeofenceService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGeofenceList = new ArrayList<Geofence>();
        populateGeofenceList();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String logName = intent.getStringExtra("logName");
        receiver = intent.getParcelableExtra(GeofenceService.BUNDLED_LISTENER);

        if (mGoogleApiClient.isConnected()) {
            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(this);
            } catch (SecurityException e) {
                Utility.ExceptionHandler(this,e);
            }
        }
        if (receiver!=null) {
            Bundle bundle = new Bundle();
            bundle.putString("status", "onStartCommand");
            receiver.send(Activity.RESULT_OK, bundle);
        }
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder =new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }


    @Override
    public void onConnected(Bundle connectionHint)
    {
        Bundle bundle = new Bundle();
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
            bundle.putString("status", "onConnected addedGeofences");
        }
        catch (SecurityException e) {
            Utility.ExceptionHandler(this,e);
            bundle.putString("status", "addGeofences Exception: "+e.getMessage());
        }
        catch (Exception e) {
            Utility.ExceptionHandler(this, e);
            bundle.putString("status", "addGeofences Exception: " + e.getMessage());
        }
        if (receiver!=null)
            receiver.send(Activity.RESULT_OK, bundle);

    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, status.getStatusCode());
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (receiver!=null){
            Bundle bundle = new Bundle();
            bundle.putString("status", "buildGoogleApiClient");
            receiver.send(Activity.RESULT_OK, bundle);
        }

    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness (0)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |  Geofence.GEOFENCE_TRANSITION_EXIT )
                    .build());
        }
        if (receiver!=null) {
            Bundle bundle = new Bundle();
            bundle.putString("status", "populateGeofenceList");
            receiver.send(Activity.RESULT_OK, bundle);
        }
    }

    private PendingIntent getGeofencePendingIntent()
    {
        Bundle bundle = new Bundle();
        if (mGeofencePendingIntent == null) {
            mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, GeofenceReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

            bundle.putString("status", "getGeofencePendingIntent Created");
        }
        else
            bundle.putString("status", "getGeofencePendingIntent Exists");

        if (receiver!=null)
            receiver.send(Activity.RESULT_OK, bundle);

        return mGeofencePendingIntent;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (receiver!=null) {
            Bundle bundle = new Bundle();
            bundle.putString("status", "onConnectionFailed " + result.getErrorMessage());
            receiver.send(Activity.RESULT_CANCELED, bundle);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (receiver!=null) {
            StringBuilder sb=new StringBuilder("onConnectionSuspended: ");
            Bundle bundle = new Bundle();
            if (cause==CAUSE_SERVICE_DISCONNECTED)
                sb.append("Service Disconnected");
            else if (cause==CAUSE_NETWORK_LOST)
                sb.append("Network Lost");
            else
                sb.append(" cause: "+Integer.toString(cause));
            bundle.putString("status", sb.toString());
            receiver.send(Activity.RESULT_OK, bundle);
        }
    }

}