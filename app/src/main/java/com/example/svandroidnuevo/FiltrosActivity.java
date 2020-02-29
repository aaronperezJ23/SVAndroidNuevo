package com.example.svandroidnuevo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class FiltrosActivity extends AppCompatActivity {

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

        mGuardar = findViewById(R.id.btnGuardar);
        mGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(FiltrosActivity.this, "Filtros guardados", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("CERCANIA_INPUT", Integer.parseInt(mCercaniaET.getText().toString()));
        returnIntent.putExtra("LONGITUD_INPUT", Integer.parseInt(mLongitudET.getText().toString()));

        returnIntent.putExtra("ENP_RETURN", mEnpSW.isChecked());
        returnIntent.putExtra("SENDA_RETURN", mSendaSW.isChecked());
        returnIntent.putExtra("FM_RETURN", mFmSW.isChecked());
        returnIntent.putExtra("VP_RETURN", mViaSW.isChecked());
        setResult(RESULT_OK, returnIntent);
    }
}
