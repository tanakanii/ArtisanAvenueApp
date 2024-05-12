package com.example.artisanavenue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.artisanavenue.databinding.ActivitySigninBinding;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignIn extends AppCompatActivity {

    EditText username, password;

    private ActivitySigninBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();


/*
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (user.equals("") || pass.equals(""))
                    Toast.makeText(SignIn.this, "Please complete the form!", Toast.LENGTH_SHORT).show();
                else {
                    Boolean checkuserpass = DB.checkusernamepassword(user, pass);
                    if(checkuserpass==true){
                        Toast.makeText(SignIn.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignIn.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
*/

    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void setListeners() {
        binding.btnSignUp2.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUp.class)));
        binding.btnSignIn.setOnClickListener(v -> {
            if(isValidSignUpDetails()){
                signIn();
            }
        });
    }
    private void signIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_USERNAME, binding.txtUsername.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.txtPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                        && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });


    }
    private Boolean isValidSignUpDetails(){
        if (binding.txtUsername.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.txtUsername.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        }else if (binding.txtPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        } else {
            return true;
        }

    }
    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar2.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar2.setVisibility(View.INVISIBLE);
        }
    }
}