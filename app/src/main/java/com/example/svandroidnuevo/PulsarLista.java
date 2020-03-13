package com.example.svandroidnuevo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;

public class PulsarLista extends AppCompatActivity {

    public static HelperParser.Ruta mRutaActual;

    private ArrayList<HelperParser.Ruta> mArray = new ArrayList<HelperParser.Ruta>();

    private final String TAG = getClass().getSimpleName();
    public final static String KEY_ARRAY = "ARRAY_DATA";
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onResume() {
        super.onResume();

        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsar_lista);

        Button mapa = findViewById(R.id.botonMapa);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(getString(R.string.rutaLoc), mRutaActual);
                //intent.putExtra("DESC", "Texto de prueba");
                startActivity(intent);
                //startActivityForResult(intent, 1);
            }
        });

        Intent intent = getIntent();
        mRutaActual = (HelperParser.Ruta) intent.getParcelableExtra(getString(R.string.rutaActual));

        TextView textView1 = findViewById(R.id.textNom);
        textView1.setText(mRutaActual.getmName());

        TextView textView2 = findViewById(R.id.textKms);
        textView2.setText(getString(R.string.kms, mRutaActual.getmLongitud().toString()));

        TextView textView3 = findViewById(R.id.textCategoria);
        textView3.setText(mRutaActual.getmCategoria());

        TextView textView4 = findViewById(R.id.textInicio);
        textView4.setText(mRutaActual.getmInicio());

        TextView textView5 = findViewById(R.id.textFin);
        textView5.setText(mRutaActual.getmFinal());

        TextView textView6 = findViewById(R.id.textTemp);
        textView6.setText(getString(R.string.grados, String.valueOf(mRutaActual.getmTemperatura())));

        ImageView imageView = findViewById(R.id.imagenTiempo);

        if(mRutaActual.getmDescTiempo()!=null) {
            if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.claro))) {
                imageView.setImageResource(R.drawable.cieloclaro);
            } else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.dispersas))) {
                imageView.setImageResource(R.drawable.nubess);
            } else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.algoNubes))) {
                imageView.setImageResource(R.drawable.algonubes);
            }else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.rotas))) {
                imageView.setImageResource(R.drawable.nubesrotas);
            }else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.nubes))) {
                imageView.setImageResource(R.drawable.nubes);
            }else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.lluLigera))) {
                imageView.setImageResource(R.drawable.lluvialigera);
            }else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.niebla))) {
                imageView.setImageResource(R.drawable.niebla);
            }else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.nieve))) {
                imageView.setImageResource(R.drawable.nieve);
            }else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.lluModerada))) {
                imageView.setImageResource(R.drawable.lluviamoderada);
            }else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.tormenta))) {
                imageView.setImageResource(R.drawable.tormenta);
            }else if (mRutaActual.getmDescTiempo().equalsIgnoreCase(getString(R.string.lloLigera))) {
                imageView.setImageResource(R.drawable.llovizna);
            }else if (mRutaActual.getmDescTiempo().equalsIgnoreCase("nada")) {
                imageView.setImageResource(R.drawable.error);
            } else {
                imageView.setImageResource(R.drawable.error);
            }
        }

        final Button botonFav = findViewById(R.id.botonFav);
        botonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                botonFav.setEnabled(false);
            }
        });
        botonFav.setEnabled(intent.getBooleanExtra("favorito", false));
    }

    public void saveData(){

        loadData();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        mArray.add(mRutaActual);

        Gson gson = new Gson();
        String json = gson.toJson(mArray);
        editor.putString(KEY_ARRAY, json);
        editor.commit();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(mArray);
        gson = new Gson();
        json = sharedPreferences.getString(KEY_ARRAY, "");

        Type founderListType = new TypeToken<ArrayList<HelperParser.Ruta>>(){}.getType();

        ArrayList<HelperParser.Ruta> restoreArray = gson.fromJson(json, founderListType);

        if(restoreArray!=null) {
            for (int i = 0; i < restoreArray.size(); i++) {
                Log.d(TAG, restoreArray.get(i).getmName());
                mArray.add(restoreArray.get(i));

            }
        }
    }
}
