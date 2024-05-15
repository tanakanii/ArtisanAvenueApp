package com.example.artisanavenue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.artisanavenue.databinding.ActivityMainBinding;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    ImageButton settingsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsButton = findViewById(R.id.settings_btn_profile);
        settingsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Settings.class)));

        preferenceManager = new PreferenceManager(getApplicationContext());
        replaceFragment(new Home());
        setListeners();
        getToken();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new Home());
            } else if (itemId == R.id.products) {
                replaceFragment(new Products());
            } else if (itemId == R.id.artisans) {
                replaceFragment(new Artisans());
            } else if (itemId == R.id.blogs) {
                replaceFragment(new Blogs());
            } else if (itemId == R.id.profile) {
                replaceFragment(new Profile());
            }

            return true;
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
    private void setListeners() {

        binding.chatsBtn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), Chats.class)));
        binding.cartBtn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), CartActivity.class)));
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private  void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }



}