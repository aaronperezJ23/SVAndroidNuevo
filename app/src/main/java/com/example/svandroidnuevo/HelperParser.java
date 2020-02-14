package com.example.svandroidnuevo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HelperParser {

    private final String TAG = getClass().getSimpleName();

    public static class Localizacion{
        public Double lat=null;
        public Double lon=null;

        public Double getLat() {
            return lat;
        }

        public Double getLon() {
            return lon;
        }

        public Localizacion(Double lat, Double lon){
            this.lat=lat;
            this.lon=lon;
        }

        @Override
        public String toString() {
            return "Localizacion{" +
                    "lat=" + lat +
                    ", lon=" + lon +
                    '}';
        }
    }

    public class Ruta {
        private String mName;
        private String mCategoria;
        private Integer mLongitud;
        private String mInicio;
        private String mFinal;
        private String mENP;

        private String mColorFill;
        private String mColorStroke;

        private Localizacion[] mLocalizaciones;



        public Ruta(String mName, String mCategoria, Integer mLongitud, String mInicio, String mFinal, String mENP, String mColorFill, String mColorStroke, Localizacion[] mLocalizaciones) {
            this.mName = mName;
            this.mCategoria = mCategoria;
            this.mLongitud = mLongitud;
            this.mInicio = mInicio;
            this.mFinal = mFinal;
            this.mENP = mENP;
            this.mColorFill = mColorFill;
            this.mColorStroke = mColorStroke;
            this.mLocalizaciones = mLocalizaciones;
        }

        @Override
        public String toString() {
            return "Ruta{" +
                    "mName='" + mName + '\'' +
                    ", mCategoria='" + mCategoria + '\'' +
                    ", mLongitud=" + mLongitud +
                    ", mInicio='" + mInicio + '\'' +
                    ", mFinal='" + mFinal + '\'' +
                    ", mENP='" + mENP + '\'' +
                    ", mColorFill='" + mColorFill + '\'' +
                    ", mColorStroke='" + mColorStroke + '\'' +
                    ", mLocalizacion=" + mLocalizaciones.toString() +
                    '}';
        }

        public String getmName() {
            return mName;
        }

        public String getmCategoria() {
            return mCategoria;
        }

        public Integer getmLongitud() {
            return mLongitud;
        }

        public String getmInicio() {
            return mInicio;
        }

        public String getmFinal() {
            return mFinal;
        }

        public String getmENP() {
            return mENP;
        }

        public String getmColorFill() {
            return mColorFill;
        }

        public String getmColorStroke() {
            return mColorStroke;
        }

        public Localizacion[] getmLocalizacion(){ return mLocalizaciones; }

    }

    public ArrayList<Ruta> parseRutas(String content){
        ArrayList<Ruta> lRutas = new ArrayList<Ruta>();

        JSONArray array;
        JSONObject json = null;

        try {
            json = new JSONObject(content);
            array = json.getJSONArray("features");

            for(int i = 0; i < array.length();i++){
                JSONObject node = array.getJSONObject(i);
                Ruta pnode = parseRuta (node);

                lRutas.add(pnode);
            }
            return lRutas;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    private Ruta parseRuta(JSONObject jsonData){
        String name = "", categoria = "", inicio="", fnl="",
                enp="", colorFill="", colorStroke="";
        Integer longitud = 0;
        Localizacion[] loc = new Localizacion[10000];

        try {

            if(jsonData.has("geometry")){
                JSONObject geo = jsonData.getJSONObject("geometry");
                if(geo.has("coordinates")){
                    JSONArray coordenadas = geo.getJSONArray("coordinates");
                    for(int i = 0; i < coordenadas.length();i++){
                        JSONArray node = coordenadas.getJSONArray(i);
                        //loc[i] = new Localizacion(node.getDouble(0), node.getDouble(1));
                        //System.out.println(node.get(0).getClass().getSimpleName());

                        if(node.get(0).getClass().getSimpleName().equalsIgnoreCase("Double")){
                            System.out.println("-" + node.get(0) + node.get(1));
                        }else {
                            //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++" + node.get(0));
                            //loc[i] = new Localizacion(node.getDouble(0), node.getDouble(1));
                            //JSONArray array = node.getJSONArray(0);
                            //for (int j = 0; j < array.length(); j++)
                              //  System.out.println("Lat: " + array.getDouble(0));
                            //System.out.println(loc[i].toString());
                            //System.out.println("Lat: " + node.getDouble(0) + " - Lon: " +node.getDouble(1));
                        }


                    }
                }
            }
            if(jsonData.has("properties")){
                JSONObject proper = jsonData.getJSONObject("properties");
                if(proper.has("DS_NOMBRE"))
                    name = proper.getString("DS_NOMBRE");
                if(proper.has("DS_CATEGORIA"))
                    categoria = proper.getString("DS_CATEGORIA");
                if(proper.has("DS_LONGITUD"))
                    longitud = proper.getInt("DS_LONGITUD");
                if(proper.has("DS_INICIO"))
                    inicio = proper.getString("DS_INICIO");
                if(proper.has("DS_FINAL"))
                    fnl = proper.getString("DS_FINAL");
                if(proper.has("DS_ENP"))
                    enp = proper.getString("DS_ENP");
                if(proper.has("COLOR_FILL"))
                    colorFill = proper.getString("COLOR_FILL");
                if(proper.has("COLOR_STROKE"))
                    colorStroke = proper.getString("COLOR_STROKE");
            }


            Ruta nuevoRuta = new Ruta(name,categoria,longitud,inicio,fnl,enp,colorFill,colorStroke,loc);
            Log.d("HOLA", nuevoRuta.toString());
            return nuevoRuta;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
