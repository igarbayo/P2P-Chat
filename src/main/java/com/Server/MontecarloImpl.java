package com.Server;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Random;

/**
 * This class implements the remote interface SomeInterface.*/
public class MontecarloImpl extends UnicastRemoteObject implements MontecarloInterface {

    // Constructor
    public MontecarloImpl() throws RemoteException {
        super();
    }

    // Método 1
    public int npuntos(int n) throws RemoteException {
        // Números aleatorios
        double doble1 = 0, doble2 = 0;
        // Número m de pares que verifican la desigualdad
        int m=0;
        // Creamos objeto aleatorio
        // currentTimeMillis() devuelve el número de milisegundos desde el UNIX epoch
        Random rand  = new Random(System.currentTimeMillis());

        for (int i = 0; i < n; i++) {
            doble1 = rand.nextDouble();
            doble2 = rand.nextDouble();
            // Verificamos la desigualdad y sumamos si corresponde
            if ((doble1*doble1 + doble2*doble2) <= 1) {
                m++;
            }
        }

        return m;
    }

} // end class
