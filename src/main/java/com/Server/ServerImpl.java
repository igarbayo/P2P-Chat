package com.Server;

import com.Client.Client;
import com.Client.ClientInfo;
import com.Client.ClientInterface;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements the remote interface SomeInterface.*/
public class ServerImpl extends UnicastRemoteObject implements ServerInterface {

    private List<ClientInterface> clientesEnLinea;
    private Map<String, ClientInfo> listaClientes;
    //private Map<Client, List<Client>> listaSolicitudes;

    // Constructor
    public ServerImpl() throws RemoteException {
        super();
        this.listaClientes = new HashMap<>();
        this.clientesEnLinea = new ArrayList<>();
        //this.listaSolicitudes = new HashMap<>();
    }


    public void actualizarClienteInfo(ClientInfo clientInfo) throws RemoteException {
        if (clientInfo != null && clientInfo.getUsuario() != null) {
            // Eliminar la instancia actual asociada al usuario, si existe
            listaClientes.remove(clientInfo.getUsuario());

            // Agregar la nueva información del cliente
            listaClientes.put(clientInfo.getUsuario(), clientInfo);
        }
    }

    public ClientInfo obtenerClienteInfo(String username) throws RemoteException {
        if (username == null || username.isEmpty()) {
            return null; // Devuelve null si el nombre de usuario es nulo o vacío
        }

        return listaClientes.get(username); // Busca y devuelve el ClientInfo en el mapa
    }

    public boolean existeCliente(ClientInfo clientInfo) throws RemoteException {
        if (clientInfo == null || clientInfo.getUsuario() == null) {
            return false; // Si el cliente o su nombre de usuario es nulo, no existe
        }

        // Verificar si el nombre de usuario está en el mapa
        return listaClientes.containsKey(clientInfo.getUsuario());
    }

    public boolean existeCliente(String username) throws RemoteException {
        if (username == null || username.isEmpty()) {
            return false; // Si el nombre de usuario es nulo o está vacío, no existe
        }

        // Verificar si el nombre de usuario está en el mapa
        return listaClientes.containsKey(username);
    }

    public boolean cargarDatos(Client client) throws RemoteException {
        // Verificar que el cliente no sea nulo
        if (client == null) {
            throw new RemoteException("El cliente no puede ser nulo");
        }

        // Obtener el nombre de usuario y la contraseña del cliente
        String usuario = client.getInfo().getUsuario();
        String contrasena = client.getInfo().getContrasena();

        if (usuario != null && contrasena != null) {
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
            client.setInfo(this.obtenerClienteInfo(usuario));
            return true; // Devuelve true si los datos se cargan correctamente
        }
        return false;
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
        List<ClientInfo> lista = this.obtenerAmigos(cliente);
        for (ClientInfo info : lista) {
            if (!info.isOnline()) {
                lista.remove(info);
            }
        }
        if (lista.isEmpty()) {
            return List.of();
        }
        return lista;
    }

    public List<ClientInfo> obtenerAmigos(Client cliente) throws RemoteException {
        if (cliente == null || cliente.getInfo() == null) {
            return List.of(); // Si el cliente o su información es nula, devuelve una lista vacía
        }

        // Lista para almacenar los amigos
        List<ClientInfo> amigos = new ArrayList<>();

        for (String amigo_usr : cliente.getInfo().getListaAmigos()) {
            amigos.add(obtenerClienteInfo(amigo_usr));
        }

        // Devolver la lista de amigos
        return amigos;
    }





    public void actualizarGrupoAmistad(Integer idGrupo) throws RemoteException {

    }

    public void notificar(List<Client> clientes, String mensaje) throws RemoteException {

    }

    public void anadirClienteEnLinea(ClientInterface cliente) throws RemoteException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        // Verificar si el cliente ya existe en el mapa
        if (clientesEnLinea.contains(cliente)) {
            throw new IllegalArgumentException("El cliente ya está registrado online.");
        }
        // Agregar el ClientInterface
       clientesEnLinea.add(cliente);
    }


    /*
    Override
    public void notificarNuevoCliente(String username) throws RemoteException {
        // Notificar a todos los clientes conectados sobre el nuevo cliente
        for (ClientInterface cliente : clientesEnLinea) {
            cliente.notificarConexion(username);
        }
    }


    @Override
    public void desconectarCliente(String username) throws RemoteException {
        // Eliminar cliente de la lista de clientes en línea y notificar a los demás
        clientesEnLinea.removeIf(cliente -> {
            try {
                return cliente.equals(username);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        });
        // Notificar desconexión a los demás clientes
        for (ClienteChat cliente : clientesEnLinea) {
            cliente.notificarDesconexion(username);
        }
    }*/



} // end class
