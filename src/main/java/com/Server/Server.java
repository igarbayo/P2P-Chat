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
            String registryURL = "rmi://" + "localhost" + ":" + PUERTO + "/server";
            // Hacemos el bind en el registro indicado
            Naming.rebind(registryURL, exportedObj);
            exportedObj.cargarInformacionClientes();

            // Apagado del servidor por defecto
            // Agregar un hook de cierre para guardar la información al detener la aplicación
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    //Imprimimos lista de clientes
                    System.out.println("LISTA DE CLIENTES");
                    System.out.println(exportedObj.getListaClientes());
                    System.out.println("-------------");

                    System.out.println("Guardando la información de los clientes antes de cerrar...");
                    exportedObj.setClientesAOffline();
                    exportedObj.guardarInformacionClientes();
                } catch (RemoteException e) {
                    System.err.println("Error al guardar la información de los clientes en el cierre: " + e.getMessage());
                    e.printStackTrace();
                }
            }));

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