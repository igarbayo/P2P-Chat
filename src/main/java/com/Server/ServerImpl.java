package com.Server;

import com.Client.Client;
import com.Client.ClientInfo;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/**
 * This class implements the remote interface SomeInterface.*/
public class ServerImpl extends UnicastRemoteObject implements ServerInterface {

    private Map<Client, ClientInfo> listaClientes;
    private Map<Client, List<Client>> listaSolicitudes;

    // Constructor
    public ServerImpl() throws RemoteException {
        super();
        this.listaClientes = new HashMap<>();
        this.listaSolicitudes = new HashMap<>();
    }


    public void anadirCliente(Client cliente) throws RemoteException {

    }

    public List<ClientInfo> obtenerAmigosEnLinea(Client cliente) throws RemoteException {

        return List.of();
    }

    public void actualizarGrupoAmistad(Integer idGrupo) throws RemoteException {

    }

    public void notificar(List<Client> clientes, String mensaje) throws RemoteException {

    }



} // end class
