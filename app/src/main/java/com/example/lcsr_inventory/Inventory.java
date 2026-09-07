package com.example.lcsr_inventory;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lcsr_inventory.databinding.FragmentInventoryBinding;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends Fragment {

    private FragmentInventoryBinding binding;
    private ProductoAdapter adapter;
    private List<Producto> productoList;

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

        productoList = new ArrayList<>();
        productoList.add(new Producto("Adaptador de compresor", 10.00));
        productoList.add(new Producto("Angulo galvanizado 45mm", 1.00));
        productoList.add(new Producto("Angulo prepintado (10 pies)", 1.50));

        adapter = new ProductoAdapter(productoList, new ProductoAdapter.OnProductListener() {
            @Override
            public void onEditClick(Producto producto) {
                mostrarPopupEditar(producto);
            }

            @Override
            public void onDeleteLongClick(Producto producto) {
                mostrarPopupEliminar();
            }
        });

        binding.inventoryRvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.inventoryRvProducts.setAdapter(adapter);

        binding.inventoryBtnAdd.setOnClickListener(v -> mostrarPopupAgregar());

        binding.inventoryBtnTotal.setOnClickListener(v -> mostrarPopupCarrito());
    }

    private void mostrarPopupAgregar() {
        PopupFormProduct formDialog = new PopupFormProduct((nombre, precio) -> {
        });
        formDialog.show(getParentFragmentManager(), "PopupFormAdd");
    }

    private void mostrarPopupEditar(Producto producto) {
        PopupFormProduct formDialog = new PopupFormProduct(producto.getName(), producto.getPrice(), (nombre, precio) -> {
        });
        formDialog.show(getParentFragmentManager(), "PopupFormEdit");
    }

    private void mostrarPopupEliminar() {
        PopupAlert alert = new PopupAlert();
        alert.setListener(() -> {
        });
        alert.show(getParentFragmentManager(), "PopupAlert");
    }

    private void mostrarPopupCarrito() {
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