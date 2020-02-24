package com.example.svandroidnuevo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyService extends Service implements LocationListener {

    private final String TAG = getClass().getSimpleName();
    private LocationManager mLocManager = null;


    public MyService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Servicio creado");

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "1",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @SuppressWarnings({"MissingPermission"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Set Foreground service

        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Foreground Service")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        startForeground(1, notification);


        // Set GPS Listener
        mLocManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1, 300,
                this);

        Log.d(TAG, "Listener set");

        return START_STICKY;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        if (mLocManager != null) {
            mLocManager.removeUpdates(this);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "new location");
        Toast.makeText(this, "New Location", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(HelperGlobal.INTENT_LOCALIZATION_ACTION);
        intent.putExtra(HelperGlobal.KEY_MESSAGE, "New Location");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
