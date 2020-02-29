package com.example.svandroidnuevo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListaFavs extends AppCompatActivity {

    private ArrayList<HelperParser.Ruta> mArray = new ArrayList<HelperParser.Ruta>();

    private final String TAG = getClass().getSimpleName();
    public static final String SHARED_PREFS = "sharedPrefs";
    private MyAdapter myadapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_favs);

        lv = findViewById(R.id.listaFavs);

        loadData();

        myadapter = new MyAdapter(this, R.layout.descripcion_lista, mArray);
        lv.setAdapter(myadapter);


        //myadapter = new MyAdapter(this, R.layout.descripcion_lista, mArray);
        //lv.setAdapter(myadapter);

        /*ArrayAdapter<HelperParser.Ruta> adapter =
                    new ArrayAdapter<HelperParser.Ruta>(this, android.R.layout.simple_list_item_1, mArray);

        lv.setAdapter(adapter);*/


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

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(mArray);
        gson = new Gson();
        json = sharedPreferences.getString(PulsarLista.KEY_ARRAY, "");

        Type founderListType = new TypeToken<ArrayList<HelperParser.Ruta>>(){}.getType();

        ArrayList<HelperParser.Ruta> restoreArray = gson.fromJson(json, founderListType);

        if(restoreArray!=null) {
            for (int i = mArray.size(); i < restoreArray.size(); i++) {
                Log.d(TAG, restoreArray.get(i).getmName());
                mArray.add(restoreArray.get(i));
            }
        }



    }

}
