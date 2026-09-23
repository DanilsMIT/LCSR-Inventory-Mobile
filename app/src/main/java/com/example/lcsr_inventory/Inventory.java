package com.example.lcsr_inventory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lcsr_inventory.databinding.FragmentInventoryBinding;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends Fragment {

    private FragmentInventoryBinding binding;
    private ProductoAdapter adapter;
    private InventoryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // Configuración del Adaptador y Eventos de los Items
        adapter = new ProductoAdapter(new ArrayList<>(), new ProductoAdapter.OnProductListener() {
            @Override
            public void onEditClick(Producto producto) {
                mostrarPopupEditar(producto);
            }

            @Override
            public void onDeleteLongClick(Producto producto) {
                mostrarPopupEliminar(producto);
            }
        });

        // Configuración del RecyclerView
        binding.inventoryRvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.inventoryRvProducts.setAdapter(adapter);

        // Observador de Datos en Tiempo Real (GET)
        viewModel.getProductos().observe(getViewLifecycleOwner(), productos -> {
            adapter.actualizarLista(productos);
        });

        // Barra de Búsqueda (Filtro Local)
        binding.inventoryInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filtrar(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Eventos de Botones Principales
        binding.inventoryBtnAdd.setOnClickListener(v -> mostrarPopupAgregar());
        binding.inventoryBtnTotal.setOnClickListener(v -> mostrarPopupCarrito());
    }

    // Funciones de Lanzamiento de Popups (Modales)
    private void mostrarPopupAgregar() {
        PopupFormProduct formDialog = new PopupFormProduct((nombre, precio) -> {
            viewModel.agregarProducto(nombre, precio);
        });
        formDialog.show(getParentFragmentManager(), "PopupFormAdd");
    }

    private void mostrarPopupEditar(Producto producto) {
        PopupFormProduct formDialog = new PopupFormProduct(producto.getName(), producto.getPrice(), (nombre, precio) -> {
            viewModel.editarProducto(producto.getId(), nombre, precio);
        });
        formDialog.show(getParentFragmentManager(), "PopupFormEdit");
    }

    private void mostrarPopupEliminar(Producto producto) {
        PopupAlert alert = new PopupAlert();
        alert.setListener(() -> {
            viewModel.eliminarProducto(producto.getId());
        });
        alert.show(getParentFragmentManager(), "PopupAlert");
    }

    private void mostrarPopupCarrito() {
        // Datos simulados para estructura inicial del carrito
        List<ProductoCarrito> productosCarritoFalsos = new ArrayList<>();
        productosCarritoFalsos.add(new ProductoCarrito("Adaptador de compresor", 10.00, 2));

        double totalPrueba = 20.00;

        PopupCarrito carritoDialog = new PopupCarrito(productosCarritoFalsos, totalPrueba, () -> {
        });
        carritoDialog.show(getParentFragmentManager(), "PopupCarrito");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}