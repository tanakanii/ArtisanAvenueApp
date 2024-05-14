package com.example.artisanavenue.models;

public class Product {
    private String name;
    private String category;
    private String price;
    private String description;
    private String image;

    public Product(String name, String category, String price, String description, String image) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
}
