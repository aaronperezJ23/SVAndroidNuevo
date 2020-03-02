package com.example.svandroidnuevo;

import android.location.Location;
import android.location.LocationManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Distancia {
    public static HelperParser.Ruta[] mRutas;
    private List<HelperParser.Localizacion> mLoc;
    private LocationManager mLocManager = null;
    Location mCurrentLocation;
    Location rutaLoc = new Location("");

    public void compararDis(Location location){
        /*double compararLoc;
    for(HelperParser.Ruta mRuta : mRutas){
        HelperParser.Localizacion[] localizacion = mRuta.getmLocalizacion();
        for (HelperParser.Localizacion localizacion1 : localizacion) {
            rutaLoc.setLatitude(localizacion1.getLat());
            rutaLoc.setLongitude(localizacion1.getLon());
            break;
        }
    }*/


        float distanci = mCurrentLocation.distanceTo(location);

        // Order Array
        Collections.sort(mLoc, new Comparator<HelperParser.Localizacion>(){
            @Override
            public int compare(HelperParser.Localizacion o1, HelperParser.Localizacion o2) {
                return o1.distance.compareTo(o2.distance);
            }

        });

        
    }

}
