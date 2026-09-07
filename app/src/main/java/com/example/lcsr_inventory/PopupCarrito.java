package com.example.lcsr_inventory;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lcsr_inventory.databinding.PopupCarritoBinding;

import java.util.List;
import java.util.Locale;

public class PopupCarrito extends DialogFragment {

    private PopupCarritoBinding binding;
    private List<ProductoCarrito> listaProductos;
    private double totalVenta;
    private OnCartActionListener listener;

    public interface OnCartActionListener {
        void onConfirmOrder();
    }

    public PopupCarrito(List<ProductoCarrito> listaProductos, double totalVenta, OnCartActionListener listener) {
        this.listaProductos = listaProductos;
        this.totalVenta = totalVenta;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupCarritoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.popupCartTxtTotal.setText(String.format(Locale.US, "TOTAL: $ %.2f", totalVenta));

        ProductoCarritoAdapter adapter = new ProductoCarritoAdapter(listaProductos, null);
        binding.popupCartRvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.popupCartRvItems.setAdapter(adapter);

        binding.popupCartBtnClose.setOnClickListener(v -> dismiss());
        binding.popupCartBtnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmOrder();
            }
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}