package com.example.lcsr_inventory;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lcsr_inventory.databinding.ItemProductBinding;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    // Variables de Lista y Listener
    private List<Producto> listaProductos;
    private List<Producto> listaOriginal;
    private OnProductListener listener;

    // Interfaz de Eventos
    public interface OnProductListener {
        void onEditClick(Producto producto);
        void onDeleteLongClick(Producto producto);
    }

    // Constructor
    public ProductoAdapter(List<Producto> listaProductos, OnProductListener listener) {
        this.listaProductos = listaProductos;
        this.listaOriginal = new ArrayList<>(listaProductos);
        this.listener = listener;
    }

    // Actualización de Datos
    public void actualizarLista(List<Producto> nuevaLista) {
        this.listaProductos = nuevaLista;
        this.listaOriginal = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    // Filtro de Búsqueda tipo google
    public void filtrar(String textoBusqueda) {
        listaProductos.clear();
        if (textoBusqueda.trim().isEmpty()) {
            listaProductos.addAll(listaOriginal);
        } else {
            String busquedaNormalizada = limpiarTexto(textoBusqueda);
            String[] palabrasBuscadas = busquedaNormalizada.split(" ");

            for (Producto producto : listaOriginal) {
                String nombreNormalizado = limpiarTexto(producto.getName());


                boolean contieneTodas = true;
                for (String palabra : palabrasBuscadas) {
                    if (!nombreNormalizado.contains(palabra)) {
                        contieneTodas = false;
                        break;
                    }
                }

                if (contieneTodas) {
                    listaProductos.add(producto);
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

    // Creación de Vistas
    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ProductoViewHolder(binding);
    }

    // Vinculación de Datos y Eventos
    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);

        holder.binding.itemProductName.setText(producto.getName());
        holder.binding.itemProductPrice.setText("$ " + producto.getPrice());
        holder.binding.itemProductImg.setImageResource(producto.getImage());

        holder.binding.itemProductEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(producto);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteLongClick(producto);
            }
            return true;
        });
    }

    // Conteo de Elementos
    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    // Clase ViewHolder
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;

        public ProductoViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}