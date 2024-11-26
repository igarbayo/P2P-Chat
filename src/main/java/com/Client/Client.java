package com.Client;

import com.Server.ServerInterface;
import javafx.application.Platform;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;



//Serializable
public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {
    //private static final long serialVersionUID = 1L;

    // Atributos
    private ClientInfo info;
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
    private PrincipalController principalController;

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
            String URL = "rmi://" + ip + ":" + puerto + "/" + info.getUsuario();
            System.out.println(URL);
            Naming.rebind(URL, this);
            System.out.println("Cliente '" + info.getUsuario() + "' registrado en RMI.");
            this.IP = ip;
            this.puerto = puerto;

            // Notificar a los demás clientes sobre la conexión
            //notificarAClientesConectados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cerrarConexion() throws RemoteException {
        try {
            System.out.println(this.IP);
            System.out.println(this.puerto);
            System.out.println(this.info);
            // Desregistrar el objeto remoto en el registro RMI con el nombre de usuario
            if (this.IP != null && this.info!=null && this.info.getUsuario()!=null) {
                String URL = "rmi://" + this.IP + ":" + this.puerto + "/" + this.info.getUsuario();
                System.out.println(URL);
                Naming.unbind(URL);
                System.out.println("Cliente '" + info.getUsuario() + "' desregistrado en RMI.");

                // Notificar a los demás clientes sobre la conexión
                //notificarAClientesConectados();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Creo que esta ya no nos hace falta, se notifica a los clientes desde el servidor en añadirCliene llamo a notificar.
    // Método para notificar a otros clientes que un nuevo cliente se ha conectado
    /*private void notificarAClientesConectados() throws RemoteException, MalformedURLException, NotBoundException {
        // Aquí, en lugar de un registro manual, puedes usar un servidor para obtener todas las referencias remotas de clientes conectados
        // Este es un ejemplo simple donde se asume que ya tienes la lista de clientes conectados.
        List<String> listaClientesConectados = new ArrayList<>();
        ServerInterface servidor=(ServerInterface) Naming.lookup("rmi://"+ IP + ":"+ puerto+"/server");
        listaClientesConectados=obtenerNombresDeUsuario(servidor.obtenerAmigosEnLinea(this));
        for (String usuarioRemoto : listaClientesConectados) {
            try {
                ClientInterface clienteRemoto = (Client) Naming.lookup("rmi://localhost/"+usuarioRemoto);
                //clienteRemoto.notificarConexion(this.info.getUsuario());  // Notificamos al cliente remoto
                //falta clienteRemotoRecibirNotificacion
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }*/

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

    public void eliminarAmigo(ClientInfo amigo) {
        if (amigo != null && this.info != null) {
            //Elimino el usuarioAmigo del amigo
            amigo.getListaAmigos().remove(this.getInfo().getUsuario());
            //Elimino el amigo del usuario
            this.getInfo().getListaAmigos().remove(amigo.getUsuario());
            System.out.println("Amigo eliminado: " + amigo.getUsuario());
        }else{
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
    public void setPrincipalController(PrincipalController principalController) throws RemoteException {
        this.principalController=principalController;
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
        // Si el usuario está en la lista de amigos, actualizamos su estado a conectado
        if (this.info.getListaAmigos().contains(username)) {
            try {
                // Buscamos el cliente en el registro RMI
                ClientInterface clienteRemoto = (ClientInterface) Naming.lookup("rmi://" + IP + ":" + puerto + "/" + username);
                // Si el cliente existe en el registro, significa que está conectado
                if (clienteRemoto != null) {
                    //faltaria algo para recibirNotificacion. clienteRemoto.recibirNotificacion o clienteRemotoRecibirNotificacion
                    System.out.println("El usuario " + this.getInfo().getUsuario() + " se ha conectado");
                }
            } catch (Exception e) {
                System.err.println("Error al notificar conexión del usuario " + username);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void notificarDesconexion(String username) throws RemoteException {
        // Si el usuario está en la lista de amigos, actualizamos su estado a desconectado
        if (this.info.getListaAmigos().contains(username)) {
            try {
                ClientInterface clienteRemoto = (ClientInterface) Naming.lookup("rmi://" + IP + ":" + puerto + "/" + username);
                //faltaria algo como recibirNotificacionDesconexion
                System.out.println("El usuario " + this.getInfo().getUsuario() + " se ha desconectado");

            } catch (Exception e) {
                System.err.println("Error al notificar desconexión del usuario " + username);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void recibirSolicitudAmistad(String fromUser) throws RemoteException {
        if (fromUser != null && !this.info.getListaAmigos().contains(fromUser)) {
            // Añadimos el usuario a la lista de solicitudes pendientes
            this.info.getListaSolicitudes().add(fromUser);
            System.out.println("Nueva solicitud de amistad recibida de: " + fromUser);
        }
    }

    @Override
    public void confirmarAmistad(String username) throws RemoteException {
        if (username != null) {
            try {
                // Añadir el usuario a la lista de amigos si no está ya
                if (!this.info.getListaAmigos().contains(username)) {
                    this.info.getListaAmigos().add(username);

                    // Crear un nuevo chat con el usuario
                    ClientInterface clienteRemoto = (ClientInterface) Naming.lookup("rmi://" + IP + ":" + puerto + "/" + username);
                    //falta algo clienteRemoto.RecibirSolicitudAceptada.
                    /*if (clienteRemoto != null) {
                        this.crearChat(((Client)clienteRemoto).getInfo());
                    }*/
                    clienteRemoto.recibirNotificacion("Amistad confirmada con"+username);

                    System.out.println("Amistad confirmada con: " + username);
                }
            } catch (Exception e) {
                System.err.println("Error al confirmar amistad con " + username);
                e.printStackTrace();
            }
        }
    }


    @Override
    public void recibirNotificacion(String mensaje) throws RemoteException {
        Platform.runLater(() -> principalController.printEnConsola(mensaje));
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

