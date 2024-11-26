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

    private String IP;
    private int puerto;
    private Map<String, ClientInterface> clientesEnLinea;
    private Map<String, ClientInfo> listaClientes;
    //private Map<Client, List<Client>> listaSolicitudes;

    public String getIP() {
        return IP;
    }
    public void setIP(String IP) {
        this.IP = IP;
    }
    public int getPuerto() {
        return puerto;
    }
    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    // Constructor
    public ServerImpl() throws RemoteException {
        super();
        this.listaClientes = new HashMap<>();
        this.clientesEnLinea = new ArrayList<>();
        //this.listaSolicitudes = new HashMap<>();
    }


    public void actualizarClienteInfo(ClientInterface client) throws RemoteException {
        ClientInfo clientInfo=client.getClientInfo();
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

    public void anadirCliente(ClientInfo clientInfo) throws RemoteException {
        if (clientInfo == null || clientInfo.getUsuario() == null) {
            throw new IllegalArgumentException("La información del cliente o su usuario no pueden ser nulos.");
        }
        String usuario = clientInfo.getUsuario();
        if (listaClientes.containsKey(usuario)) {
            throw new IllegalArgumentException("El cliente con el usuario '" + usuario + "' ya está registrado.");
        }
        listaClientes.put(usuario, clientInfo);
    }

    public void anadirClienteOnLine(Client client) throws RemoteException {
        if (client != null && client.getInfo() != null && client.getInfo().getUsuario() != null) {
            clientesEnLinea.add(client);
        }
    }

    public List<ClientInterface> obtenerAmigosEnLinea(ClientInterface clienteInterface)throws RemoteException{
        if (clienteInterface == null) {
            return List.of(); // Devuelve una lista vacía si el cliente es nulo.
        }

        // Obtener la información del cliente.
        ClientInfo clienteInfo = clienteInterface.getClientInfo();
        if (clienteInfo == null) {
            return List.of(); // Devuelve una lista vacía si la información del cliente es nula.
        }

        // Obtener la lista de nombres de usuario de los amigos del cliente.
        List<String> nombresAmigos = clienteInfo.getListaAmigos();

        if (nombresAmigos == null || nombresAmigos.isEmpty()) {
            return List.of(); // Devuelve una lista vacía si no tiene amigos registrados.
        }

        // Filtrar los amigos que están en línea.
        List<ClientInterface> amigos = clientesEnLinea.values().stream()
                .filter(cliente -> {
                    try {
                        // Verifica si el nombre del cliente en línea está en la lista de amigos.
                        return nombresAmigos.contains(cliente.getNombre());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());

        /*List<ClientInterface> amigos = new ArrayList<>();

        for (ClientInterface cliente : clientes) {
            if (obtenerAmigos(clienteInterface.getNombre()).contains(cliente.getNombre())) {
                amigos.add(cliente);
            }
        }*/
        return amigos; // Devuelve la lista de amigos en línea.
    }


    public List<String> obtenerAmigos(String usuario) throws RemoteException {
        if (usuario == null) {
            return List.of(); // Si el cliente o su información es nula, devuelve una lista vacía
        }

        // Lista para almacenar los amigos
        List<ClientInfo> amigos = new ArrayList<>();

        System.out.println("He llegado a 1");

        for (String amigo_usr : cliente.getListaAmigos()) {
            amigos.add(obtenerClienteInfo(amigo_usr));
        }

        // Devolver la lista de amigos
        return amigos;
    }



    public void actualizarGrupoAmistad() throws RemoteException {
        // Lista para almacenar clientes que necesitan ser notificados
        List<Client> clientesANotificar = new ArrayList<>();

        // Recorrer todos los clientes en línea y actualizar sus listas de amigos
        for (ClientInterface cliente : clientesEnLinea.values()) {
            try {
                // Obtener la información actual del cliente
                if (cliente.getNombre() != null) {
                    // Obtener la información más actualizada del servidor
                    ClientInfo infoActualizada = listaClientes.get(cliente.getNombre());


                    // Verificar si hay cambios en la lista de amigos
                    if (!obtenerAmigos(cliente.getNombre()).equals(infoActualizada.getListaAmigos())) {
                        // Crear un cliente temporal para la notificación
                        Client clienteTemp = new Client();
                        clienteTemp.setInfo(infoActualizada);
                        clientesANotificar.add(clienteTemp);

                        // Actualizar la información del cliente en línea
                        cliente.setListaAmigos(new ArrayList<>(infoActualizada.getListaAmigos()));
                    }
                }
            } catch (RemoteException e) {
                // Si hay un error al comunicarse con un cliente, continuamos con el siguiente
                e.printStackTrace();
            }
        }

        // Si hay clientes para notificar, enviamos las notificaciones
        if (!clientesANotificar.isEmpty()) {
            //String mensaje = "Tu lista de amigos ha sido actualizada";
            //habría que hacerlo desde confirmacion aceptada notificar(clientesANotificar, mensaje);
        }
    }


    //No funciona bien esta funcion. Habría que pillar la conexion. Si no se pueden enviar las notificaciones directamente desde los clientes.
    public void notificar(List<ClientInfo> clientes, String mensaje) throws RemoteException {
        if (clientes == null || mensaje == null) {
            throw new IllegalArgumentException("La lista de clientes y el mensaje no pueden ser nulos");
        }

        // Intentar notificar a cada cliente en la lista
        for (ClientInfo clienteInfo : clientes) {
            if (clienteInfo != null && clienteInfo.isOnline()) {
                ClientInterface clientInterface = this.obtenerInstanciaConInfo(clienteInfo);
                if (clientInterface != null) {
                    clientInterface.recibirNotificacion(mensaje);
                }
            }
        }
    }

    public void notificarAmigos(ClientInterface client, String mensaje) throws RemoteException {
        List<ClientInterface> amigosInfo = this.obtenerAmigosEnLinea(client);
        if (amigosInfo != null && !amigosInfo.isEmpty()) {
            this.notificar(amigosInfo, mensaje);
        }
    }

    /*
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
    }*/


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
