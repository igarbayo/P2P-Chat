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

    public void actualizarClienteInfo(ClientInfo clientInfo) throws RemoteException;
    public ClientInfo obtenerClienteInfo(String username) throws RemoteException;

    /**
     *
     * @param clientInfo
     * @return
     * @throws RemoteException
     */
    public boolean existeCliente(ClientInfo clientInfo) throws RemoteException;

    public boolean existeCliente(String username) throws RemoteException;

    public boolean cargarDatos(Client client) throws RemoteException;

    /**
     *
     * @param clientInfo
     * @throws RemoteException
     */
    public void anadirCliente(ClientInfo clientInfo) throws RemoteException;
    //public void anadirCliente(Client cliente) throws RemoteException;

    /**
     *
     * @param cliente
     * @return
     * @throws RemoteException
     */
    public List<ClientInfo> obtenerAmigosEnLinea(Client cliente) throws RemoteException;

    /**
     *
     * @param cliente
     * @return
     * @throws RemoteException
     */
    public List<ClientInfo> obtenerAmigos(Client cliente) throws RemoteException;


    /**
     *
     *
     * @return
     * @throws RemoteException
     */
    public List<String> obtenerListaClientes() throws RemoteException;

    /**
     *
     *
     * @throws RemoteException
     */
    public void actualizarGrupoAmistad() throws RemoteException;

    /**
     *
     * @param clientes
     * @param mensaje
     * @throws RemoteException
     */
    public void notificar(List<Client> clientes, String mensaje) throws RemoteException;


    public void anadirClienteEnLinea(ClientInfo clientInfo) throws RemoteException;

} // end interface