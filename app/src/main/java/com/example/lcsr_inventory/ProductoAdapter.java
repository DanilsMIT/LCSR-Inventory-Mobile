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

    // Variables de Lista y Listener (Solo dejamos uno)
    private List<Producto> listaProductos;
    private List<Producto> listaOriginal;
    private final OnProductListener listener;

    // Interfaz de Eventos Unificada
    public interface OnProductListener {
        void onEditClick(Producto producto);
        void onDeleteLongClick(Producto producto);
        void onItemClick(Producto producto); // <- Para abrir el popup de cantidad
        void onImageClick(Producto producto); // <- Ver imagen en grande
        void onImageLongClick(Producto producto); // <- Cambiar foto
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
        // Forzamos siempre 2 decimales usando String.format
        holder.binding.itemProductPrice.setText(String.format(java.util.Locale.US, "$ %.2f", producto.getPrice()));
        
        // Cargar imagen usando Glide (Soporta links HTTPS de Cloudinary y optimización en caché)
        if (producto.getImagePath() != null && !producto.getImagePath().isEmpty()) {
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(producto.getImagePath())
                    .placeholder(producto.getImage()) // Logo por defecto mientras carga
                    .error(producto.getImage()) // Logo por defecto si falla el internet
                    .into(holder.binding.itemProductImg);
        } else {
            holder.binding.itemProductImg.setImageResource(producto.getImage());
        }

        // Clic simple en la imagen (Ver foto en grande)
        holder.binding.itemProductImg.setOnClickListener(v -> {
            if (listener != null) listener.onImageClick(producto);
        });

        // Mantener presionado en la imagen (Cambiar foto)
        holder.binding.itemProductImg.setOnLongClickListener(v -> {
            if (listener != null) listener.onImageLongClick(producto);
            return true;
        });

        // 1. Clic en el botón de Editar
        holder.binding.itemProductEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(producto);
        });

        // 2. Mantener presionado en la tarjeta para Eliminar
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteLongClick(producto);
            }
            return true; // true indica que consumimos el evento (no activa el clic normal)
        });

        // 3. NUEVO: Toque simple en la tarjeta para el Carrito
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(producto);
            }
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