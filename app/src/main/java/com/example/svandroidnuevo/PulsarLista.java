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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsar_lista);

        Button mapa = findViewById(R.id.botonMapa);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("rutaLoc", mRutaActual);
                //intent.putExtra("DESC", "Texto de prueba");
                startActivity(intent);
                //startActivityForResult(intent, 1);
            }
        });

        Intent intent = getIntent();
        mRutaActual = (HelperParser.Ruta) intent.getParcelableExtra("rutaActual");
        //Log.d("HOLA", mRutaActual.getmCategoria());


        TextView textView1 = (TextView) findViewById(R.id.textNom);
        textView1.setText(mRutaActual.getmName());
        TextView textView2 = (TextView) findViewById(R.id.textKms);
        textView2.setText("Longitud: " + mRutaActual.getmLongitud().toString() + " km");

        TextView textView3 = (TextView) findViewById(R.id.textCategoria);
        textView3.setText("Categoría: " + mRutaActual.getmCategoria());

        TextView textView4 = (TextView) findViewById(R.id.textInicio);
        textView4.setText("Inicio: " + mRutaActual.getmInicio());

        TextView textView5 = (TextView) findViewById(R.id.textFin);
        textView5.setText("Final: " + mRutaActual.getmFinal());

        TextView textView6 = (TextView) findViewById(R.id.textTemp);
        textView6.setText("Temperatura: " + String.valueOf(mRutaActual.getmTemperatura()) + "º");

        //TextView textView7 = (TextView) findViewById(R.id.textDesc);
        //textView7.setText("Clima: " + mRutaActual.getmDescTiempo());

        final Button botonFav = findViewById(R.id.botonFav);
        botonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();
                //SendArrayIntent();
                botonFav.setEnabled(false);
            }
        });



    }

    public void saveData(){

        loadData();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<HelperParser.Ruta> mArrayComparacion = mArray;

        /*if(mArray!=null) {
            for (int i = 0; i < mArray.size(); i++) {
                for (int j = 0; j < mArray.size(); j++) {
                    if (!mArray.get(i).getmName().equals(mRutaActual.getmName())) {
                        Log.d(TAG, "NO COINCIDE");
                        mArray.add(mRutaActual);
                        break;



                    }else{
                        Log.d(TAG, "COINCIDE");

                    }
                }
            }
        }else{
            mArray.add(mRutaActual);
        }*/

        mArray.add(mRutaActual);

        Gson gson = new Gson();
        String json = gson.toJson(mArray);
        editor.putString(KEY_ARRAY, json);
        editor.commit();


        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
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
