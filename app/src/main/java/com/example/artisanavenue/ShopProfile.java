package com.example.artisanavenue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.artisanavenue.databinding.ActivityShopProfileBinding;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.util.Base64;

public class ShopProfile extends AppCompatActivity {

    private ActivityShopProfileBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        loadShopDetails();
        binding.addproductLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ShopProfile.this, AddProduct.class);
            startActivity(intent);
        });
        binding.imageBack3.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }

    private void loadShopDetails() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        database.collection(Constants.KEY_COLLECTION_SHOPS)
                .whereEqualTo(Constants.KEY_USER_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            binding.textShopName.setText(documentSnapshot.getString(Constants.KEY_SHOP_NAME));
                            binding.textShopAddress.setText(documentSnapshot.getString(Constants.KEY_SHOP_ADDRESS));
                            binding.textShopDescription.setText(documentSnapshot.getString(Constants.KEY_SHOP_DESCRIPTION));

                            String base64Image = documentSnapshot.getString(Constants.KEY_SHOP_IMAGE);
                            if (base64Image != null && !base64Image.isEmpty()) {
                                byte[] imageData = Base64.decode(base64Image, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                                if (binding != null) {
                                    binding.imageShopProfile.setImageBitmap(bitmap);
                                }
                            }
                        }
                    }
                });
    }
}
