package com.example.artisanavenue;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.artisanavenue.databinding.FragmentSettingsMenuBinding;

public class SettingsMenu extends Fragment {

    private FragmentSettingsMenuBinding binding;
    private OnSettingsMenuInteractionListener listener;

    public interface OnSettingsMenuInteractionListener {
        void onAccountSettingsSelected();
        void onNotificationSettingsSelected();
        void onAddressSettingsSelected();
        void onPaymentMethodSettingsSelected();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsMenuInteractionListener) {
            listener = (OnSettingsMenuInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsMenuInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsMenuBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set click listeners for buttons
        binding.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAccountSettingsSelected();
            }
        });

        binding.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNotificationSettingsSelected();
            }
        });

        binding.addresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddressSettingsSelected();
            }
        });

        binding.paymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPaymentMethodSettingsSelected();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
