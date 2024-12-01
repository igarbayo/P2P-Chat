// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Client;

import java.io.Serializable;
import java.util.*;

public class Chat implements Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos
    private Set<String> clientes;
    private List<Mensaje> mensajes;

    // Getters y setters
    public Set<String> getClientes() {
        return clientes;
    }
    public void setClientes(Set<String> clientes) {
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
    public Chat(Set<String> clientes) {
        if (clientes!=null) {
            this.clientes = clientes;
        }
        this.mensajes = new ArrayList<>();
    }

    // Añadir un mensaje
    public void anadirMensaje(Mensaje mensaje) {
        if (mensaje!=null &&
                clientes.contains(mensaje.getClienteOrigen()) &&
                clientes.contains(mensaje.getClienteDestino())
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
