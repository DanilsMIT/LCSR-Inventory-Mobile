package com.example.lcsr_inventory;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lcsr_inventory.databinding.ItemClienteBinding;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> listaClientes;
    private List<Cliente> listaOriginal;
    private final OnClienteListener listener;

    public interface OnClienteListener {
        void onItemClick(Cliente cliente);
        void onEditClick(Cliente cliente);
        void onDeleteLongClick(Cliente cliente);
    }

    public ClienteAdapter(List<Cliente> listaClientes, OnClienteListener listener) {
        this.listaClientes = listaClientes;
        this.listaOriginal = new ArrayList<>(listaClientes);
        this.listener = listener;
    }

    public void actualizarLista(List<Cliente> nuevaLista) {
        this.listaClientes = nuevaLista;
        this.listaOriginal = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    public void filtrar(String textoBusqueda) {
        listaClientes.clear();
        if (textoBusqueda.trim().isEmpty()) {
            listaClientes.addAll(listaOriginal);
        } else {
            String busquedaNormalizada = limpiarTexto(textoBusqueda);

            for (Cliente cliente : listaOriginal) {
                String nombreNormalizado = limpiarTexto(cliente.getNombre());
                String cedula = cliente.getCedula() != null ? cliente.getCedula() : "";
                
                if (nombreNormalizado.contains(busquedaNormalizada) || cedula.contains(busquedaNormalizada)) {
                    listaClientes.add(cliente);
                }
            }
        }
        notifyDataSetChanged();
    }

    private String limpiarTexto(String texto) {
        if (texto == null) return "";
        String textoNormalizado = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD);
        return textoNormalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClienteBinding binding = ItemClienteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ClienteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);

        holder.binding.itemClienteNombre.setText(cliente.getNombre());
        holder.binding.itemClienteCedula.setText("C.I: " + cliente.getCedula());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(cliente);
        });
        
        holder.binding.itemClienteEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(cliente);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteLongClick(cliente);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        ItemClienteBinding binding;

        public ClienteViewHolder(ItemClienteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
