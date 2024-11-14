package com.Server;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    // Variables estáticas
    public static int s = 1;        // número de servidores

    public static void main(String[] args) {

        // El argumento será el array de puertos para los servidores
        if (args.length < 3) {
            System.err.println("Uso: java Server <id> <IP> <puerto>");
            return;
        }

        // Orden de argumentos
        // args[0] = id
        // args[1] = IP
        // args[2] = puerto

        try {
            MontecarloImpl exportedObj = new MontecarloImpl();
            // Seleccionamos el puerto
            startRegistry(Integer.parseInt(args[2]));
            // Registramos el objeto con el nombre “id”
            String registryURL = "rmi://" + args[1] + ":" + Integer.parseInt(args[2]) + "/" + args[0];
            // Hacemos el bind en el registro indicado
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Servidor " + args[0] + " ready.");
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