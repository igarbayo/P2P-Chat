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

    public void notificarAmigos(ClientInfo client, String mensaje) throws RemoteException;
    public ClientInterface obtenerInstanciaConInfo(ClientInfo clientInfo) throws RemoteException;
    public void actualizarClienteInfo(ClientInfo clientInfo) throws RemoteException;
    public ClientInfo obtenerClienteInfo(String username) throws RemoteException;

    public boolean existeCliente(ClientInfo clientInfo) throws RemoteException;

    public boolean existeCliente(String username) throws RemoteException;

    public boolean cargarDatos(Client client) throws RemoteException;

    public void anadirCliente(ClientInfo clientInfo) throws RemoteException;

    public void anadirClienteOnLine(Client client) throws RemoteException;

    public List<ClientInfo> obtenerAmigosEnLinea(ClientInfo cliente) throws RemoteException;

    public List<ClientInfo> obtenerAmigos(ClientInfo cliente) throws RemoteException;

    public List<String> obtenerListaClientes() throws RemoteException;

    public void actualizarGrupoAmistad() throws RemoteException;

    public void notificar(List<ClientInfo> clientes, String mensaje) throws RemoteException;


} // end interface