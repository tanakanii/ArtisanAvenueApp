package com.example.artisanavenue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.artisanavenue.databinding.ActivityChatsBinding;
import com.example.artisanavenue.databinding.ActivityUserBinding;
import com.example.artisanavenue.utilities.PreferenceManager;

public class Chats extends AppCompatActivity {

    private ActivityChatsBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();

    }
    private void setListeners(){
        binding.imageBack2.setOnClickListener(v -> onBackPressed());
        binding.favNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

}