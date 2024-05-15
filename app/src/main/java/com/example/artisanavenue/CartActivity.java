package com.example.artisanavenue;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artisanavenue.adapters.CartAdapter;
import com.example.artisanavenue.models.Product;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartItems;
    private PreferenceManager preferenceManager;
    private View progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        progressBar = findViewById(R.id.progressBarCart);
        preferenceManager = new PreferenceManager(getApplicationContext());

        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        Cart cart = Cart.getInstance(userId);
        cart.loadCartItems(cartItems -> {
            this.cartItems = cartItems;
            cartAdapter = new CartAdapter(cartItems);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(cartAdapter);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }
}