package com.example.artisanavenue;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find the logout button by its ID
        Button btnLogout = rootView.findViewById(R.id.btnLogout);

        // Set OnClickListener for the logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout button click here
                // Perform logout actions (e.g., clear session, navigate to sign-in screen)
                logoutUser();
            }
        });

        return rootView;
    }

    // Method to handle logout action
    private void logoutUser() {
        // Implement logout logic here
        // For example, you can clear session, navigate to sign-in screen, etc.
        // You can start a new activity (e.g., SignInActivity) after logout
        // Here's an example to navigate to the sign-in screen:
        startActivity(new Intent(getActivity(), SignIn.class));
        getActivity().finish(); // Close the current activity after navigating to sign-in
    }
}
