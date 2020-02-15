package com.example.svandroidnuevo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
    private final String TAG = getClass().getSimpleName();
    private ArrayList<String> mNames;
    private boolean mListSimple = false;
    private ListView lv = null;
    private MyAdapter myadapter;
    private LocationManager mLocManager;
    private Location mCurrentLocation;
    private static final Integer MY_PERMISSIONS_GPS = 1;


    private ArrayList<HelperParser.Ruta> mRutas;
    private String[] mCategorias = new String[224];
    private String[] mInicio = new String[224];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        TestSSL();

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
                startActivity(intent);
                //Toast.makeText(MainActivity.this, "Has pulsado: " + mNames.get(position), Toast.LENGTH_LONG).show();
            }
        });
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
                }else{
                    Toast.makeText(getApplicationContext(), "Permission denied by used", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
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
            textView2.setText(rutas.get(i).getmLongitud().toString());
            TextView textView3 = (TextView) v.findViewById(R.id.textValoracion);
            textView3.setText(rutas.get(i).getmCategoria());

            return v;
        }
    }

 /*   public class MyAdapter extends BaseAdapter {

        private Context context;
        private  int layout;
        private ArrayList<String> names;


        public MyAdapter(Context context, int layout, ArrayList<String> names) {
            this.context = context;
            this.layout = layout;
            this.names = names;
        }

        @Override
        public int getCount() {return this.names.size();}

        @Override
        public Object getItem(int i) {
            return this.names.get(i);
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
            textView1.setText(names.get(i));
            TextView textView2 = (TextView) v.findViewById(R.id.textKms);
            textView2.setText(names.get(i));
            TextView textView3 = (TextView) v.findViewById(R.id.textValoracion);
            textView3.setText(names.get(i));

            return v;
        }
    }*/

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

    //Para que se ordene la lista dependiendo ya sea de la longitud, nombre o categoria
    public class cusComparatorLong implements Comparator<HelperParser.Ruta> {
        @Override
        public int compare(HelperParser.Ruta o1, HelperParser.Ruta o2) {
            return o1.getmLongitud().compareTo(o2.getmLongitud());
        }
    }
    public class cusComparatorNom implements Comparator<HelperParser.Ruta> {
        @Override
        public int compare(HelperParser.Ruta o1, HelperParser.Ruta o2) {
            return o1.getmName().compareTo(o2.getmName());
        }
    }
    public class cusComparatorCat implements Comparator<HelperParser.Ruta> {
        @Override
        public int compare(HelperParser.Ruta o1, HelperParser.Ruta o2) {
            return o1.getmCategoria().compareTo(o2.getmCategoria());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.ordenar){
        }else if(id == R.id.nombre){
            Collections.sort(mRutas, new cusComparatorNom());
        }else if(id == R.id.longitud){
            Collections.sort(mRutas, new cusComparatorLong());
        }else if(id == R.id.categoria){
            Collections.sort(mRutas, new cusComparatorCat());
        }else if(id == R.id.fav){
            Toast.makeText(this,"Opcion FAVORITO", Toast.LENGTH_SHORT).show();
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

                    //Log.d(TAG, response.body().string());
                    Log.d(TAG, response.toString());

                    HelperParser myparser = new HelperParser();
                    mRutas = myparser.parseRutas(response.body().string());
                    Log.d(TAG,String.valueOf(mRutas.isEmpty()));
                    for(int i = 0; i<mRutas.size();i++){
                        //Log.d("hola", response);
                        mCategorias[i]=(mRutas.get(i).getmCategoria());
                        //mLocalizaciones.add(mRutas.get(i).getmLocalizacion());
                        //mInicio[i]=(mRutas.get(i).getmInicio());
                        //Log.d(TAG, mRutas.get(i).getmName());
                        //Log.d(TAG, String.valueOf(mRutas.get(i).getmLongitud()));
                        //Log.d(TAG, mRutas.get(i).getmCategoria());
                        //Log.d(TAG, mRutas.get(i).getmENP());

                    }

                    Arrays.sort(mCategorias);

                    //for (int i=0; i<mCategorias.length;i++){
                      //  Log.d(TAG,mCategorias[i]);
                    //}

                    for (HelperParser.Ruta mRuta : mRutas) {
                        HelperParser.Localizacion[] localizacion = mRuta.getmLocalizacion();
                        for (HelperParser.Localizacion localizacion1 : localizacion) {
                            //System.out.println(mRuta.getmName() + "---" + localizacion1.getLat());

                        }
                    }
                    //Arrays.sort(mInicio);

                    //for (int i=0; i<mInicio.length;i++){
                      //  Log.d(TAG,mInicio[i]);
                    //}

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myadapter=new MyAdapter(MainActivity.this, R.layout.descripcion_lista,mRutas);
                            lv.setAdapter(myadapter);
                        }
                    });



                }
            }
        });

    }
}
