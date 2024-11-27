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
    List<Map.Entry<String, String>> listaSolicitudes;



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
        this.clientesEnLinea = new HashMap<>();
        this.listaSolicitudes=new ArrayList<>();
    }

    public void anadirSolicitud(String origen, String destino) throws RemoteException {
        if (origen!=null && destino!=null) {
            if (!this.listaSolicitudes.contains(new AbstractMap.SimpleEntry<>(origen, destino))) {
                this.listaSolicitudes.add(new AbstractMap.SimpleEntry<>(origen, destino));
            }
        }
    }

    public void eliminarSolicitud(String origen, String destino) throws RemoteException {
        if (origen!=null && destino!=null) {
            this.listaSolicitudes.remove(new AbstractMap.SimpleEntry<>(origen, destino));
        }
    }

    public List<String> getSolicitudes(ClientInterface client) throws RemoteException {
        if (client != null) {
            List<String> lista = new ArrayList<>();
            for (Map.Entry<String, String> entry : this.listaSolicitudes) {
                if (entry.getValue().equals(client.getNombre())) {
                    lista.add(entry.getKey());
                }
            }
            return lista;
        } else {
            return List.of();
        }
    }

    public ClientInterface getInterface(String username) throws RemoteException {
            // Añadir comprobacion con otro parametro de que sean amigas
            return this.clientesEnLinea.get(username);
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

    // REVISAR
    public ClientInfo obtenerClienteInfo(String username) throws RemoteException {
        if (username == null || username.isEmpty()) {
            return null; // Devuelve null si el nombre de usuario es nulo o vacío
        }

        return listaClientes.get(username); // Busca y devuelve el ClientInfo en el mapa
    }


    public boolean existeCliente(ClientInterface client) throws RemoteException {
        ClientInfo clientInfo = client.getClientInfo();
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

    public void anadirCliente(ClientInterface client) throws RemoteException {
        if (client == null || client.getNombre() == null || client.getClientInfo()==null) {
            throw new IllegalArgumentException("La información del cliente o su usuario no pueden ser nulos.");
        }
        String usuario = client.getNombre();
        if (listaClientes.containsKey(usuario)) {
            throw new IllegalArgumentException("El cliente con el usuario '" + usuario + "' ya está registrado.");
        }
        listaClientes.put(usuario, client.getClientInfo());
    }

    public void anadirClienteOnLine(ClientInterface client) throws RemoteException {
        if (client != null) {
            // Añadimos el cliente a lista del servidor
            clientesEnLinea.put(client.getNombre(), client);

            // Recuperamos sus amigos en linea
            Map<String, ClientInterface> mapa = this.obtenerAmigosEnLinea(client);
            if (mapa != null) {
                client.setAmigosOnline(mapa);
            }

            System.out.println(clientesEnLinea.keySet());
        }
    }

    public Map<String, ClientInterface> obtenerAmigosEnLinea(ClientInterface clienteInterface) throws RemoteException {
        // Verifica si el cliente proporcionado es válido
        if (clienteInterface == null) {
            throw new IllegalArgumentException("El clienteInterface no puede ser null");
        }

        // Obtener la información del cliente
        ClientInfo clienteInfo = clienteInterface.getClientInfo();
        if (clienteInfo == null) {
            return Map.of(); // Devuelve un mapa vacío si la información del cliente es nula
        }

        // Obtener la lista de nombres de usuario de los amigos del cliente
        List<String> nombresAmigos = clienteInfo.getListaAmigos();
        if (nombresAmigos == null || nombresAmigos.isEmpty()) {
            return Map.of(); // Devuelve un mapa vacío si no hay amigos en la lista
        }

        // Filtrar los clientes en línea para incluir solo aquellos que están en la lista de amigos
        Map<String, ClientInterface> amigosEnLinea = new HashMap<>();
        for (Map.Entry<String, ClientInterface> entry : clientesEnLinea.entrySet()) {
            String nombreUsuario = entry.getKey();
            ClientInterface clienteEnLinea = entry.getValue();

            // Si el nombre de usuario está en la lista de amigos, añádelo al mapa resultante
            if (nombresAmigos.contains(nombreUsuario)) {
                amigosEnLinea.put(nombreUsuario, clienteEnLinea);
            }
        }

        return amigosEnLinea;
    }

    public List<String> obtenerAmigos(String usuario) throws RemoteException {
        if (usuario == null) {
            return List.of(); // Si el cliente o su información es nula, devuelve una lista vacía
        }

        // Lista para almacenar los amigos
        List<String> amigos = new ArrayList<>();

        ClientInfo cliente = this.listaClientes.get(usuario);

        //System.out.println("He llegado a 1");
        if (cliente != null) {
            for (String amigo_usr : cliente.getListaAmigos()) {
                amigos.add(amigo_usr);
            }
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
    public void notificar(List<ClientInterface> clientes, String mensaje) throws RemoteException {
        if (clientes == null || mensaje == null) {
            throw new IllegalArgumentException("La lista de clientes y el mensaje no pueden ser nulos");
        }

        // Intentar notificar a cada cliente en la lista
        for (ClientInterface cliente : clientes) {
            if (cliente != null && cliente.getOnline()) {
                cliente.recibirNotificacion(mensaje);
            }
        }
    }

    public void notificarAmigos(ClientInterface client, String mensaje) throws RemoteException {
        Map<String, ClientInterface> amigos = this.obtenerAmigosEnLinea(client);
        if (amigos!=null && !amigos.isEmpty()) {
            List<ClientInterface> amigosLista = new ArrayList<>(amigos.values());
            if (!amigosLista.isEmpty()) {
                this.notificar(amigosLista, mensaje);
            }
        }
    }




    /*@Override
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
