package com.example.svandroidnuevo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class HelperParser implements Serializable {

    private final String TAG = getClass().getSimpleName();


    public static class Localizacion implements Parcelable{
        public Double lat;
        public Double lon;
        public Float distance = null;

        protected Localizacion(Parcel in) {
            if (in.readByte() == 0) {
                lat = null;
            } else {
                lat = in.readDouble();
            }
            if (in.readByte() == 0) {
                lon = null;
            } else {
                lon = in.readDouble();
            }

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (lat == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(lat);
            }
            if (lon == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(lon);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Localizacion> CREATOR = new Creator<Localizacion>() {
            @Override
            public Localizacion createFromParcel(Parcel in) {
                return new Localizacion(in);
            }

            @Override
            public Localizacion[] newArray(int size) {
                return new Localizacion[size];
            }
        };

        public Double getLat() {
            return lat;
        }

        public Double getLon() {
            return lon;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public void setLon(Double lon) {
            this.lon = lon;
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

    public static class Ruta implements Parcelable{
        private String mName;
        private String mCategoria;
        private Integer mLongitud;
        private String mInicio;
        private String mFinal;
        private String mENP;

        private String mColorFill;
        private String mColorStroke;

        public Localizacion[] mLocalizaciones;

        public double mTemperatura;
        public String mDescTiempo;

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

        protected Ruta(Parcel in) {
            mName = in.readString();
            mCategoria = in.readString();
            if (in.readByte() == 0) {
                mLongitud = null;
            } else {
                mLongitud = in.readInt();
            }
            mInicio = in.readString();
            mFinal = in.readString();
            mENP = in.readString();
            mColorFill = in.readString();
            mColorStroke = in.readString();
            mLocalizaciones = in.createTypedArray(Localizacion.CREATOR);
            mTemperatura = in.readDouble();
            mDescTiempo = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mName);
            dest.writeString(mCategoria);
            if (mLongitud == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(mLongitud);
            }
            dest.writeString(mInicio);
            dest.writeString(mFinal);
            dest.writeString(mENP);
            dest.writeString(mColorFill);
            dest.writeString(mColorStroke);
            dest.writeTypedArray(mLocalizaciones, flags);
            dest.writeDouble(mTemperatura);
            dest.writeString(mDescTiempo);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Ruta> CREATOR = new Creator<Ruta>() {
            @Override
            public Ruta createFromParcel(Parcel in) {
                return new Ruta(in);
            }

            @Override
            public Ruta[] newArray(int size) {
                return new Ruta[size];
            }
        };

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

        public void setmTemperatura(double mTemperatura) {
            this.mTemperatura = mTemperatura;
        }

        public double getmTemperatura() {
            return mTemperatura;
        }

        public void setmDescTiempo(String mDescTiempo) {
            this.mDescTiempo = mDescTiempo;
        }

        public String getmDescTiempo() {
            return mDescTiempo;
        }
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
        Localizacion[] loc=null;

        try {

            if(jsonData.has("geometry")){
                JSONObject geo = jsonData.getJSONObject("geometry");
                if(geo.has("coordinates")){
                    JSONArray coordenadas = geo.getJSONArray("coordinates");
                    //Log.d(TAG, String.valueOf(coordenadas.length()));
                    loc = new Localizacion[coordenadas.length()];
                    for(int i = 0; i < coordenadas.length();i++){
                        JSONArray node = coordenadas.getJSONArray(i);

                            loc[i] = new Localizacion(node.optDouble(0),node.optDouble(1));
                             if(node.optJSONArray(i)!=null) {
                                 for (int j = 0; j < node.optJSONArray(i).length(); j++) {
                                     loc[i] = new Localizacion(node.optJSONArray(j).optDouble(0), node.optJSONArray(j).optDouble(1));
                                 }
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
            //Log.d("HOLA", nuevoRuta.toString());
            return nuevoRuta;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

}
