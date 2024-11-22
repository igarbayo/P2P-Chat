package com.Client;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;


public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {
    private static final long serialVersionUID = 1L;
    // Atributos
    private ClientInfo info;
    // los chats se almacenan en cada instancia de cliente de forma local, cada vez que se arranca
    // se borran cuando se cierra la instancia del cliente, puesto que la app tampoco deja mandar
    // mensajes si un cliente no está conectado
    private List<Chat> chats;
    private String IP;
    private int puerto;

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
    protected List<Chat> getChats() {
        return chats;
    }
    protected void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    // Constructor
    public Client() throws RemoteException {
        super();
        this.info = null;
        this.chats = new ArrayList<Chat>();
    }

    // Métodos
    // Método para registrar al cliente en el registro RMI y enviar una notificación a otros clientes
    public void registrarCliente(String ip, int puerto) throws RemoteException {
        try {
            // Registrar el objeto remoto en el registro RMI con el nombre de usuario
            Naming.rebind("rmi://" + ip + ":" + puerto + "/" + info.getUsuario(), this);
            System.out.println("Cliente '" + info.getUsuario() + "' registrado en RMI.");
            this.IP = ip;
            this.puerto = puerto;

            // Notificar a los demás clientes sobre la conexión
            notificarAClientesConectados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cerrarConexion() throws RemoteException {
        try {
            // Registrar el objeto remoto en el registro RMI con el nombre de usuario
            Naming.unbind("rmi://" + this.IP + ":" + this.puerto + "/" + info.getUsuario());
            System.out.println("Cliente '" + info.getUsuario() + "' desregistrado en RMI.");

            // Notificar a los demás clientes sobre la conexión
            notificarAClientesConectados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para notificar a otros clientes que un nuevo cliente se ha conectado
    private void notificarAClientesConectados() {
        // Aquí, en lugar de un registro manual, puedes usar un servidor para obtener todas las referencias remotas de clientes conectados
        // Este es un ejemplo simple donde se asume que ya tienes la lista de clientes conectados.
        try {
            Client clienteRemoto = (Client) Naming.lookup("rmi://localhost/clienteRemoto");
            clienteRemoto.notificarConexion(info.getUsuario());  // Notificamos al cliente remoto
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean enviarMensaje(Client clientDestino, String contenido) {
        if (clientDestino == null || contenido == null || this.info == null) {
            return false;
        }
        // Si no está en línea, se manda falso.
        if (!clientDestino.getInfo().isOnline()) {
            return false;
        }
        if (!clientDestino.getInfo().getListaAmigos().contains(this.getInfo().getUsuario())) {
            return false;
        }
        try {
            // Buscar el objeto remoto del destinatario en el registro RMI
            ClientInterface destino = (ClientInterface) Naming.lookup("rmi://localhost/"
                    + clientDestino.getInfo().getUsuario());
            Mensaje mensaje = new Mensaje(this.getInfo(), clientDestino.getInfo(), contenido);
            Optional<Chat> chatDestino = clientDestino.obtenerChat(this.getInfo());
            // Actualizamos en el destinatario
            if (chatDestino.isPresent()) {
                destino.recibirMensaje(mensaje);  // Invocar el método remoto del destinatario
                // Actualizamos en el origen
                mensaje = new Mensaje(clientDestino.getInfo(), this.getInfo(), contenido);
                chatDestino = this.obtenerChat(clientDestino.getInfo());
                if (chatDestino.isPresent()) {
                    chatDestino.get().anadirMensaje(mensaje);
                    this.actualizarChat(chatDestino.get());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }





    /**
     *
     * @param
     */
    public void aceptarSolicitudAmistad(ClientInfo clienteSolicitante) {
        // Verificar que el cliente solicitante no sea nulo
        if (clienteSolicitante != null && this.info != null) {

            // 1. Añadir usuario(this) a clienteSolicitante.listaAmigos
            clienteSolicitante.getListaAmigos().add(this.getInfo().getUsuario());

            // 2. Añadir usuario(clienteSolicitante) a this.listaAmigos
            this.getInfo().getListaAmigos().add(clienteSolicitante.getUsuario());

            // 3. Borrar la solicitud de la lista de 'this'
            this.info.getListaSolicitudes().remove(clienteSolicitante.getUsuario());

            // (Opcional) Confirmación
            System.out.println("Solicitud de amistad aceptada: ");
        } else {
            // Si los objetos son nulos, manejar el error
            System.out.println("Error: Cliente o información no válida.");
        }
    }


    public void rechazarSolicitudAmistad(ClientInfo clienteSolicitante) {
        // Eliminar la solicitud de la lista de solicitudes
        this.info.getListaSolicitudes().remove(clienteSolicitante.getUsuario());
    }

    public List<String> obtenerNombresDeUsuario(List<ClientInfo> listaClientInfo) {
        return listaClientInfo.stream()  // Convierte la lista en un stream
                .map(ClientInfo::getUsuario) // Mapea cada ClientInfo a su atributo usuario
                .collect(Collectors.toList()); // Recoge los resultados en una lista
    }


    public Optional<Chat> obtenerChat(ClientInfo clientDestino) {
        if (this.info != null && clientDestino != null &&
                this.info.getListaAmigos().contains(clientDestino.getUsuario())) {
            return chats.stream()
                    .filter(chat -> chat.getClientes().containsValue(info) && chat.getClientes().containsValue(clientDestino))
                    .findFirst(); // Retorna el primer chat que cumpla la condición, o Optional.empty() si no se encuentra
        }
        return Optional.empty();
    }


    // Crea un chat entre el origen y un cliente destino
    public void crearChat(ClientInfo clientDestino) {
        if (clientDestino!=null && obtenerChat(clientDestino).isEmpty()) {
            Map<String, ClientInfo> clientes = new HashMap<>();
            clientes.put(clientDestino.getUsuario(), clientDestino);
            clientes.put(info.getUsuario(), info);
            Chat chat = new Chat(clientes);
            chats.add(chat);
        }
    }

    public void actualizarChat(Chat chat) {
        if (chat != null) {
            this.chats.remove(chat);
            this.chats.add(chat);
        }
    }


    @Override
    public void recibirMensaje(Mensaje mensaje) throws RemoteException {
        Optional<Chat> chat = this.obtenerChat(mensaje.getClienteOrigen());
        if (chat.isPresent()) {
            chat.get().anadirMensaje(mensaje);
            actualizarChat(chat.get());
        }
    }

    @Override
    public void notificarConexion(String username) throws RemoteException {

    }

    @Override
    public void notificarDesconexion(String username) throws RemoteException {

    }

    @Override
    public void recibirSolicitudAmistad(String fromUser) throws RemoteException {

    }

    @Override
    public void confirmarAmistad(String username) throws RemoteException {

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
