package com.example.svandroidnuevo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.svandroidnuevo.PulsarLista.KEY_ARRAY;
import static com.example.svandroidnuevo.PulsarLista.mRutaActual;

public class FiltrosActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "sharedPrefs";

    private static final String CERCANIA = "cercania";
    private static final String LONGITUD = "lonigtud";

    private static final String ENP_SW = "enpsw";
    private static final String SV_SW = "svsw";
    private static final String FM_SW = "fmsw";
    private static final String VP_SW = "vpsw";

    private EditText mCercaniaET;
    private EditText mLongitudET;
    private Switch mSendaSW;
    private Switch mEnpSW;
    private Switch mFmSW;
    private Switch mViaSW;

    private Button mGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        mCercaniaET = findViewById(R.id.etCercania);
        mLongitudET = findViewById(R.id.etLongitud);

        mSendaSW = findViewById(R.id.swSV);
        mEnpSW = findViewById(R.id.swEnp);
        mFmSW = findViewById(R.id.swFm);
        mViaSW = findViewById(R.id.swVp);

        loadData();
        if(mCercaniaET.getText().equals(100)){
            mCercaniaET.setText("");
        }
        if(mLongitudET.getText().equals(100)){
            mLongitudET.setText("");
        }

        mGuardar = findViewById(R.id.btnGuardar);
        mGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!String.valueOf(mCercaniaET.getText()).equalsIgnoreCase("")) {
            editor.putInt(CERCANIA, Integer.parseInt(String.valueOf(mCercaniaET.getText())));
        }

        if(!String.valueOf(mLongitudET.getText()).equalsIgnoreCase("")) {
            editor.putInt(LONGITUD, Integer.parseInt(String.valueOf(mLongitudET.getText())));
        }

        editor.putBoolean(ENP_SW, mEnpSW.isChecked());
        editor.putBoolean(SV_SW, mSendaSW.isChecked());
        editor.putBoolean(FM_SW, mFmSW.isChecked());
        editor.putBoolean(VP_SW, mViaSW.isChecked());

        editor.apply();

        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mCercaniaET.setText(String.valueOf(sharedPreferences.getInt(CERCANIA, 100)));
        mLongitudET.setText(String.valueOf(sharedPreferences.getInt(LONGITUD, 100)));

        mEnpSW.setChecked(sharedPreferences.getBoolean(ENP_SW, false));
        mSendaSW.setChecked(sharedPreferences.getBoolean(SV_SW, false));
        mFmSW.setChecked(sharedPreferences.getBoolean(FM_SW, false));
        mViaSW.setChecked(sharedPreferences.getBoolean(VP_SW, false));

    }

}
