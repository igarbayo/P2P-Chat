package com.Client;

import java.util.Objects;

public class Mensaje {

    // Atributos
    private String clienteOrigen;
    private String clienteDestino;
    private String contenido;

    // Getters y setters
    public String getClienteOrigen() {
        return clienteOrigen;
    }
    public void setClienteOrigen(String clienteOrigen) {
        if (clienteOrigen!=null) {
            this.clienteOrigen = clienteOrigen;
        }
    }
    public String getClienteDestino() {
        return clienteDestino;
    }
    public void setClienteDestino(String clienteDestino) {
        if (clienteDestino!=null) {
            this.clienteDestino = clienteDestino;
        }
    }
    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        if (contenido!=null) {
            this.contenido = contenido;
        }
    }

    // Constructores
    public Mensaje(String clienteOrigen, String clienteDestino, String contenido) {
        if (clienteOrigen!=null) {
            this.clienteOrigen = clienteOrigen;
        }
        if (clienteDestino!=null) {
            this.clienteDestino = clienteDestino;
        }
        if (contenido!=null) {
            this.contenido = contenido;
        }
    }

    // Equals para comparaciones
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(clienteOrigen, mensaje.clienteOrigen) && Objects.equals(clienteDestino, mensaje.clienteDestino) && Objects.equals(contenido, mensaje.contenido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clienteOrigen, clienteDestino, contenido);
    }

    @Override
    public String toString() {
        return "[" + clienteOrigen + "] " + contenido;
    }
}
