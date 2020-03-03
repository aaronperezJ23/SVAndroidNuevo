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
                    imageView.setImageResource(R.drawable.lluviamoderada);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.niebla))) {
                    imageView.setImageResource(R.drawable.niebla);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.nieve))) {
                    imageView.setImageResource(R.drawable.nieve);
                }else if (rutas.get(i).getmDescTiempo().equalsIgnoreCase(getString(R.string.lluModerada))) {
                    imageView.setImageResource(R.drawable.lluvialigera);
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
