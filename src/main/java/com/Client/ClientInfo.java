package com.Client;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;

public class ClientInfo implements Serializable {

    // Atributos
    private String usuario;
    private String contrasena;
    private Integer idGrupo;
    private List<String> listaSolicitudes;
    private boolean online;

    // Getters y Setters
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    public Integer getIdGrupo() {
        if (idGrupo == null) {
            return null;
        } else {
            return idGrupo;
        }
    }
    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }
    public List<String> getListaSolicitudes() {
        return listaSolicitudes;
    }
    public void setListaSolicitudes(List<String> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }
    public boolean isOnline() {
        return online;
    }
    public void setOnline(boolean online) {
        this.online = online;
    }

    // Constructor
    public ClientInfo(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.idGrupo = null;
        this.listaSolicitudes = new ArrayList<String>();
        this.online = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientInfo that = (ClientInfo) o;
        return Objects.equals(usuario, that.usuario); // Compara por el nombre de usuario
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario); // Genera un hash basado en el nombre de usuario
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "usuario='" + usuario + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", idGrupo=" + idGrupo +
                ", listaSolicitudes=" + listaSolicitudes +
                ", online=" + online +
                '}';
    }
}
