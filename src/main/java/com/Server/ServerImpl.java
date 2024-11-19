package com.Server;

import com.Client.Client;
import com.Client.ClientInfo;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<ClientInfo> obtenerAmigos(Client cliente) throws RemoteException {
        if (cliente == null || cliente.getInfo() == null) {
            return List.of(); // Si el cliente o su información es nula, devuelve una lista vacía
        }

        // Obtener el ID de grupo del cliente dado
        Integer idGrupoCliente = cliente.getInfo().getIdGrupo();
        if (idGrupoCliente == null) {
            return List.of(); // Si el ID de grupo es nulo, devuelve una lista vacía
        }

        // Filtrar clientes en el mismo grupo
        return listaClientes.keySet().stream()
                .filter(c -> c != cliente) // Evitar que el cliente se incluya a sí mismo
                .filter(c -> {
                    ClientInfo info = c.getInfo();
                    return info != null && idGrupoCliente.equals(info.getIdGrupo());
                })
                .map(Client::getInfo) // Mapear los clientes a sus `ClientInfo`
                .collect(Collectors.toList());
    }


    public void actualizarGrupoAmistad(Integer idGrupo) throws RemoteException {

    }

    public void notificar(List<Client> clientes, String mensaje) throws RemoteException {

    }



} // end class
