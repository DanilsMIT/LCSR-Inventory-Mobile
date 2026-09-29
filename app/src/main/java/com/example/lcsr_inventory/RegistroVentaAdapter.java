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
        void onDeleteClick(RegistroVenta venta);
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

        // Generar un número de recibo amigable a partir del ID de Firebase
        String idCorto = venta.getId() != null && venta.getId().length() > 6 
            ? venta.getId().substring(venta.getId().length() - 6).toUpperCase() 
            : "000000";
            
        holder.binding.itemVentaId.setText("#REC-" + idCorto);
        holder.binding.itemVentaDate.setText(venta.getDate());
        holder.binding.itemVentaTotal.setText(String.format(Locale.US, "$ %.2f", venta.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(venta);
            }
        });

        // Eliminar historial con presión larga
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(venta);
            }
            return true;
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