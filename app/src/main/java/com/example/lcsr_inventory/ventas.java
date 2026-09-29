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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Collections;

public class ventas extends Fragment {

    private FragmentVentasBinding binding;
    private RegistroVentaAdapter adapter;
    private List<RegistroVenta> listaVentas;
    private DatabaseReference ventasRef;

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

        adapter = new RegistroVentaAdapter(listaVentas, new RegistroVentaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RegistroVenta venta) {
                mostrarPopupDetalleVenta(venta);
            }

            @Override
            public void onDeleteClick(RegistroVenta venta) {
                eliminarRegistroVenta(venta);
            }
        });
        binding.ventasRvHistory.setAdapter(adapter);

        ventasRef = FirebaseDatabase.getInstance().getReference("ventas");
        cargarVentasDesdeFirebase();
    }

    private void cargarVentasDesdeFirebase() {
        ventasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaVentas.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    RegistroVenta venta = item.getValue(RegistroVenta.class);
                    if (venta != null) {
                        listaVentas.add(venta);
                    }
                }
                
                // Opcional: ordenar de más reciente a más antiguo
                Collections.reverse(listaVentas);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void eliminarRegistroVenta(RegistroVenta venta) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Registro")
                .setMessage("¿Estás seguro de que deseas eliminar este registro de venta?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (venta.getId() != null) {
                        ventasRef.child(venta.getId()).removeValue();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void mostrarPopupDetalleVenta(RegistroVenta venta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        PopupCarritoBinding cartBinding = PopupCarritoBinding.inflate(getLayoutInflater());
        builder.setView(cartBinding.getRoot());
        AlertDialog dialog = builder.create();

        cartBinding.popupCartTitle.setText("DETALLE DE VENTA");
        cartBinding.popupCartDate.setVisibility(View.VISIBLE);
        cartBinding.popupCartDate.setText("Fecha: " + venta.getDate());
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