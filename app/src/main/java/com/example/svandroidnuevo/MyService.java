package com.example.svandroidnuevo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.svandroidnuevo.ListaFavs.SHARED_PREFS;

import static com.example.svandroidnuevo.MainActivity.mRutas;
import static com.example.svandroidnuevo.PulsarLista.KEY_ARRAY;

public class MyService extends Service implements LocationListener {

    private final String TAG = getClass().getSimpleName();
    private LocationManager mLocManager = null;
    private ArrayList<HelperParser.Ruta> mArrayRutas = new ArrayList<>();

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
                .setSmallIcon(R.drawable.path)
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

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(mArrayRutas);
        gson = new Gson();
        json = sharedPreferences.getString(KEY_ARRAY, "");

        Type founderListType = new TypeToken<ArrayList<HelperParser.Ruta>>(){}.getType();

        ArrayList<HelperParser.Ruta> restoreArray = gson.fromJson(json, founderListType);

        if(restoreArray!=null) {
            for (int i = 0; i < restoreArray.size(); i++) {
                mArrayRutas.add(restoreArray.get(i));

            }
        }

    }

    private void createNotificationChannel2() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CANAL";
            String description = "DESCRIPTION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mArrayRutas.clear();
        loadData();

        for (HelperParser.Ruta mRuta : mArrayRutas) {
            HelperParser.Localizacion[] localizacion = mRuta.getmLocalizacion();
            for (HelperParser.Localizacion localizacion1 : localizacion) {
                double[] loc =UTM2LatLon.transformarLatitudLongitud(UTM2LatLon.crearCadena(localizacion1.getLat(),localizacion1.getLon()));
                Location locatis = new Location("");
                locatis.setLatitude(loc[0]);
                locatis.setLongitude(loc[1]);
                if(location.distanceTo(locatis)<1000){
                    createNotificationChannel2();

                    // Create an explicit intent for an Activity in your app
                    Intent intent = new Intent(this, PulsarLista.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("rutaActual", mRuta);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "2")
                            .setSmallIcon(R.drawable.path)
                            .setContentTitle(getString(R.string.cercanaRuta))
                            .setContentText(mRuta.getmName() + getString(R.string.estasCerca))
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(2, builder.build());

                }
                break;
            }

        }
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
