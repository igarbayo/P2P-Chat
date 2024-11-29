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

    // Borrar esta
    Map<String, ClientInfo> getListaClientes() throws RemoteException;

    void cargarInformacionClientes() throws RemoteException;
    void guardarInformacionClientes() throws RemoteException;
    void anadirSolicitud(String origen, String destino) throws RemoteException;
    void eliminarSolicitud(String origen, String destino) throws RemoteException;
    ClientInterface getInterface(String username) throws RemoteException;
    void notificarAmigos(ClientInterface client, String mensaje) throws RemoteException;
    void actualizarClienteInfo(ClientInterface clientInfo) throws RemoteException;
    ClientInfo obtenerClienteInfo(String username) throws RemoteException;
    List<String> getSolicitudes(ClientInterface client) throws RemoteException;
    boolean existeCliente(ClientInterface client) throws RemoteException;
    boolean existeCliente(String username) throws RemoteException;
    void anadirCliente(ClientInterface client) throws RemoteException;
    void anadirClienteOnLine(ClientInterface client) throws RemoteException;
    Map<String, ClientInterface> obtenerAmigosEnLinea(ClientInterface cliente) throws RemoteException;
    List<String> obtenerAmigos(String usuario) throws RemoteException;
    void actualizarGrupoAmistad() throws RemoteException;
    void notificar(List<ClientInterface> clientes, String mensaje) throws RemoteException;
    void eliminarDeClientesConectados(ClientInterface cliente) throws RemoteException;
    boolean estaLogueado(String usuario) throws RemoteException;
    void setClientesAOffline() throws RemoteException;

} // end interface