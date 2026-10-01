package com.example.lcsr_inventory;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lcsr_inventory.databinding.ItemProductCarritoBinding;

import java.util.List;
import java.util.Locale;

public class ProductoCarritoAdapter extends RecyclerView.Adapter<ProductoCarritoAdapter.CarritoViewHolder> {

    private final List<ProductoCarrito> carritoList;
    private final OnCartListener listener;

    public interface OnCartListener {
        void onDeleteCartItem(int position, ProductoCarrito producto);
    }

    private boolean isReadOnly;

    public ProductoCarritoAdapter(List<ProductoCarrito> carritoList, OnCartListener listener) {
        this.carritoList = carritoList;
        this.listener = listener;
        this.isReadOnly = (listener == null);
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductCarritoBinding binding = ItemProductCarritoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new CarritoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        ProductoCarrito producto = carritoList.get(position);

        holder.binding.itemCartName.setText(producto.getName());
        holder.binding.itemCartPrice.setText(String.format(Locale.US, "$ %.2f", producto.getPrice()));
        holder.binding.itemCartQuantity.setText(String.valueOf(producto.getCantidad()));
        holder.binding.itemCartSubtotal.setText(String.format(Locale.US, "$ %.2f", producto.getSubtotal()));

        // Ocultamos el tacho de basura si estamos en modo Solo Lectura (Historial)
        if (isReadOnly) {
            holder.binding.itemCartBtnDelete.setVisibility(android.view.View.GONE);
        } else {
            holder.binding.itemCartBtnDelete.setVisibility(android.view.View.VISIBLE);
            
            // Botón explícito para eliminar del carrito (tachito)
            holder.binding.itemCartBtnDelete.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteCartItem(pos, producto);
                }
            });
            
            // También mantenemos el "mantener presionado" por si a alguien se le escapa el dedo
            holder.itemView.setOnLongClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteCartItem(pos, producto);
                }
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return carritoList.size();
    }

    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        ItemProductCarritoBinding binding;

        public CarritoViewHolder(ItemProductCarritoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}