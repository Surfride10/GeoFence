package com.jddldesign.GeoFence;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/*
public class MainActivity extends Activity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>{
*/
public class MainActivity extends Activity implements Observer{

    private ListView lvStatus=null;
    private List<String> alStatusItems = null;
    private ArrayAdapter arrayAdapterStatus = null;
    private BroadcastReceiver geoReceiver=null;
    private Handler panelHandler=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            ObservableObject.getInstance().addObserver(this);
            //mAddGeofencesButton = (Button) findViewById(R.id.btnAddGeofences);
            lvStatus = (ListView) findViewById(R.id.lvStatus);
            alStatusItems = new ArrayList<String>();
            arrayAdapterStatus = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    alStatusItems);
            lvStatus.setAdapter(arrayAdapterStatus);


            Intent geoIntent = new Intent(this, GeofenceService.class);
            geoIntent.putExtra("logName", "MAIN_ACTIVITY");
            geoIntent.putExtra(GeofenceService.BUNDLED_LISTENER, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);

                    String val = resultData.getString("status");
                    if (resultCode == Activity.RESULT_OK) {
                        AddStatusMessage("[" + val + "]");
                    } else {
                        AddStatusMessage("ERR [" + val + "]");
                    }
                }
            });
            startService(geoIntent);
            }
            catch (Exception e){
                Utility.ExceptionHandler(this, e);
            }
    }

    @Override
    public void update(Observable observable, Object data) {
        try {
            if (data instanceof Intent) {
                GeofencingEvent event = GeofencingEvent.fromIntent((Intent) data);
                StringBuilder sb = new StringBuilder();
                if (event.hasError()) {
                    sb.append("geoFenceEvent has error: " + GeofenceStatusCodes.getStatusCodeString(event.getErrorCode()));
                } else {


                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> tasks =am.getRunningTasks(100); // need depricated version for api 19

                    for (ActivityManager.RunningTaskInfo task : tasks){
                        //arrayAdapterStatus.add(task.topActivity.getPackageName()+"/"+this.getClass().getPackage().getName());
                        if(task.topActivity.getPackageName().equals(this.getClass().getPackage().getName()))
                        {
                            am.moveTaskToFront(task.id, 0);
                            break;
                        }
                    }

                    sb.append("Location ");
                    for (Geofence geofence : event.getTriggeringGeofences()) {
                        sb.append(geofence.getRequestId() + " ");
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
                AddStatusMessage(sb.toString());
            }
        }
        catch (Exception E)
        {
            Utility.ExceptionHandler(this,E);
        }
    }

    public void AddStatusMessage(String message) {
        try {


            if (arrayAdapterStatus.getCount()>100)
                arrayAdapterStatus.remove(arrayAdapterStatus.getItem(0));

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
            StringBuilder sb=new StringBuilder(sdf.format(Calendar.getInstance().getTime()));
            sb.append("\n");
            sb.append(message);

            arrayAdapterStatus.add(sb.toString());
            //arrayAdapterStatus.notifyDataSetChanged();

            lvStatus.setSelection(arrayAdapterStatus.getCount() - 1);
        } catch (Exception E){
            Utility.ExceptionHandler(this,E);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
