package com.example.artisanavenue;

import com.example.artisanavenue.models.Product;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    private static Cart instance;
    private final List<Product> cartItems;
    private final FirebaseFirestore database;
    private final String userId;

    private Cart(String userId) {
        this.userId = userId;
        cartItems = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
    }

    public static Cart getInstance(String userId) {
        if (instance == null) {
            instance = new Cart(userId);
        }
        return instance;
    }

    public void addItem(Product product) {
        cartItems.add(product);
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put(Constants.KEY_PRODUCT_ID, product.getId());
        cartItem.put(Constants.KEY_PRODUCT_NAME, product.getName());
        cartItem.put(Constants.KEY_PRODUCT_CATEGORY, product.getCategory());
        cartItem.put(Constants.KEY_PRODUCT_PRICE, product.getPrice());
        cartItem.put(Constants.KEY_PRODUCT_DESCRIPTION, product.getDescription());
        cartItem.put(Constants.KEY_PRODUCT_IMAGE, product.getImage());
        cartItem.put(Constants.KEY_USER_ID, product.getUserId());
        cartItem.put(Constants.KEY_SHOP_ID, product.getShopId());

        database.collection(Constants.KEY_COLLECTION_CART)
                .document(userId)
                .collection(Constants.KEY_COLLECTION_CART_ITEMS)
                .add(cartItem);
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
        database.collection(Constants.KEY_COLLECTION_CART)
                .document(userId)
                .collection(Constants.KEY_COLLECTION_CART_ITEMS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        document.getReference().delete();
                    }
                });
    }

    public void loadCartItems(CartLoadListener listener) {
        database.collection(Constants.KEY_COLLECTION_CART)
                .document(userId)
                .collection(Constants.KEY_COLLECTION_CART_ITEMS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Product product = new Product(
                                document.getId(),
                                document.getString(Constants.KEY_PRODUCT_NAME),
                                document.getString(Constants.KEY_PRODUCT_CATEGORY),
                                document.getString(Constants.KEY_PRODUCT_PRICE),
                                document.getString(Constants.KEY_PRODUCT_DESCRIPTION),
                                document.getString(Constants.KEY_PRODUCT_IMAGE),
                                document.getString(Constants.KEY_USER_ID),
                                document.getString(Constants.KEY_SHOP_ID)
                        );
                        cartItems.add(product);
                    }
                    listener.onCartLoaded(cartItems);
                });
    }

    public interface CartLoadListener {
        void onCartLoaded(List<Product> cartItems);
    }
}
