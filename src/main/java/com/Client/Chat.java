package com.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Chat {

    // Atributos
    private Map<String, ClientInfo> clientes;
    private List<Mensaje> mensajes;

    // Getters y setters
    public Map<String, ClientInfo> getClientes() {
        return clientes;
    }
    public void setClientes(Map<String, ClientInfo> clientes) {
        if (clientes != null) {
            this.clientes = clientes;
        }
    }
    public List<Mensaje> getMensajes() {
        return mensajes;
    }
    public void setMensajes(List<Mensaje> mensajes) {
        if (mensajes!=null) {
            this.mensajes = mensajes;
        }
    }

    // Constructor
    public Chat(Map<String, ClientInfo> clientes) {
        if (clientes!=null) {
            this.clientes = clientes;
        }
        this.mensajes = new ArrayList<>();
    }

    // Añadir un mensaje
    public void anadirMensaje(Mensaje mensaje) {
        if (mensaje!=null &&
                clientes.containsValue(mensaje.getClienteOrigen()) &&
                clientes.containsValue(mensaje.getClienteDestino())
        ) {
            this.mensajes.add(mensaje);
        }
    }

    // Eliminar mensaje
    public void eliminarMensaje(Mensaje mensaje) {
        if (mensaje!=null) {
            mensajes.remove(mensaje);
        }
    }

    // Método equals
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(clientes, chat.clientes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientes);
    }
}
