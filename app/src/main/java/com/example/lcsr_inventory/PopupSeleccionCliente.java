package com.example.lcsr_inventory;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lcsr_inventory.databinding.PopupSeleccionClienteBinding;

public class PopupSeleccionCliente extends DialogFragment {

    private PopupSeleccionClienteBinding binding;
    private OnSeleccionListener listener;

    public interface OnSeleccionListener {
        void onOpcionSeleccionada(boolean esNuevo); // true = Nuevo, false = Buscar Existente
        void onCancelado();
    }

    public PopupSeleccionCliente(OnSeleccionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupSeleccionClienteBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.popupSelClienteBtnNuevo.setOnClickListener(v -> {
            if (listener != null) listener.onOpcionSeleccionada(true);
            dismiss();
        });

        binding.popupSelClienteBtnBuscar.setOnClickListener(v -> {
            if (listener != null) listener.onOpcionSeleccionada(false);
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // Si el usuario toca afuera, cancelamos el proceso del checkbox
            getDialog().setOnCancelListener(dialog -> {
                if (listener != null) listener.onCancelado();
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
