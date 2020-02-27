package com.example.svandroidnuevo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.kwabenaberko.openweathermaplib.constants.Lang;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.mklimek.sslutilsandroid.SslUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private final static int MY_PERMISSIONS_GPS_FINE_LOCATION = 1;
    Intent mServiceIntent;

    private final String TAG = getClass().getSimpleName();
    private ArrayList<String> mNames;
    private boolean mListSimple = false;
    private ListView lv = null;
    private MyAdapter myadapter;
    private LocationManager mLocManager;
    private Location mCurrentLocation;
    private static final Integer MY_PERMISSIONS_GPS = 1;

    public static ArrayList<HelperParser.Ruta> mRutas;
    private String[] mCategorias = new String[224];
    private String[] mInicio = new String[224];

    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        TestSSL();
        //weatherInfo();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lista);
        this.setTitle(R.string.Titulo);

        mNames = new ArrayList<String>();
        mNames.add("Android");
        mNames.add("iPhone");
        mNames.add("Nokia");
        mNames.add("Xiaomi");
        mNames.add("Lenovo");
        mNames.add("Huawei");
        mNames.add("Zte");
        mNames.add("Motorola");
        mNames.add("Dell");

        if (mListSimple) {
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mNames);

            lv.setAdapter(adapter);

        } else {
            //myadapter = new MyAdapter(this, R.layout.descripcion_lista, mRutas);
            //lv.setAdapter(myadapter);
        }
        //SI PULSAS QUE SALDRA UN MENSAJITO
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, PulsarLista.class);
                HelperParser.Ruta rutita = mRutas.get(position);
                intent.putExtra("rutaActual", rutita);
                startActivity(intent);

                //Toast.makeText(MainActivity.this, "Has pulsado: " + mNames.get(position), Toast.LENGTH_LONG).show();
            }
        });


        /*  SERVICIOS


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(HelperGlobal.INTENT_LOCALIZATION_ACTION));


        Button bt1 = findViewById(R.id.btStart);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
            }
        });

        Button bt2 = findViewById(R.id.btStop);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(mServiceIntent);
            }
        });

        // Ask user permission for location.
        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_GPS_FINE_LOCATION);

        } else {
            startService();
        }*/
    }

    // Este receiver gestiona mensajes recibidos con el intent 'location-event-position'
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(HelperGlobal.KEY_MESSAGE);
            Log.d(TAG, "BroadcastReceiver::Got message: " + message);
        }
    };

    @Override
    protected void onDestroy() {

        Log.i(TAG, "Activity onDestroy!");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);


        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(MainActivity.this, new  String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_GPS);
        }else{
            Toast.makeText(getApplicationContext(), "[LOCATION] Permission granted in the past!", Toast.LENGTH_SHORT).show();
            startLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1: {
                if (grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
                    startLocation();
                    //startService();
                }else{
                    Toast.makeText(getApplicationContext(), "Permission denied by used", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /*  REQUEST PERMISSIONS DE SERVICIOS

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_GPS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted by user
                    Toast.makeText(getApplicationContext(), "GPS Permission granted!",
                            Toast.LENGTH_SHORT).show();

                    startService();

                } else {
                    // permission denied
                    Toast.makeText(getApplicationContext(),
                            "Permission denied by user!", Toast.LENGTH_SHORT).show();
                }
                return;

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }*/

    public void startService() {

        mServiceIntent = new Intent(getApplicationContext(), MyService.class);
        startService(mServiceIntent);
    }

    @SuppressWarnings({"MissingPermission"})
    private  void startLocation(){

        mLocManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //LO QUE HACE ES LLEVARTE  A LOS SETTINGS PARA QUE ACTIVES EL GPS
        if(! mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        }else{
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,300,this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class MyAdapter extends BaseAdapter {

        private Context context;
        private  int layout;
        private ArrayList<HelperParser.Ruta> rutas;

        public MyAdapter(Context context, int layout, ArrayList<HelperParser.Ruta> rutas) {
            this.context = context;
            this.layout = layout;
            this.rutas = rutas;
        }

        @Override
        public int getCount() {return this.rutas.size();}

        @Override
        public Object getItem(int i) {
            return this.rutas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup ViewGroup) {
            //Copiamos la vista
            View v = view;
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);

            v = layoutInflater.inflate(R.layout.descripcion_lista, null);
            TextView textView1 = (TextView) v.findViewById(R.id.textNom);
            textView1.setText(rutas.get(i).getmName());
            TextView textView2 = (TextView) v.findViewById(R.id.textKms);
            textView2.setText(rutas.get(i).getmLongitud().toString() + " km");

            //double[] loc =UTM2LatLon.transformarLatitudLongitud(UTM2LatLon.crearCadena(rutas.get(i).mLocalizaciones[0].getLat(),rutas.get(i).mLocalizaciones[0].getLat()));
            //textView2.setText("Latitud: " + loc[0] + ", Longitud: " + loc[1]);

            TextView textView3 = (TextView) v.findViewById(R.id.textValoracion);
            textView3.setText(rutas.get(i).getmCategoria());
            ImageView imageView = v.findViewById(R.id.imageView2);


            if(rutas.get(i).getmDescTiempo()!=null) {
                if (rutas.get(i).getmDescTiempo().equalsIgnoreCase("cielo claro")) {
                    imageView.setImageResource(R.drawable.season);
                } else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase("nubes dispersas")) {
                    imageView.setImageResource(R.drawable.cloud);
                } else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase("algo de nubes")) {
                    imageView.setImageResource(R.drawable.forecast);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase("nubes rotas")) {
                    imageView.setImageResource(R.drawable.forecast);
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher_background);
                }
            }

            return v;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflowmenu, menu);

        final MenuItem searchItem = menu.findItem(R.id.buscar);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //permite modificar el hint que el EditText muestra por defecto
        searchView.setQueryHint(getText(R.string.buscar));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "hola", Toast.LENGTH_SHORT).show();
                //se oculta el EditText
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.ordenar){
        }else if(id == R.id.nombre){
            Collections.sort(mRutas, new PulsarLista.cusComparatorNom());
        }else if(id == R.id.longitud){
            Collections.sort(mRutas, new PulsarLista.cusComparatorLong());
        }else if(id == R.id.categoria){
            Collections.sort(mRutas, new PulsarLista.cusComparatorCat());
        }else if(id == R.id.fav){
            Toast.makeText(this,"Opcion FAVORITO", Toast.LENGTH_SHORT).show();
            Intent mIntent = new Intent(this, ListaFavs.class);
            //mIntent.putParcelableArrayListExtra(KEY_ARRAY, mArray);
            startActivity(mIntent);
        }else if(id == R.id.ajustes){
            Toast.makeText(this,"Opcion AJUSTES", Toast.LENGTH_SHORT).show();
        }else if(id == R.id.ayuda){
            Toast.makeText(this,"Opcion AYUDA", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void TestSSL () {

        OkHttpClient client = new OkHttpClient();
        SSLContext sslContext = SslUtils.getSslContextForCertificateFile(this, "comunidad-madrid.pem");
        client.setSslSocketFactory(sslContext.getSocketFactory());


        Request request = new Request.Builder()
                .url("https://datos.comunidad.madrid/catalogo/dataset/66784709-f106-4906-bf37-8c46c6033f54/resource/d37614e5-22c1-41b6-9334-66e7ee61975c/download/spacmsendasnaturaleza.json")
                .build();

        mPd = new ProgressDialog(MainActivity.this);
        mPd.setTitle("Adquiriendo datos...");
        mPd.show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {


                Log.e(TAG, request.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {

                    Log.d(TAG, response.toString());

                    HelperParser myparser = new HelperParser();
                    mRutas = myparser.parseRutas(response.body().string());
                    Log.d(TAG,String.valueOf(mRutas.isEmpty()));
                    for(int i = 0; i<mRutas.size();i++){
                        mCategorias[i]=(mRutas.get(i).getmCategoria());
                    }

                    for (HelperParser.Ruta mRuta : mRutas) {
                        HelperParser.Localizacion[] localizacion = mRuta.getmLocalizacion();
                        for (HelperParser.Localizacion localizacion1 : localizacion) {
                            double[] loc =UTM2LatLon.transformarLatitudLongitud(UTM2LatLon.crearCadena(localizacion1.getLat(),localizacion1.getLon()));
                            //System.out.println(mRuta.getmName() + ", Latitud: " + loc[0] + ", Longitud: " + loc[1]);
                            weatherInfo(loc[0], loc[1], mRuta);
                            //System.out.println(mRuta.getmDescTiempo());
                            /*try {
                                Thread.sleep(40);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                            break;
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myadapter=new MyAdapter(MainActivity.this, R.layout.descripcion_lista,mRutas);
                            lv.setAdapter(myadapter);
                        }
                    });

                    mPd.dismiss();

                }
            }
        });

    }

    private void weatherInfo(double lat, double lon, final HelperParser.Ruta ruta){


        OpenWeatherMapHelper helper = new OpenWeatherMapHelper(getString(R.string.OPEN_WEATHER_MAP_API_KEY));

        helper.setUnits(Units.METRIC);

        helper.setLang(Lang.SPANISH);

        helper.getCurrentWeatherByGeoCoordinates(lat,  lon, new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                ruta.setmTemperatura(currentWeather.getMain().getTempMax());
                ruta.setmDescTiempo(currentWeather.getWeather().get(0).getDescription());

                Log.v(TAG, "Coordinates: " + currentWeather.getCoord().getLat() + ", "+currentWeather.getCoord().getLon() +"\n"
                        +"Weather Description: " + currentWeather.getWeather().get(0).getDescription() + "\n"
                        +"Temperature: " + currentWeather.getMain().getTempMax()+"\n"
                        +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                        +"City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry()
                );
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v(TAG, throwable.getMessage());
            }


        });


    }

}
