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
import android.content.SharedPreferences;
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
import java.util.Collections;

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
    public static ArrayList<HelperParser.Ruta> mRutasAux = new ArrayList<>();

    private ProgressDialog mPd;

    private static final String SHARED_PREFS = "sharedPrefs";

    private static final String CERCANIA = "cercania";
    private static final String LONGITUD = "lonigtud";

    private static final String ENP_SW = "enpsw";
    private static final String SV_SW = "svsw";
    private static final String FM_SW = "fmsw";
    private static final String VP_SW = "vpsw";

    private static int mCercania;
    private static int mLongitud;

    private static Boolean mENP;
    private static Boolean mSV;
    private static Boolean mFM;
    private static Boolean mVP;

    @Override
    protected void onResume() {
        super.onResume();
        if(mRutas!=null) {
            loadData();
            TestSSL();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        loadData();
        TestSSL();
        //weatherInfo();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lista);
        this.setTitle(R.string.Titulo);

        //Te lleva a la descripcion de la lista
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            Intent intent = new Intent(MainActivity.this, PulsarLista.class);
            HelperParser.Ruta rutita = mRutasAux.get(position);
            intent.putExtra("rutaActual", rutita);
            startActivity(intent);

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
*/
        // Ask user permission for location.
        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_GPS_FINE_LOCATION);

        } else {
            startService();
        }
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mCercania = sharedPreferences.getInt(CERCANIA, 100);
        mLongitud = sharedPreferences.getInt(LONGITUD, 100);

        mENP = sharedPreferences.getBoolean(ENP_SW, true);
        mSV = sharedPreferences.getBoolean(SV_SW, true);
        mFM = sharedPreferences.getBoolean(FM_SW, true);
        mVP = sharedPreferences.getBoolean(VP_SW, true);

        Toast.makeText(this, String.valueOf(mCercania), Toast.LENGTH_SHORT).show();
    }

    // Este receiver gestiona mensajes recibidos con el intent 'location-event-position'
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(HelperGlobal.KEY_MESSAGE);
            Log.d(TAG, getString(R.string.brotcastReciv) + message);
        }
    };

    @Override
    protected void onDestroy() {
        Log.i(TAG, getString(R.string.onDestr));
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
            Toast.makeText(getApplicationContext(), R.string.inthepastPermi, Toast.LENGTH_SHORT).show();
            startLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1: {
                if (grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), R.string.grantedPermission, Toast.LENGTH_LONG).show();
                    startLocation();
                    //startService();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.deniedPermission, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

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

            TextView textView3 = (TextView) v.findViewById(R.id.textValoracion);
            textView3.setText(rutas.get(i).getmCategoria());
            ImageView imageView = v.findViewById(R.id.imageView2);

            if(rutas.get(i).getmDescTiempo()!=null) {
                if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.claro))) {
                    imageView.setImageResource(R.drawable.cieloclaro);
                } else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.dispersas))) {
                    imageView.setImageResource(R.drawable.nubess);
                } else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.algoNubes))) {
                    imageView.setImageResource(R.drawable.algonubes);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.rotas))) {
                    imageView.setImageResource(R.drawable.nubesrotas);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.nubes))) {
                    imageView.setImageResource(R.drawable.nubes);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.lluLigera))) {
                    imageView.setImageResource(R.drawable.lluvialigera);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.niebla))) {
                    imageView.setImageResource(R.drawable.niebla);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.nieve))) {
                    imageView.setImageResource(R.drawable.nieve);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.lluModerada))) {
                    imageView.setImageResource(R.drawable.lluviamoderada);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.tormenta))) {
                    imageView.setImageResource(R.drawable.tormenta);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.lloLigera))) {
                    imageView.setImageResource(R.drawable.llovizna);
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
        }else if(id == R.id.nombre) {
            Collections.sort(mRutas, new OrdenarLista.cusComparatorNom());
        }else if(id == R.id.longitud){
            Collections.sort(mRutas, new OrdenarLista.cusComparatorLong());
        }else if(id == R.id.categoria){
            Collections.sort(mRutas, new OrdenarLista.cusComparatorCat());
        }else if(id == R.id.filtra) {
            Intent mIntent = new Intent(this, FiltrosActivity.class);
            startActivity(mIntent);
        }else if(id == R.id.fav){
            Intent mIntent = new Intent(this, ListaFavs.class);
            startActivity(mIntent);
        }else if(id == R.id.ayuda){
            Intent mIntent = new Intent(this, Ayuda.class);
            startActivity(mIntent);
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
        mPd.setTitle(getString(R.string.adquiriendo_dat));
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
                    //mPd.dismiss();
                    if(mRutasAux!=null){
                        mRutasAux.clear();
                    }
                    for (HelperParser.Ruta mRuta : mRutas) {
                        if(mRuta.getmLongitud()<mLongitud){
                            if(mFM && mRuta.getmCategoria().equalsIgnoreCase(getString(R.string.fm))) {
                                mRutasAux.add(mRuta);
                            }
                            if(mSV && mRuta.getmCategoria().equalsIgnoreCase(getString(R.string.sv))) {
                                mRutasAux.add(mRuta);
                            }
                            if(mENP && mRuta.getmCategoria().equalsIgnoreCase(getString(R.string.enp))) {
                                mRutasAux.add(mRuta);
                            }
                            if(mVP && mRuta.getmCategoria().equalsIgnoreCase(getString(R.string.rvp))) {
                                mRutasAux.add(mRuta);
                            }
                            Log.d(TAG, mRuta.getmCategoria());
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        myadapter=new MyAdapter(MainActivity.this, R.layout.descripcion_lista,mRutasAux);
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

                Log.v(TAG, getString(R.string.weather, currentWeather.getCoord().getLat(), currentWeather.getCoord().getLon(),
                        currentWeather.getCoord().getLon(), currentWeather.getMain().getTempMax(), currentWeather.getWind().getSpeed(),
                        currentWeather.getName(), currentWeather.getSys().getCountry())
                );
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v(TAG, throwable.getMessage());
            }
        });
    }

}
