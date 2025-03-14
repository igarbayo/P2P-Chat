// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Server;

import com.Client.ClientInfo;
import com.Client.ClientInterface;

import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class ServerImpl extends UnicastRemoteObject implements ServerInterface {

    // Atributos
    private String IP;
    private int puerto;
    private Map<String, ClientInterface> clientesEnLinea;
    private Map<String, ClientInfo> listaClientes;
    private CopyOnWriteArrayList<Map.Entry<String, String>> listaSolicitudes;

    // Getters y setters
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
        this.listaClientes = new ConcurrentHashMap<>();
        this.clientesEnLinea = new ConcurrentHashMap<>();
        this.listaSolicitudes = new CopyOnWriteArrayList<>();
    }

    @Override
    public // Imprime "hola" por pantalla
    void hola() throws RemoteException {
        System.out.println("Hola");
    }

    @Override
    public void cargarInformacionClientes() throws RemoteException {
        String filePath = "src/main/resources/com/Server/clientInfo.txt";
        File file = new File(filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder currentClientInfo = new StringBuilder();
            String line;
            boolean readingClient = false;

            while ((line = reader.readLine()) != null) {
                // Detectar el inicio de un bloque ClientInfo
                if (line.trim().startsWith("ClientInfo{")) {
                    readingClient = true;
                    currentClientInfo.setLength(0); // Resetear el StringBuilder
                }

                if (readingClient) {
                    currentClientInfo.append(line).append("\n");
                    // Detectar el final del bloque ClientInfo
                    if (line.contains("}")) {
                        readingClient = false;
                        // Analizar y guardar el ClientInfo completo
                        ClientInfo clientInfo = parseClientInfo(currentClientInfo.toString());
                        if (clientInfo != null) {
                            listaClientes.put(clientInfo.getUsuario(), clientInfo);
                        }
                    }
                }
            }

            System.out.println("Información de clientes cargada correctamente desde " + filePath);
        } catch (IOException e) {
            System.err.println("Error al cargar la información de los clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ClientInfo parseClientInfo(String data) {
        try {
            String usuario = extractValue(data, "usuario='", "',");
            String contrasena = extractValue(data, "contrasena='", "',");
            String listaAmigosString = extractValue(data, "listaAmigos=", "]");
            boolean online = Boolean.parseBoolean(extractValue(data, "online=", "}").trim());

            // Convertir la cadena de listaAmigos a una lista de Strings
            List<String> listaAmigos = new ArrayList<>();
            if (!listaAmigosString.isEmpty() && !listaAmigosString.equals("null")) {
                listaAmigosString = listaAmigosString.replaceAll("[\\[\\] ]", ""); // Eliminar corchetes y espacios
                if (!listaAmigosString.isEmpty()) {
                    listaAmigos = Arrays.asList(listaAmigosString.split(","));
                }
            }

            // Crear y devolver un nuevo objeto ClientInfo
            return new ClientInfo(usuario, contrasena, new CopyOnWriteArrayList<>(listaAmigos), online);
        } catch (StringIndexOutOfBoundsException e) {
            System.err.println("Error al analizar ClientInfo: " + data);
            e.printStackTrace();
            return null;
        }
    }

    private String extractValue(String data, String start, String end) {
        int startIndex = data.indexOf(start);
        int endIndex = data.indexOf(end, startIndex);
        if (startIndex != -1 && endIndex != -1 && startIndex + start.length() <= endIndex) {
            return data.substring(startIndex + start.length(), endIndex).trim();
        }
        // Imprimir información de depuración si no se encuentran los delimitadores
        System.err.println("Delimitadores no encontrados en la cadena: " + data + start);
        return "";
    }

    // Guardar la información
    @Override
    public void guardarInformacionClientes() throws RemoteException {
        String filePath = "src/main/resources/com/Server/clientInfo.txt"; // Ruta del archivo en resources
        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, ClientInfo> entry : listaClientes.entrySet()) {
                // Escribir cada ClientInfo en el archivo
                writer.write(entry.getValue().toString());
                writer.newLine();
            }
            System.out.println("Información de clientes guardada correctamente en " + filePath);
        } catch (IOException e) {
            System.err.println("Error al guardar la información de los clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Métodos
    @Override
    public void anadirSolicitud(String origen, String destino) throws RemoteException {
        if (origen!=null && destino!=null) {
            if (!this.listaSolicitudes.contains(new AbstractMap.SimpleEntry<>(origen, destino))) {
                this.listaSolicitudes.add(new AbstractMap.SimpleEntry<>(origen, destino));
            }
        }
    }

    @Override
    public void eliminarSolicitud(String origen, String destino) throws RemoteException {
        if (origen!=null && destino!=null) {
            this.listaSolicitudes.remove(new AbstractMap.SimpleEntry<>(origen, destino));
        }
    }

    @Override
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

    @Override
    public ClientInterface getInterface(String username) throws RemoteException {
            // Añadir comprobacion con otro parametro de que sean amigas
            return this.clientesEnLinea.get(username);
    }

    @Override
    public void actualizarClienteInfo(ClientInterface client) throws RemoteException {
        ClientInfo clientInfo=client.getClientInfo();
        if (clientInfo != null && clientInfo.getUsuario() != null) {
            // Eliminar la instancia actual asociada al usuario, si existe
            listaClientes.remove(clientInfo.getUsuario());

            // Agregar la nueva información del cliente
            listaClientes.put(clientInfo.getUsuario(), clientInfo);
        }
    }

    @Override
    public void actualizarClienteInfo(ClientInfo clientInfo) throws RemoteException {
        if (clientInfo != null && clientInfo.getUsuario() != null) {
            // Eliminar la instancia actual asociada al usuario, si existe
            listaClientes.remove(clientInfo.getUsuario());

            // Agregar la nueva información del cliente
            listaClientes.put(clientInfo.getUsuario(), clientInfo);
        }
    }

    // REVISAR
    @Override
    public ClientInfo obtenerClienteInfo(String username) throws RemoteException {
        if (username == null || username.isEmpty()) {
            return null; // Devuelve null si el nombre de usuario es nulo o vacío
        }

        return listaClientes.get(username); // Busca y devuelve el ClientInfo en el mapa
    }

    @Override
    public boolean existeCliente(ClientInterface client) throws RemoteException {
        ClientInfo clientInfo = client.getClientInfo();
        if (clientInfo == null || clientInfo.getUsuario() == null) {
            return false; // Si el cliente o su nombre de usuario es nulo, no existe
        }

        // Verificar si el nombre de usuario está en el mapa
        return listaClientes.containsKey(clientInfo.getUsuario());
    }

    @Override
    public boolean existeCliente(String username) throws RemoteException {
        if (username == null || username.isEmpty()) {
            return false; // Si el nombre de usuario es nulo o está vacío, no existe
        }

        // Verificar si el nombre de usuario está en el mapa
        return listaClientes.containsKey(username);
    }

    @Override
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

    @Override
    public void anadirClienteOnLine(ClientInterface client) throws RemoteException {
        if (client != null) {
            // Añadimos el cliente a lista del servidor
            clientesEnLinea.put(client.getNombre(), client);

            // Recuperamos sus amigos en linea
            List<ClientInterface> listaANotificar=new ArrayList<>();;
            Map<String, ClientInterface> mapa = this.obtenerAmigosEnLinea(client);
            if (mapa != null) {
                client.setAmigosOnline(mapa);
                listaANotificar.addAll(mapa.values());
                for (ClientInterface amigo : mapa.values()) {
                    amigo.addAmigoOnline(client);
                }
            }

            // Cambiamos a online su bit en sus amigos online
            client.setOnline(client.getAmigosOnline());

            // Actualizamos su info en el servidor
            actualizarClienteInfo(client);

            // Notificamos a sus amigos en linea
            notificar(listaANotificar, "Tu amigo "+client.getNombre()+" se ha conectado");

            // Actualizamos la info en el servidor
            this.actualizarClienteEnLinea(client,1);
            if (this.listaClientes != null) {
                for (ClientInfo info : listaClientes.values()) {
                    if(client.getClientInfo().getUsuario().equals(info.getUsuario())) {
                        info.setOnline(true);
                    }
                }
            }
            //Notifico a los amigos de este cliente en linea.

        }
    }

    @Override
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
        List<String> nombresAmigos = obtenerAmigos(clienteInterface.getClientInfo().getUsuario());
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

    @Override
    public List<String> obtenerAmigos(String usuario) throws RemoteException {
        if (usuario == null) {
            return List.of(); // Si el cliente o su información es nula, devuelve una lista vacía
        }

        // Lista para almacenar los amigos
        List<String> amigos = new ArrayList<>();

        ClientInfo cliente = this.listaClientes.get(usuario);

        if (cliente != null) {
            for (String amigo_usr : cliente.getListaAmigos()) {
                amigos.add(amigo_usr);
            }
        }

        // Devolver la lista de amigos
        return amigos;
    }

    @Override
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

    @Override
    public void eliminarDeClientesConectados(ClientInterface cliente) throws RemoteException {
        if(cliente != null) {
            clientesEnLinea.remove(cliente.getClientInfo().getUsuario());
            //Notifico de la desconexion.
            this.actualizarClienteEnLinea(cliente,2);
            notificarAmigos(cliente,"Tu amigo "+cliente.getClientInfo().getUsuario()+" se ha desconectado.");
        }
    }

    @Override
    public void notificarAmigos(ClientInterface client, String mensaje) throws RemoteException {
        Map<String, ClientInterface> amigos = this.obtenerAmigosEnLinea(client);
        if (amigos!=null && !amigos.isEmpty()) {
            List<ClientInterface> amigosLista = new ArrayList<>(amigos.values());
            if (!amigosLista.isEmpty()) {
                this.notificar(amigosLista, mensaje);
            }
        }
    }

    @Override
    public boolean estaLogueado(String usuario) throws RemoteException {
        return this.clientesEnLinea.containsKey(usuario);
    }

    @Override
    public void setClientesAOffline() throws RemoteException {
        if (this.listaClientes != null) {
            for (ClientInfo info : listaClientes.values()) {
                info.setOnline(false);
            }
        }
        if (this.clientesEnLinea != null) {
            clientesEnLinea.clear();
        }
    }

    @Override
    public void actualizarClienteEnLinea(ClientInterface cliente, int online) throws RemoteException {
        if(cliente.getClientInfo().getUsuario()!=null && online!=0) {
            if(online==1){
                ClientInfo infoActualizada = listaClientes.get(cliente.getClientInfo().getUsuario());
                infoActualizada.setOnline(true);
                listaClientes.remove(cliente.getClientInfo().getUsuario());
                listaClientes.put(cliente.getClientInfo().getUsuario(), infoActualizada);
            }
            if (online==2){
                ClientInfo infoActualizada = listaClientes.get(cliente.getClientInfo().getUsuario());
                infoActualizada.setOnline(false);
                listaClientes.remove(cliente.getClientInfo().getUsuario());
                listaClientes.put(cliente.getClientInfo().getUsuario(), infoActualizada);
            }
        }
    }


} // end class
