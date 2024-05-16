package com.example.artisanavenue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.artisanavenue.adapters.HintAdapter;
import com.example.artisanavenue.databinding.ActivityAddProductBinding;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddProduct extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    private Spinner categorySpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        categorySpinner = findViewById(R.id.category_spinner);
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.artisan_categories));
        ArrayAdapter<String> adapter = new HintAdapter(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        binding.addProdBtn.setOnClickListener(v -> {
            if (isValidProductDetails()) {
                addProduct();
            }
        });

        binding.imageBack.setOnClickListener(v -> onBackPressed());
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

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void addProduct() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> product = new HashMap<>();
        product.put(Constants.KEY_PRODUCT_NAME, binding.textProductName.getText().toString());
//        product.put(Constants.KEY_PRODUCT_CATEGORY, binding.textProductCategory.getText().toString());
        product.put(Constants.KEY_PRODUCT_PRICE, binding.textProductPrice.getText().toString());
        product.put(Constants.KEY_PRODUCT_DESCRIPTION, binding.textProductDescription.getText().toString());
        product.put(Constants.KEY_PRODUCT_IMAGE, encodedImage);
        product.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        product.put(Constants.KEY_SHOP_ID, getIntent().getStringExtra(Constants.KEY_SHOP_ID));

        database.collection(Constants.KEY_COLLECTION_PRODUCTS)
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    showToast("Product added successfully!");
                    loading(false);
                    startActivity(new Intent(getApplicationContext(), ShopProfile.class));
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private boolean isValidProductDetails() {
        if (encodedImage == null) {
            showToast("Select product image");
            return false;
        } else if (binding.textProductName.getText().toString().trim().isEmpty()) {
            showToast("Enter product name");
            return false;
        }
//        else if (binding.textProductCategory.getText().toString().trim().isEmpty()) {
//            showToast("Enter category");
//            return false;
//        }
        else if (binding.textProductPrice.getText().toString().trim().isEmpty()) {
            showToast("Enter product price");
            return false;
        } else if (binding.textProductDescription.getText().toString().trim().isEmpty()) {
            showToast("Enter description");
            return false;
        } else {
            return true;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
