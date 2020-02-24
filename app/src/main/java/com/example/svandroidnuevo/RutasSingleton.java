package com.example.svandroidnuevo;

public class RutasSingleton {

    /**
     * Private constructor so nobody can instantiate the class.
     */
    private RutasSingleton() {}

    /**
     * Static to class instance of the class.
     */
    private static final RutasSingleton INSTANCE = new RutasSingleton();

    /**
     * To be called by user to obtain instance of the class.
     *
     * @return instance of the singleton.
     */
    public static RutasSingleton getInstance() {
        return INSTANCE;
    }
}
