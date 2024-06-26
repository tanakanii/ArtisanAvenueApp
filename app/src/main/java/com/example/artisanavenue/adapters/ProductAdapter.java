package com.example.artisanavenue.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artisanavenue.R;
import com.example.artisanavenue.listeners.ProductClickListener;
import com.example.artisanavenue.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> products;
    private final ProductClickListener productClickListener;

    public ProductAdapter(List<Product> products, ProductClickListener productClickListener) {
        this.products = products;
        this.productClickListener = productClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.textProductName.setText(product.getName());
        holder.textProductPrice.setText(product.getPrice());

        String base64Image = product.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] imageData = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            holder.imageProduct.setImageBitmap(bitmap);
        } else {
            holder.imageProduct.setImageResource(R.drawable.interest_textile); // Placeholder image if no image is available
        }

        holder.itemView.setOnClickListener(v -> productClickListener.onProductClicked(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textProductPrice;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textProductPrice = itemView.findViewById(R.id.textProductPrice);
        }
    }
}
