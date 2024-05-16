package com.example.artisanavenue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artisanavenue.adapters.CartAdapter;
import com.example.artisanavenue.models.Product;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemInteractionListener {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartItems;
    private PreferenceManager preferenceManager;
    private View progressBar;
    private TextView totalPriceTextView;
    ImageButton backButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        progressBar = findViewById(R.id.progressBarCart);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        preferenceManager = new PreferenceManager(getApplicationContext());
        backButton = findViewById(R.id.back_btn);

        backButton.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, MainActivity.class)));
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        Cart cart = Cart.getInstance(userId);
        cart.loadCartItems(cartItems -> {
            this.cartItems = cartItems;
            cartAdapter = new CartAdapter(cartItems, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(cartAdapter);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onCartItemCheckedChange(List<Product> selectedItems) {
        double totalPrice = 0.0;
        for (Product item : selectedItems) {
            totalPrice += Double.parseDouble(item.getPrice()) * item.getQuantity();
        }
        totalPriceTextView.setText(String.format("Total: ₱%.2f", totalPrice));
    }

    @Override
    public void onCartItemDeleted(Product product) {
        // Remove item from cartItems list
        cartItems.remove(product);
        // Notify adapter about the item removal
        cartAdapter.notifyDataSetChanged();
        // Update total price
        onCartItemCheckedChange(cartAdapter.getSelectedItems());

        // Remove item from database
        Cart cart = Cart.getInstance(preferenceManager.getString(Constants.KEY_USER_ID));
        cart.removeCartItem(product, isSuccess -> {
            if (isSuccess) {
                // Optionally show a success message
                runOnUiThread(() -> {
                    Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                });
            } else {
                // Optionally show an error message
                runOnUiThread(() -> {
                    Toast.makeText(CartActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}