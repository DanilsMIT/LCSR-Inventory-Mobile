package com.example.lcsr_inventory;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lcsr_inventory.databinding.FragmentInventoryBinding;
import com.example.lcsr_inventory.databinding.PopupCarritoBinding;
import com.example.lcsr_inventory.databinding.PopupFormProductBinding;
import com.example.lcsr_inventory.databinding.PopupAlertBinding;

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
                mostrarPopupFormulario("Editar Producto");
            }

            @Override
            public void onDeleteLongClick(Producto producto) {
                mostrarPopupEliminar();
            }
        });

        binding.inventoryRvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.inventoryRvProducts.setAdapter(adapter);

        binding.inventoryBtnAdd.setOnClickListener(v -> mostrarPopupFormulario("Agregar Producto"));

        binding.inventoryBtnTotal.setOnClickListener(v -> mostrarPopupCarrito());
    }

    private void mostrarPopupFormulario(String titulo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        PopupFormProductBinding formBinding = PopupFormProductBinding.inflate(getLayoutInflater());
        builder.setView(formBinding.getRoot());
        AlertDialog dialog = builder.create();

        formBinding.popupProductTitle.setText(titulo);
        formBinding.popupProductBtnClose.setOnClickListener(v -> dialog.dismiss());
        formBinding.popupProductBtnSave.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }

    private void mostrarPopupEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        PopupAlertBinding alertBinding = PopupAlertBinding.inflate(getLayoutInflater());
        builder.setView(alertBinding.getRoot());
        AlertDialog dialog = builder.create();

        alertBinding.popupDeleteBtnAccept.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }

    private void mostrarPopupCarrito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        PopupCarritoBinding cartBinding = PopupCarritoBinding.inflate(getLayoutInflater());
        builder.setView(cartBinding.getRoot());
        AlertDialog dialog = builder.create();

        cartBinding.popupCartBtnClose.setOnClickListener(v -> dialog.dismiss());
        cartBinding.popupCartBtnConfirm.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}