package com.example.artisanavenue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.artisanavenue.databinding.ActivityAddShopBinding;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class AddShop extends AppCompatActivity {

    private ActivityAddShopBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        checkIfShopExists();
    }

    private void checkIfShopExists() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        database.collection(Constants.KEY_COLLECTION_SHOPS)
                .whereEqualTo(Constants.KEY_USER_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // User already has a shop, redirect to ShopProfile
                        Intent intent = new Intent(getApplicationContext(), ShopProfile.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        setListeners();
                    }
                });
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.addShopBtn.setOnClickListener(v -> {
            if (isValidShopDetails()) {
                addShop();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addShop() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> shop = new HashMap<>();
        shop.put(Constants.KEY_SHOP_NAME, binding.textShop.getText().toString());
        shop.put(Constants.KEY_SHOP_ADDRESS, binding.textAddress.getText().toString());
        shop.put(Constants.KEY_SHOP_DESCRIPTION, binding.textDescription.getText().toString());
        shop.put(Constants.KEY_SHOP_IMAGE, encodedImage);
        shop.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        database.collection(Constants.KEY_COLLECTION_SHOPS)
                .add(shop)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    showToast("Shop added successfully!");
                    finish();
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 250;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.lblAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private boolean isValidShopDetails() {
        if (encodedImage == null) {
            showToast("Select shop image");
            return false;
        } else if (binding.textShop.getText().toString().trim().isEmpty()) {
            showToast("Enter shop name");
            return false;
        } else if (binding.textAddress.getText().toString().trim().isEmpty()) {
            showToast("Enter shop address");
            return false;
        } else if (binding.textDescription.getText().toString().trim().isEmpty()) {
            showToast("Enter shop description");
            return false;
        } else {
            return true;
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.addShopBtn.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.addShopBtn.setVisibility(View.VISIBLE);
        }
    }
}
