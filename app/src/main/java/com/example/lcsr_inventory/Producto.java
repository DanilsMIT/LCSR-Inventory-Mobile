package com.example.lcsr_inventory;

import com.google.firebase.database.PropertyName;

public class Producto {
    private String id;
    private String name;
    private double price;
    private int image = R.drawable.logo;

    public Producto() {
    }

    public Producto(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("articulo")
    public String getName() { return name; }

    @PropertyName("articulo")
    public void setName(String name) { this.name = name; }

    @PropertyName("precio")
    public double getPrice() { return price; }

    @PropertyName("precio")
    public void setPrice(double price) { this.price = price; }

    public int getImage() { return image; }
    public void setImage(int image) { this.image = image; }
}