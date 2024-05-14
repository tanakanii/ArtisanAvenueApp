package com.example.artisanavenue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.artisanavenue.databinding.ActivitySettingsBinding;
import com.example.artisanavenue.utilities.PreferenceManager;

public class Settings extends AppCompatActivity implements SettingsMenu.OnSettingsMenuInteractionListener {

    private ActivitySettingsBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Example condition to check which fragment to load initially
        boolean isAccountSettings = getIntent().getBooleanExtra("isAccountSettings", false);

        if (isAccountSettings) {
            replaceFragment(new Accounts());
        } else {
            replaceFragment(new SettingsMenu());
        }

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.settings_framelayout);
                if (currentFragment instanceof Accounts ||
                        currentFragment instanceof Notifications ||
                        currentFragment instanceof Address ||
                        currentFragment instanceof Payment) {
                    replaceFragment(new SettingsMenu());
                } else if (currentFragment instanceof SettingsMenu) {
                    finish(); // Navigate back to MainActivity
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.settings_framelayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onAccountSettingsSelected() {
        replaceFragment(new Accounts());
    }

    @Override
    public void onNotificationSettingsSelected() {
        replaceFragment(new Notifications());
    }

    @Override
    public void onAddressSettingsSelected() {
        replaceFragment(new Address());
    }

    @Override
    public void onPaymentMethodSettingsSelected() {
        replaceFragment(new Payment());
    }
}
