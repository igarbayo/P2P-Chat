// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Client;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;

    // Atributos
    private String clienteOrigen;
    private String clienteDestino;
    private String contenido;
    private String tiempoFormateado;

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
    public String getTiempoFormateado() {
        return tiempoFormateado;
    }
    public void setTiempoFormateado(String tiempoFormateado) {
        if (tiempoFormateado!=null) {
            this.tiempoFormateado = tiempoFormateado;
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
        LocalTime tiempoActual = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        tiempoFormateado=tiempoActual.format(formato);
    }

    public Mensaje(String clienteOrigen, String clienteDestino, String contenido, String tiempoFormateado) {
        if (clienteOrigen!=null) {
            this.clienteOrigen = clienteOrigen;
        }
        if (clienteDestino!=null) {
            this.clienteDestino = clienteDestino;
        }
        if (contenido!=null) {
            this.contenido = contenido;
        }
        if (tiempoFormateado!=null) {
            this.tiempoFormateado = tiempoFormateado;
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
        return "[" + tiempoFormateado + "]" + " [de " + clienteOrigen + "] " + contenido;
    }

    public String StringIzquierda() {
        return "[" + tiempoFormateado + "] " + contenido;
    }

    public String StringDerecha() {
        return contenido + " [" + tiempoFormateado + "]";
    }
}
