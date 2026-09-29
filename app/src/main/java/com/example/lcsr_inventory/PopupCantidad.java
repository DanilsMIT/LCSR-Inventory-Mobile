package com.example.lcsr_inventory;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lcsr_inventory.databinding.PopupCantidadBinding;

public class PopupCantidad extends DialogFragment {

    private PopupCantidadBinding binding;
    private Producto producto;
    private OnProductoAgregadoListener listener;
    private int cantidadActual = 1;

    public interface OnProductoAgregadoListener {
        void onProductoAgregado(Producto producto, int cantidad, double subtotal);
    }

    public PopupCantidad(Producto producto, OnProductoAgregadoListener listener) {
        this.producto = producto;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupCantidadBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Asegurarnos de que el Dialog ocupe el ancho completo de la pantalla para que los márgenes del CardView funcionen bien
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.popupCantidadTvName.setText(producto.getName());
        binding.popupCantidadTvPrice.setText("$ " + producto.getPrice() + " c/u");
        actualizarSubtotal();

        // Botón Menos
        binding.popupCantidadBtnMinus.setOnClickListener(v -> {
            obtenerCantidadDelEditText();
            if (cantidadActual > 1) {
                cantidadActual--;
                binding.popupCantidadInputQty.setText(String.valueOf(cantidadActual));
            }
        });

        // Botón Más
        binding.popupCantidadBtnPlus.setOnClickListener(v -> {
            obtenerCantidadDelEditText();
            cantidadActual++;
            binding.popupCantidadInputQty.setText(String.valueOf(cantidadActual));
        });

        binding.popupCantidadBtnClose.setOnClickListener(v -> dismiss());

        binding.popupCantidadInputQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                obtenerCantidadDelEditText();
                actualizarSubtotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Botón Agregar al Carrito
        binding.popupCantidadBtnAdd.setOnClickListener(v -> {
            obtenerCantidadDelEditText();
            if (cantidadActual > 0) {
                double subtotal = cantidadActual * producto.getPrice();
                listener.onProductoAgregado(producto, cantidadActual, subtotal);
                dismiss();
            } else {
                Toast.makeText(getContext(), "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerCantidadDelEditText() {
        String texto = binding.popupCantidadInputQty.getText().toString();
        if (!texto.isEmpty()) {
            try {
                cantidadActual = Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                cantidadActual = 1;
            }
        } else {
            cantidadActual = 0;
        }
    }

    private void actualizarSubtotal() {
        double subtotal = cantidadActual * producto.getPrice();
        binding.popupCantidadTvSubtotal.setText("Subtotal: $ " + String.format("%.2f", subtotal));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}