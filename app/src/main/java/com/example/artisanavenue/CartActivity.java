package com.example.artisanavenue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.artisanavenue.adapters.CartAdapter;
import com.example.artisanavenue.models.Product;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.view.PaymentFlowActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemInteractionListener {

    String PUBLIC_KEY = "pk_test_51PH5WbCeWJDQcwdIks78WQMdFMtDk2VWImjqWcrgRh2wi8N8yPhO9sBe3CC9XopIDbR5liQC4vlqBEGAbtVPLDsK005w4w4xSz";
    String SECRET_KEY = "sk_test_51PH5WbCeWJDQcwdIgUJxrWaZkhX4GPykxcSo30Klt0DACvVJnxLMBPX3fQbGp1q9wYGmyYEpyJjXWDSVsiUzxbkG00yjMDSnC9";
    PaymentSheet paymentSheet;
    String customerID;
    String EphemeralKey;
    String ClientSecret;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartItems;
    private PreferenceManager preferenceManager;
    private View progressBar;
    private TextView totalPriceText;
    private Button totalPriceButton;
    ImageButton backButton;
    private String totalValueString;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        progressBar = findViewById(R.id.progressBarCart);
//        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        totalPriceText = findViewById(R.id.totalPriceText);
        totalPriceButton = findViewById(R.id.totalPriceButton);
        preferenceManager = new PreferenceManager(getApplicationContext());
        backButton = findViewById(R.id.back_btn);

        PaymentConfiguration.init(this, PUBLIC_KEY);
        getCustomer();
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        totalPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFlow();
            }
        });

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

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
            getCustomer();
        } else {
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCustomer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
//                            Toast.makeText(CartActivity.this, customerID, Toast.LENGTH_SHORT).show();

                            getEphemeralKey(customerID);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getEphemeralKey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            EphemeralKey = object.getString("id");
//                            Toast.makeText(CartActivity.this, EphemeralKey, Toast.LENGTH_SHORT).show();

                            getClientSecret(customerID,EphemeralKey);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                header.put("Stripe-Version","2020-08-27");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephemeralKey) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
//                            Toast.makeText(CartActivity.this, ClientSecret, Toast.LENGTH_SHORT).show();

//                            PaymentFlow();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerID);
                params.put("amount", totalValueString);
                params.put("currency","usd");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }

    private void PaymentFlow() {
        paymentSheet.presentWithPaymentIntent(ClientSecret,
                new PaymentSheet.Configuration("Artisan Avenue",
                        new PaymentSheet.CustomerConfiguration(customerID, EphemeralKey)));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onCartItemCheckedChange(List<Product> selectedItems) {
        double totalPrice = 0.0;
        for (Product item : selectedItems) {
            totalPrice += Double.parseDouble(item.getPrice()) * item.getQuantity();
        }
//        totalPriceTextView.setText(String.format("Total: ₱%.2f", totalPrice));
//        totalPriceButton.setText(String.format("Total: ₱%.2f", totalPrice));
        totalPriceText.setText(String.format("₱%.2f", totalPrice));
        totalValueString = totalPriceButton.getText().toString().replaceAll("[^\\d]", "");
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