package com.example.lcsr_inventory;

import java.util.List;

public class RegistroVenta {
    private String id;
    private String date;
    private List<ProductoCarrito> productos;
    private double total;
    private String app_secret = "LCSR_2026_secreto"; // Secreto

    public RegistroVenta() {
        // Firebase necesita un constructor vacío.
        // También es buena idea inicializar la lista para evitar NullPointerExceptions
        this.productos = new java.util.ArrayList<>();
    }

    public RegistroVenta(String id, String date, List<ProductoCarrito> productos, double total) {
        this.id = id;
        this.date = date;
        this.productos = productos;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ProductoCarrito> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoCarrito> productos) {
        this.productos = productos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getApp_secret() { return app_secret; }
    public void setApp_secret(String app_secret) { this.app_secret = app_secret; }
}