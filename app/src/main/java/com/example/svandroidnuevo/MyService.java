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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.svandroidnuevo.MainActivity.mRutas;

public class MyService extends Service implements LocationListener {

    private final String TAG = getClass().getSimpleName();
    private LocationManager mLocManager = null;
    Location rutaLoc = new Location("");

    public MyService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, getString(R.string.servCreado));

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

        /*float comparar = 0;
        for(HelperParser.Ruta mRuta : mRutas){
            HelperParser.Localizacion[] localizacion = mRuta.getmLocalizacion();
            for (HelperParser.Localizacion localizacion1 : localizacion) {
                double[] loc =UTM2LatLon.transformarLatitudLongitud(UTM2LatLon.crearCadena(localizacion1.getLat(),localizacion1.getLon()));
                rutaLoc.setLatitude(loc[0]);
                rutaLoc.setLongitude(loc[1]);
                comparar = location.distanceTo(rutaLoc);
                break;
            }
        }*/
        
        
        Toast.makeText(this, "New Location", Toast.LENGTH_SHORT).show();

        //float distancia = location.distanceTo(rutaLoc);
        Intent intent = new Intent(HelperGlobal.INTENT_LOCALIZATION_ACTION);
        intent.putExtra(HelperGlobal.KEY_MESSAGE, R.string.nuevaLocalizacion);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d(TAG, getString(R.string.NewLocali) +
                location.getLatitude() + ", " +
                location.getLongitude());
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
