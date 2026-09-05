package com.example.lcsr_inventory;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lcsr_inventory.databinding.ItemProductBinding;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productList;
    private OnProductListener listener;

    public interface OnProductListener {
        void onEditClick(Producto producto);
        void onDeleteLongClick(Producto producto);
    }

    public ProductoAdapter(List<Producto> productList, OnProductListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ProductoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productList.get(position);

        holder.binding.itemProductName.setText(producto.getName());
        holder.binding.itemProductPrice.setText("$ " + producto.getPrice());
        holder.binding.itemProductImg.setImageResource(producto.getImage());

        holder.binding.itemProductBtnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(producto);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteLongClick(producto);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;

        public ProductoViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}