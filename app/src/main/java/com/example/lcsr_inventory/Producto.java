package com.example.lcsr_inventory;

import com.google.firebase.database.PropertyName;

public class Producto {
    private String id;
    private String name;
    private double price;
    // Guardaremos el enlace HTTPS de Cloudinary
    private String imagePath; 
    private String imagePublicId; // ID único de la foto en Cloudinary para borrarla
    private String app_secret = "LCSR_2026_secreto"; // Secreto para escribir en DB
    
    // Dejamos este int para retrocompatibilidad temporal o imagen por defecto
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

    @PropertyName("imagePath")
    public String getImagePath() { return imagePath; }

    @PropertyName("imagePath")
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @PropertyName("imagePublicId")
    public String getImagePublicId() { return imagePublicId; }

    @PropertyName("imagePublicId")
    public void setImagePublicId(String imagePublicId) { this.imagePublicId = imagePublicId; }

    public int getImage() { return image; }
    public void setImage(int image) { this.image = image; }

    public String getApp_secret() { return app_secret; }
    public void setApp_secret(String app_secret) { this.app_secret = app_secret; }
}