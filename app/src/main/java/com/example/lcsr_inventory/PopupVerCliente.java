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

import com.example.lcsr_inventory.databinding.PopupVerClienteBinding;

public class PopupVerCliente extends DialogFragment {

    private PopupVerClienteBinding binding;
    private final Cliente cliente;

    public PopupVerCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupVerClienteBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.popupVerClienteBtnClose.setOnClickListener(v -> dismiss());

        binding.popupVerClienteNombre.setText(cliente.getNombre());
        binding.popupVerClienteValCedula.setText(cliente.getCedula());
        
        binding.popupVerClienteValRuc.setText(cliente.getRuc().isEmpty() ? "N/A" : cliente.getRuc());
        binding.popupVerClienteValCelular.setText(cliente.getCelular().isEmpty() ? "N/A" : cliente.getCelular());
        binding.popupVerClienteValCorreo.setText(cliente.getCorreo().isEmpty() ? "N/A" : cliente.getCorreo());
        binding.popupVerClienteValDireccion.setText(cliente.getDireccion().isEmpty() ? "N/A" : cliente.getDireccion());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
