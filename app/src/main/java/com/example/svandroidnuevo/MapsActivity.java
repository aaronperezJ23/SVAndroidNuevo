package com.example.svandroidnuevo;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import static com.example.svandroidnuevo.MainActivity.mRutas;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static HelperParser.Ruta mRutaActual;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        mRutaActual = (HelperParser.Ruta) intent.getSerializableExtra("rutaLoc");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double[] locInit =UTM2LatLon.transformarLatitudLongitud(UTM2LatLon.crearCadena(mRutaActual.getmLocalizacion()[0].getLat(),mRutaActual.getmLocalizacion()[0].getLon()));

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(5);
        polylineOptions.color(Color.BLUE);


        // Add a marker in local and move the camera
        LatLng local = new LatLng(locInit[0], locInit[1]);
        mMap.addMarker(new MarkerOptions().position(local));
        float zoomLevel = 11.0f;
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(local));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, zoomLevel));

        HelperParser.Localizacion[] localizacion = mRutaActual.getmLocalizacion();
        for (HelperParser.Localizacion localizacion1 : localizacion) {
            double[] loc =UTM2LatLon.transformarLatitudLongitud(UTM2LatLon.crearCadena(localizacion1.getLat(),localizacion1.getLon()));
            //local = new LatLng(loc[0],loc[1]);
            //mMap.addMarker(new MarkerOptions().position(local));
            polylineOptions.add(new LatLng(loc[0],loc[1]));

        }

        Polyline polyline1 = googleMap.addPolyline(polylineOptions);


    }
}
