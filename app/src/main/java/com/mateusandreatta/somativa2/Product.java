package com.mateusandreatta.somativa2;

import java.io.Serializable;

public class Product implements Serializable {
    long id;
    String name;
    int price;
    String photoPath;

    public Product() {
    }

    public Product(String name, int price, String photoPath) {
        this.name = name;
        this.price = price;
        this.photoPath = photoPath;
    }

    public Product(long id, String name, int price, String photoPath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.photoPath = photoPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
