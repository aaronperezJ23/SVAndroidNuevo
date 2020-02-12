package com.example.svandroidnuevo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ArrayList<String> mNames;
    private  boolean mListSimple = false;
    private ListView lv = null;
    private MyAdapter myadapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        //TestSSL();

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



        if(mListSimple){
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mNames);

            lv.setAdapter(adapter);

        }else{
            myadapter = new MyAdapter(this, R.layout.descripcion_lista, mNames);
            lv.setAdapter(myadapter);
        }
        //SI PULSAS QUE SALDRA UN MENSAJITO
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, PulsarLista.class);
                startActivity(intent);
                //Toast.makeText(MainActivity.this, "Has pulsado: " + mNames.get(position), Toast.LENGTH_LONG).show();
            }
        });

    }


    public class MyAdapter extends BaseAdapter {

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
            Toast.makeText(this,"Opcion ORDENAR", Toast.LENGTH_SHORT).show();
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
                    Log.d(TAG, response.body().string());
                }
            }
        });

    }
}
