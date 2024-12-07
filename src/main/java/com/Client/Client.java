// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Client;

import com.Client.gui.PrincipalController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


//Serializable
public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String BASE_PATH = "src/main/resources/com/Client/";

    // Atributos
    private ClientInfo info;
    private CopyOnWriteArrayList<Chat> chats;
    private String IP;
    private int puerto;
    private Map<String, ClientInterface> amigosOnLine;
    private CopyOnWriteArrayList<String> listaNotificaciones;

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
    // Getters y Setters
    public ClientInfo getInfo() {
        return info;
    }
    public void setInfo(ClientInfo info) {
        this.info = info;
    }
    protected CopyOnWriteArrayList<Chat> getChats() {
        return chats;
    }
    protected void setChats(CopyOnWriteArrayList<Chat> chats) {
        this.chats = chats;
    }
    private PrincipalController principalController;
    public PrincipalController getPrincipalController() {
        return principalController;
    }
    public Map<String, ClientInterface> getAmigosOnLine() {
        return amigosOnLine;
    }
    public void setAmigosOnLine(Map<String, ClientInterface> amigosOnLine) {
        this.amigosOnLine = amigosOnLine;
    }
    public CopyOnWriteArrayList<String> getListaNotificaciones() {
        return listaNotificaciones;
    }

    // Constructor
    public Client() throws RemoteException {
        super();
        this.info = null;
        this.chats = new CopyOnWriteArrayList<>();
        this.amigosOnLine = new ConcurrentHashMap<>();
        this.listaNotificaciones = new CopyOnWriteArrayList<>();
    }

    // Métodos
    @Override
    public void addNotificacion(String notificacion) throws RemoteException{
        listaNotificaciones.add(notificacion);
    }

    @Override
    public ClientInterface getInterface(String username) throws RemoteException {
        // Añadir comprobacion con otro parametro de que sean amigas
        return this.amigosOnLine.get(username);
    }

    @Override
    public void addAmigoOnline(String nombre, ClientInterface client) throws RemoteException {
        if (this.amigosOnLine != null && nombre != null && client != null) {
            Map<String, ClientInterface> mapa = new HashMap<>(this.getAmigosOnline());
            mapa.put(nombre, client);
            this.setAmigosOnline(mapa);
        }
    }

    @Override
    public PrincipalController getController() throws RemoteException {
        return principalController;
    }

    @Override
    public boolean getOnline() throws RemoteException {
        if (this.info.isOnline()) {
            return true;
        }
        return false;
    }

    @Override
    public CopyOnWriteArrayList<String> getNotificaciones() throws RemoteException {
        return this.getListaNotificaciones();
    }

    public void registrarCliente(String ip, int puerto) throws RemoteException {
        try {
            // Registrar el objeto remoto en el registro RMI con el nombre de usuario
            String URL = "rmi://" + ip + ":" + puerto + "/" + info.getUsuario();
            System.out.println(URL);
            Naming.rebind(URL, this);
            System.out.println("Cliente '" + info.getUsuario() + "' registrado en RMI.");
            this.IP = ip;
            this.puerto = puerto;

            //ServerInterface server = (ServerInterface) Naming.lookup("rmi://" + ip + ":" + puerto + "/" + server);
            // Notificar a los demás clientes sobre la conexión
            //notificarAClientesConectados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cerrarConexion() throws RemoteException {
        try {
            // Desregistrar el objeto remoto en el registro RMI con el nombre de usuario
            if (this.IP != null && this.info!=null && this.info.getUsuario()!=null) {
                String URL = "rmi://" + this.IP + ":" + this.puerto + "/" + this.info.getUsuario();
                System.out.println(URL);
                //Naming.unbind(URL);
                System.out.println("Cliente '" + info.getUsuario() + "' desregistrado en RMI.");

                // Notificar a los demás clientes sobre la conexión
                //notificarAClientesConectados();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recibirMensaje(Mensaje mensaje) throws RemoteException {
        Optional<Chat> chat = this.obtenerChat(mensaje.getClienteOrigen());
        if (chat.isPresent()) {
            chat.get().anadirMensaje(mensaje);
            actualizarChat(chat.get());
        } else {
            crearChat(this.getInterface(mensaje.getClienteOrigen()));
            recibirMensaje(mensaje);
        }
    }

    @Override
    public boolean enviarMensaje(ClientInterface clientDestino, Mensaje mensaje) throws RemoteException {
        if (clientDestino == null || this.info == null || mensaje == null) {
            return false;
        }
        if (!this.getAmigosOnLine().containsValue(clientDestino)) {
            return false;
        }
        if (!clientDestino.getClientInfo().isOnline()) {
            return false;
        }
        clientDestino.recibirMensaje(mensaje);

        return true;
    }

    @Override
    public String getNombre() throws RemoteException{
        return this.info.getUsuario();
    }

    @Override
    public ClientInfo getClientInfo() throws RemoteException {
        return this.info;
    }

    @Override
    public Map<String, ClientInterface> getAmigosOnline() throws RemoteException {
        return this.amigosOnLine;
    }

    @Override
    public void setAmigosOnline(Map<String, ClientInterface> amigosOnline) throws RemoteException {
        if(amigosOnline != null){
            this.amigosOnLine=amigosOnline;
        }
    }

    @Override
    public void setListaAmigos(List<String> lista) throws RemoteException{
        if (lista!=null) {
            this.info.setListaAmigos(new CopyOnWriteArrayList<>(lista));
        }

    }

    public void aceptarSolicitudAmistad(ClientInfo clienteSolicitante) {
        // Verificar que el cliente solicitante no sea nulo
        if (clienteSolicitante != null && this.info != null) {

            // 1. Añadir usuario(this) a clienteSolicitante.listaAmigos
            clienteSolicitante.getListaAmigos().add(this.getInfo().getUsuario());

            // 2. Añadir usuario(clienteSolicitante) a this.listaAmigos
            this.getInfo().getListaAmigos().add(clienteSolicitante.getUsuario());
        }
    }

    public void eliminarAmigo(ClientInfo amigo) {
        if (amigo != null && this.info != null) {
            //Elimino el usuarioAmigo del amigo
            amigo.getListaAmigos().remove(this.getInfo().getUsuario());
            //Elimino el amigo del usuario
            this.getInfo().getListaAmigos().remove(amigo.getUsuario());
        }
    }

    public Optional<Chat> obtenerChat(String clientDestino) {
        if (this.info != null && clientDestino != null &&
                this.info.getListaAmigos().contains(clientDestino)) {
            return chats.stream()
                    .filter(chat -> chat.getClientes().contains(this.getInfo().getUsuario()) && chat.getClientes().contains(clientDestino))
                    .findFirst(); // Retorna el primer chat que cumpla la condición, o Optional.empty() si no se encuentra
        }
        return Optional.empty();
    }

    @Override
    public void addChat(Chat chat) throws RemoteException {
        if (this.chats!=null) {
            this.chats.add(chat);
        }
    }

    // Crea un chat entre el origen y un cliente destino
    @Override
    public void crearChat(ClientInterface clientDestino) throws RemoteException {
        if (clientDestino!=null && obtenerChat(clientDestino.getNombre()).isEmpty()) {
            Set<String> clientes = new HashSet<>();
            clientes.add(clientDestino.getNombre());
            clientes.add(info.getUsuario());
            Chat chat = new Chat(clientes);
            chats.add(chat);
            clientDestino.addChat(chat);
        }
    }

    public void actualizarChat(Chat chat) {
        if (chat != null) {
            this.chats.remove(chat);
            this.chats.add(chat);
        }
    }


    @Override
    public void setPrincipalController(PrincipalController principalController) throws RemoteException {
        if (principalController!=null) {
            this.principalController=principalController;
        }
    }

    @Override
    public void confirmarAmistad(String username) throws RemoteException {
        if (username != null) {
            try {
                // Añadir el usuario a la lista de amigos si no está ya
                if (!this.info.getListaAmigos().contains(username)) {
                    this.info.getListaAmigos().add(username);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void recibirNotificacion(String mensaje) throws RemoteException {
        if (this.getClientInfo().isOnline()) {
            this.addNotificacion(mensaje);
        }

    }

    @Override
    public void setOnline(Map<String, ClientInterface> mapa) throws RemoteException {
        if (mapa!=null && !mapa.isEmpty()) {
            for (ClientInterface cliente : mapa.values()) {
                ClientInterface origen = cliente.getAmigosOnline().get(this.getInfo().getUsuario());
                if (origen !=null && origen.getClientInfo() != null) {
                    origen.getClientInfo().setOnline(true);
                }
            }
        }
    }

    @Override
    public void addAmigoOnline(ClientInterface client) throws RemoteException {
        Map<String, ClientInterface> mapa = this.getAmigosOnLine();
        if (mapa!=null) {
            mapa.put(client.getClientInfo().getUsuario(), client);
            this.setAmigosOnLine(mapa);
        } else {
            mapa = new HashMap<>();
            mapa.put(client.getClientInfo().getUsuario(), client);
            this.setAmigosOnLine(mapa);
        }
    }

    @Override
    public void saveForUser(String username, byte[] key, byte[] nonce) throws RemoteException, IOException {
        // Crear carpeta del usuario si no existe
        String userPath = BASE_PATH + this.info.getUsuario();
        Files.createDirectories(Paths.get(userPath));

        // Crear el archivo
        File file = new File(userPath + "/" + username + ".key");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Key: " + Base64.getEncoder().encodeToString(key) + "\n");
            writer.write("Nonce: " + Base64.getEncoder().encodeToString(nonce) + "\n");
        }
    }

    @Override
    public ClientInfo obtenerAmigoInfo(String usernameAmigo) throws RemoteException {
        if (usernameAmigo == null || usernameAmigo.isEmpty()) {
            return null; // Devuelve null si el nombre de usuario es nulo o vacío
        }

        return amigosOnLine.get(usernameAmigo).getClientInfo(); // Busca y devuelve el ClientInfo en el mapa
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return Objects.equals(info, client.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), info);
    }
}

