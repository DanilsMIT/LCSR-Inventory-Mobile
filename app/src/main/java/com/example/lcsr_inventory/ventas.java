package com.example.lcsr_inventory;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lcsr_inventory.databinding.FragmentVentasBinding;
import com.example.lcsr_inventory.databinding.PopupCarritoBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ventas extends Fragment {

    private FragmentVentasBinding binding;
    private RegistroVentaAdapter adapter;
    private List<RegistroVenta> listaVentas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVentasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ventasRvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        listaVentas = new ArrayList<>();

        List<ProductoCarrito> productosFalsos = new ArrayList<>();
        productosFalsos.add(new ProductoCarrito("Adaptador de compresor", 10.00, 2));
        productosFalsos.add(new ProductoCarrito("Angulo galvanizado 45mm", 1.00, 1));

        listaVentas.add(new RegistroVenta("REC-001", "05/09/2026", productosFalsos, 21.00));
        listaVentas.add(new RegistroVenta("REC-002", "04/09/2026", productosFalsos, 21.00));

        adapter = new RegistroVentaAdapter(listaVentas, venta -> {
            mostrarPopupDetalleVenta(venta);
        });

        binding.ventasRvHistory.setAdapter(adapter);
    }

    private void mostrarPopupDetalleVenta(RegistroVenta venta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        PopupCarritoBinding cartBinding = PopupCarritoBinding.inflate(getLayoutInflater());
        builder.setView(cartBinding.getRoot());
        AlertDialog dialog = builder.create();

        cartBinding.popupCartTitle.setText("DETALLE: " + venta.getId());
        cartBinding.popupCartTxtTotal.setText(String.format(Locale.US, "TOTAL: $ %.2f", venta.getTotal()));

        cartBinding.popupCartBtnConfirm.setText("CERRAR");
        cartBinding.popupCartBtnConfirm.setOnClickListener(v -> dialog.dismiss());
        cartBinding.popupCartBtnClose.setOnClickListener(v -> dialog.dismiss());

        ProductoCarritoAdapter carritoAdapter = new ProductoCarritoAdapter(venta.getProductos(), null);
        cartBinding.popupCartRvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartBinding.popupCartRvItems.setAdapter(carritoAdapter);

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