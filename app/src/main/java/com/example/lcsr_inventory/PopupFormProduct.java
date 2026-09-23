package com.example.lcsr_inventory;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lcsr_inventory.databinding.PopupFormProductBinding;

public class PopupFormProduct extends DialogFragment {

    private PopupFormProductBinding binding;
    private String tituloAccion;
    private String nombreActual;
    private double precioActual;
    private boolean esEdicion;
    private OnProductSaveListener listener;

    public interface OnProductSaveListener {
        void onSave(String nombre, double precio);
    }

    // Constructor para cuando es NUEVO producto
    public PopupFormProduct(OnProductSaveListener listener) {
        this.tituloAccion = "Agregar Producto";
        this.nombreActual = "";
        this.precioActual = 0.0;
        this.esEdicion = false;
        this.listener = listener;
    }

    // Constructor para cuando es EDITAR producto (recibe datos previos)
    public PopupFormProduct(String nombreActual, double precioActual, OnProductSaveListener listener) {
        this.tituloAccion = "Editar Producto";
        this.nombreActual = nombreActual;
        this.precioActual = precioActual;
        this.esEdicion = true;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupFormProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.popupProductTitle.setText(tituloAccion);

        if (esEdicion) {
            binding.popupProductInputName.setText(nombreActual);
            binding.popupProductInputPrice.setText(String.valueOf(precioActual));
        }

        binding.popupProductBtnClose.setOnClickListener(v -> dismiss());

        binding.popupProductBtnSave.setOnClickListener(v -> {
            String nombre = binding.popupProductInputName.getText().toString().trim();
            String precioStr = binding.popupProductInputPrice.getText().toString().trim();

            if (!nombre.isEmpty() && !precioStr.isEmpty()) {
                double precio = Double.parseDouble(precioStr);
                if (listener != null) {
                    listener.onSave(nombre, precio);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}