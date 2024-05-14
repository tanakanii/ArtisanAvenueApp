package com.example.artisanavenue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artisanavenue.models.Product;
import com.example.artisanavenue.utilities.Constants;
import com.example.artisanavenue.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
public class Products extends Fragment {

    private RecyclerView recyclerView;
    private com.example.artisanavenue.ProductAdapter productAdapter;
    private List<Product> productList;
    private PreferenceManager preferenceManager;
    private View progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        progressBar = view.findViewById(R.id.progressBar3);
        productList = new ArrayList<>();
        productAdapter = new com.example.artisanavenue.ProductAdapter(productList);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(productAdapter);

        preferenceManager = new PreferenceManager(requireContext());
        loadProducts();

        return view;
    }

    private void loadProducts() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_PRODUCTS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Product product = new Product(
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_NAME),
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_CATEGORY),
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_PRICE),
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION),
                                    documentSnapshot.getString(Constants.KEY_PRODUCT_IMAGE)
                            );
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
}