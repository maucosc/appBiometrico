package com.example.gestionpass.models;

public class Pass {
    private String sitio;
    private String usuario;
    private String passw;
    private String notas;

    public Pass(String sitio, String usuario, String passw, String notas) {
        this.sitio = sitio;
        this.usuario = usuario;
        this.passw = passw;
        this.notas = notas;
    }

    public String getSitio() {
        return sitio;
    }

    public void setSitio(String sitio) {
        this.sitio = sitio;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassw() {
        return passw;
    }

    public void setPassw(String passw) {
        this.passw = passw;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
