package com.example.artisanavenue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.artisanavenue.databinding.ActivityMainBinding;
import com.example.artisanavenue.databinding.FragmentProfileBinding;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Base64;
import android.widget.Toast;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentProfileBinding binding;
    private PreferenceManager preferenceManager;
    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using data binding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        preferenceManager = new PreferenceManager(requireContext()); // Use requireContext()

        loadUserDetails(); // Load user details from preferences

        // Set OnClickListener for the logout button
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        return rootView;
    }


    private void loadUserDetails() {
        String firstName = preferenceManager.getString(Constants.KEY_FIRST_NAME);
        String lastName = preferenceManager.getString(Constants.KEY_LAST_NAME);
        String fullName = firstName + " " + lastName;

        // Ensure binding object is initialized and views are available
        if (binding != null) {
            binding.textName.setText(fullName); // Set user's name
        }

        String base64Image = preferenceManager.getString(Constants.KEY_IMAGE);
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] imageData = Base64.decode(base64Image, Base64.DEFAULT); // Use Base64.decode directly
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            // Ensure binding object is initialized and views are available
            if (binding != null) {
                binding.profileImage.setImageBitmap(bitmap); // Set user's profile image
            }
        }
    }
    private void showToast(String message){
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", null)
                .show();
    }
    private void logoutUser() {
        showToast("Signed out!");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(requireActivity(), SignIn.class));
                });
    }

}
