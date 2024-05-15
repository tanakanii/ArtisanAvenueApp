package com.example.artisanavenue.models;

public class Product {
    private String id;
    private String name;
    private String category;
    private String price;
    private String description;
    private String image;
    private String userId;
    private String shopId;

    public Product(String id, String name, String category, String price, String description, String image, String userId, String shopId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.image = image;
        this.userId = userId;
        this.shopId = shopId;
    }

    public Product(String name, String category, String price, String description, String image) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.image = image;
    }


    public String getId() {
        return id;
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

    public String getUserId() {
        return userId;
    }

    public String getShopId() {
        return shopId;
    }
}
