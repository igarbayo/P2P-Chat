package com.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientInfo {

    // Atributos
    private String usuario;
    private String contrasena;
    private Integer idGrupo;
    private List<ClientInfo> listaSolicitudes;

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
    public List<ClientInfo> getListaSolicitudes() {
        return listaSolicitudes;
    }
    public void setListaSolicitudes(List<ClientInfo> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    // Constructor
    public ClientInfo(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.idGrupo = null;
        this.listaSolicitudes = new ArrayList<ClientInfo>();
    }

}
