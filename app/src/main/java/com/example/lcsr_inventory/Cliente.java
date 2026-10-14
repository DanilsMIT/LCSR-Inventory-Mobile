package com.example.lcsr_inventory;

public class Cliente {
    private String id;
    private String nombre;
    private String cedula;
    private String ruc;
    private String celular;
    private String correo;
    private String direccion;
    private String app_secret = "LCSR_2026_secreto";

    public Cliente() {
        // Constructor vacío para Firebase
    }

    public Cliente(String nombre, String cedula, String ruc, String celular, String correo, String direccion) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.ruc = ruc;
        this.celular = celular;
        this.correo = correo;
        this.direccion = direccion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getApp_secret() { return app_secret; }
    public void setApp_secret(String app_secret) { this.app_secret = app_secret; }
}
