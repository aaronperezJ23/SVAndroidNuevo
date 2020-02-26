package com.example.svandroidnuevo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

public class PulsarLista extends AppCompatActivity {

    public static HelperParser.Ruta mRutaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsar_lista);

        Button mapa = findViewById(R.id.botonIniciar);
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

    }

    //Para que se ordene la lista dependiendo ya sea de la longitud, nombre o categoria
    public static class cusComparatorLong implements Comparator<HelperParser.Ruta> {
        @Override
        public int compare(HelperParser.Ruta o1, HelperParser.Ruta o2) {
            return o1.getmLongitud().compareTo(o2.getmLongitud());
        }
    }
    public static class cusComparatorNom implements Comparator<HelperParser.Ruta> {
        @Override
        public int compare(HelperParser.Ruta o1, HelperParser.Ruta o2) {
            return o1.getmName().compareTo(o2.getmName());
        }
    }
    public static class cusComparatorCat implements Comparator<HelperParser.Ruta> {
        @Override
        public int compare(HelperParser.Ruta o1, HelperParser.Ruta o2) {
            return o1.getmCategoria().compareTo(o2.getmCategoria());
        }
    }
}
