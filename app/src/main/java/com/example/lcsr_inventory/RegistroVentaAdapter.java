package com.example.lcsr_inventory;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lcsr_inventory.databinding.ItemVentaBinding;

import java.util.List;
import java.util.Locale;

public class RegistroVentaAdapter extends RecyclerView.Adapter<RegistroVentaAdapter.VentaViewHolder> {

    private List<RegistroVenta> ventaList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RegistroVenta venta);
    }

    public RegistroVentaAdapter(List<RegistroVenta> ventaList, OnItemClickListener listener) {
        this.ventaList = ventaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVentaBinding binding = ItemVentaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new VentaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {
        RegistroVenta venta = ventaList.get(position);

        holder.binding.itemVentaId.setText(venta.getId());
        holder.binding.itemVentaDate.setText(venta.getDate());
        holder.binding.itemVentaTotal.setText(String.format(Locale.US, "$ %.2f", venta.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(venta);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ventaList.size();
    }

    public static class VentaViewHolder extends RecyclerView.ViewHolder {
        ItemVentaBinding binding;

        public VentaViewHolder(ItemVentaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}