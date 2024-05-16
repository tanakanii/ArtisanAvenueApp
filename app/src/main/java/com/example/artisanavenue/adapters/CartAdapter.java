package com.example.artisanavenue.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artisanavenue.R;
import com.example.artisanavenue.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<Product> cartItems;
    private final OnCartItemInteractionListener listener;

    public interface OnCartItemInteractionListener {
        void onCartItemCheckedChange(List<Product> selectedItems);
        void onCartItemDeleted(Product product);
    }

    public CartAdapter(List<Product> cartItems, OnCartItemInteractionListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);
        holder.textProductName.setText(product.getName());
        holder.textProductPrice.setText(product.getPrice());
        holder.textQuantity.setText(String.valueOf(product.getQuantity()));

        String base64Image = product.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] imageData = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            holder.imageProduct.setImageBitmap(bitmap);
        } else {
            holder.imageProduct.setImageResource(R.drawable.interest_textile);
        }

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(product.isChecked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setChecked(isChecked);
            listener.onCartItemCheckedChange(getSelectedItems());
        });

        holder.plusBtn.setOnClickListener(v -> {
            int currentQuantity = product.getQuantity();
            product.setQuantity(currentQuantity + 1);
            holder.textQuantity.setText(String.valueOf(product.getQuantity()));
            listener.onCartItemCheckedChange(getSelectedItems());
        });

        holder.minusBtn.setOnClickListener(v -> {
            int currentQuantity = product.getQuantity();
            if (currentQuantity > 1) {
                product.setQuantity(currentQuantity - 1);
                holder.textQuantity.setText(String.valueOf(product.getQuantity()));
                listener.onCartItemCheckedChange(getSelectedItems());
            }
        });

        holder.cartItemDelete.setOnClickListener(v -> {
            listener.onCartItemDeleted(product);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<Product> getSelectedItems() {
        List<Product> selectedItems = new ArrayList<>();
        for (Product item : cartItems) {
            if (item.isChecked()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textProductPrice, textQuantity;
        CheckBox checkBox;
        ImageButton plusBtn, minusBtn, cartItemDelete;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textProductPrice = itemView.findViewById(R.id.textProductPrice);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            checkBox = itemView.findViewById(R.id.checkBox);
            plusBtn = itemView.findViewById(R.id.plusBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            cartItemDelete = itemView.findViewById(R.id.cartItemDelete);
        }
    }
}