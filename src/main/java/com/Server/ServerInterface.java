// file: SomeInterface.java
// to be implemented by a Java RMI server class.

package com.Server;
import com.Client.Client;
import com.Client.ClientInfo;
import com.Client.ClientInterface;

import java.rmi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ServerInterface extends Remote {

    public void anadirSolicitud(String origen, String destino) throws RemoteException;
    public void eliminarSolicitud(String origen, String destino) throws RemoteException;
    public ClientInterface getInterface(String username) throws RemoteException;
    public void notificarAmigos(ClientInterface client, String mensaje) throws RemoteException;
    public void actualizarClienteInfo(ClientInterface clientInfo) throws RemoteException;
    public ClientInfo obtenerClienteInfo(String username) throws RemoteException;
    public List<String> getSolicitudes(ClientInterface client) throws RemoteException;
    public boolean existeCliente(ClientInterface client) throws RemoteException;

    public boolean existeCliente(String username) throws RemoteException;

    public void anadirCliente(ClientInterface client) throws RemoteException;

    public void anadirClienteOnLine(ClientInterface client) throws RemoteException;

    public Map<String, ClientInterface> obtenerAmigosEnLinea(ClientInterface cliente) throws RemoteException;

    public List<String> obtenerAmigos(String usuario) throws RemoteException;

    public void actualizarGrupoAmistad() throws RemoteException;

    public void notificar(List<ClientInterface> clientes, String mensaje) throws RemoteException;

} // end interface