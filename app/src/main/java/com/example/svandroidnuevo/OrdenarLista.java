package com.example.svandroidnuevo;

import java.util.Comparator;

public class OrdenarLista {
    //Para que se ordene la lista dependiendo ya sea de la longitud, nombre o categoria
    public static class cusComparatorLong implements Comparator<HelperParser.Ruta> {
        @Override
        public int compare(HelperParser.Ruta o1, HelperParser.Ruta o2) {
            return o1.getmLongitud().compareTo(o2.getmLongitud());
        }
    }
    public static class cusComparatorCerc implements Comparator<HelperParser.Ruta> {
        @Override
        public int compare(HelperParser.Ruta o1, HelperParser.Ruta o2) {
            return Float.compare(o1.getmCercania(),o2.getmCercania());
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
