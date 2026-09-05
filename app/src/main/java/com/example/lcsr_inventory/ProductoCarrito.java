package com.example.lcsr_inventory;

public class ProductoCarrito {
    private String name;
    private double price;
    private int cantidad;

    public ProductoCarrito(String name, double price, int cantidad) {
        this.name = name;
        this.price = price;
        this.cantidad = cantidad;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return this.price * this.cantidad;
    }
}