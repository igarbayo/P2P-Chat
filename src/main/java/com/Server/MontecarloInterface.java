// file: SomeInterface.java
// to be implemented by a Java RMI server class.

package com.Server;
import java.rmi.*;

public interface MontecarloInterface extends Remote {

    /**
     * Dados n número n de pares ordenados (x, y), calcula el número m de pares
     * que han verificado la desigualdad x^2 + y^2 <= 1
     * @param n número de pares total
     * @return número de pares que verifican la desigualdad
     * @throws java.rmi.RemoteException
     */
    public int npuntos(int n) throws java.rmi.RemoteException;


} // end interface