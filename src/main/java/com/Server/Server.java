package com.Server;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Server {

    // Variable estática
    public final static int PUERTO = 6789;

    public static void main(String[] args) {

        try {
            ServerImpl exportedObj = new ServerImpl();
            // Seleccionamos el puerto
            startRegistry(PUERTO);
            // Registramos el objeto con el nombre “id”
            String registryURL = "rmi://" + "localhost" + ":" + PUERTO + "/chat";
            // Hacemos el bind en el registro indicado
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Servidor listo.");
        }
        catch (RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    // This method starts a RMI registry on the local host, if it
    // does not already exists at the specified port number.
    private static void startRegistry(int RMIPortNum) throws RemoteException{
        try {
        Registry registry= LocateRegistry.getRegistry(RMIPortNum);
        registry.list( );
            // The above call will throw an exception
            // if the registry does not already exist
        } catch (RemoteException ex) {
            // No valid registry at that port
            System.out.println("El registro RMI no se puede ubicar en el puerto " + RMIPortNum);
            Registry registry= LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("El registro RMI se ha creado en el puerto " + RMIPortNum);
        }
    }

}