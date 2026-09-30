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

        // Ya no mostraremos el ID feo, en su lugar pondremos "Venta - [Fecha sin hora]"
        String fechaCompleta = venta.getDate(); // Ej: "30/09/2026 13:04"
        String fechaCorta = "Venta";
        String horaCorta = "";
        
        if (fechaCompleta != null && fechaCompleta.length() >= 10) {
            fechaCorta = "Venta del " + fechaCompleta.substring(0, 10); // "Venta del 30/09/2026"
            if (fechaCompleta.length() > 10) {
                horaCorta = "Hora: " + fechaCompleta.substring(11).trim(); // "Hora: 13:04"
            }
        }
            
        holder.binding.itemVentaId.setText(fechaCorta);
        holder.binding.itemVentaDate.setText(horaCorta);
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