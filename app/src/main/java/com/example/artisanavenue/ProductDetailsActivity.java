package com.example.artisanavenue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.artisanavenue.databinding.ActivityProductDetailsBinding;
import com.example.artisanavenue.models.Product;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ProductDetailsActivity extends AppCompatActivity {

    private ActivityProductDetailsBinding binding;
    private Product product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());

        loadProductDetails();

        binding.buttonAddToCart.setOnClickListener(v -> {
            String userId = preferenceManager.getString(Constants.KEY_USER_ID);
            Cart.getInstance(userId).addItem(product);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });

        binding.backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void loadProductDetails() {
        Intent intent = getIntent();
        String productId = intent.getStringExtra(Constants.KEY_PRODUCT_ID);

        if (productId != null) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_PRODUCTS)
                    .document(productId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            product = new Product(
                                    documentSnapshot.getId(),
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_NAME),
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_CATEGORY),
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_PRICE),
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION),
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE),
                                    documentSnapshot.getString(Constants.KEY_USER_ID),
                                    documentSnapshot.getString(Constants.KEY_SHOP_ID)
                            );

                            binding.textProductName.setText(product.getName());
                            binding.textProductPrice.setText(product.getPrice());
                            binding.textProductDescription.setText(product.getDescription());

                            String base64Image = product.getImage();
                            if (base64Image != null && !base64Image.isEmpty()) {
                                byte[] imageData = Base64.decode(base64Image, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                                binding.imageProduct.setImageBitmap(bitmap);
                            }

                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show());
        }
    }


}
