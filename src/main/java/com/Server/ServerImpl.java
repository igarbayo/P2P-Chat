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

    private Map<String, ClientInfo> listaClientes;
    private Map<Client, List<Client>> listaSolicitudes;

    // Constructor
    public ServerImpl() throws RemoteException {
        super();
        this.listaClientes = new HashMap<>();
        this.listaSolicitudes = new HashMap<>();
    }

    public boolean existeCliente(ClientInfo clientInfo) throws RemoteException {
        if (clientInfo == null || clientInfo.getUsuario() == null) {
            return false; // Si el cliente o su nombre de usuario es nulo, no existe
        }

        // Verificar si el nombre de usuario está en el mapa
        return listaClientes.containsKey(clientInfo.getUsuario());
    }

    public boolean cargarDatos(Client client) throws RemoteException {
        // Verificar que el cliente no sea nulo
        if (client == null) {
            throw new RemoteException("El cliente no puede ser nulo");
        }

        // Obtener el nombre de usuario y la contraseña del cliente
        String usuario = client.getInfo().getUsuario();
        String contrasena = client.getInfo().getContrasena();

        // Verificar si el cliente existe en la lista de clientes
        ClientInfo clientInfo = listaClientes.get(usuario); // Utilizando el username como clave en el mapa

        // Si el cliente no existe en el servidor
        if (clientInfo == null) {
            throw new RemoteException("El cliente con el nombre de usuario " + usuario + " no existe en el servidor");
        }

        // Verificar si la contraseña es correcta
        if (!clientInfo.getContrasena().equals(contrasena)) {
            return false; // Si la contraseña no coincide, devolver false
        }

        // Si la contraseña es correcta, asignar la información a la instancia de Client
        client.setInfo(clientInfo);
        return true; // Devuelve true si los datos se cargan correctamente
    }




    public void anadirCliente(Client cliente) throws RemoteException {
        if (cliente == null || cliente.getInfo() == null || cliente.getInfo().getUsuario() == null) {
            throw new IllegalArgumentException("El cliente, su información o su usuario no pueden ser nulos.");
        }
        String usuario = cliente.getInfo().getUsuario();
        // Verificar si el cliente ya existe en el mapa
        if (listaClientes.containsKey(usuario)) {
            throw new IllegalArgumentException("El cliente con el usuario '" + usuario + "' ya está registrado.");
        }
        // Agregar el ClientInfo al mapa
        listaClientes.put(usuario, cliente.getInfo());
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
        // Filtrar los amigos en el mismo grupo
        return listaClientes.values().stream()
                .filter(info -> !info.getUsuario().equals(cliente.getInfo().getUsuario())) // Evitar incluir al cliente actual
                .filter(info -> idGrupoCliente.equals(info.getIdGrupo())) // Verificar que están en el mismo grupo
                .collect(Collectors.toList());
    }




    public void actualizarGrupoAmistad(Integer idGrupo) throws RemoteException {

    }

    public void notificar(List<Client> clientes, String mensaje) throws RemoteException {

    }



} // end class
