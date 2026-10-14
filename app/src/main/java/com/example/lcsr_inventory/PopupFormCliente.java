package com.example.lcsr_inventory;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lcsr_inventory.databinding.PopupFormClienteBinding;

public class PopupFormCliente extends DialogFragment {

    private PopupFormClienteBinding binding;
    private OnClienteGuardadoListener listener;

    private boolean esEdicion = false;
    private Cliente clienteAEditar = null;

    public interface OnClienteGuardadoListener {
        void onGuardado(Cliente cliente);
        void onCancelado();
    }

    // Constructor para NUEVO cliente
    public PopupFormCliente(OnClienteGuardadoListener listener) {
        this.listener = listener;
    }
    
    // Constructor para EDITAR cliente
    public PopupFormCliente(Cliente clienteAEditar, OnClienteGuardadoListener listener) {
        this.listener = listener;
        this.clienteAEditar = clienteAEditar;
        this.esEdicion = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupFormClienteBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Si es edición, precargamos los datos en las cajitas
        if (esEdicion && clienteAEditar != null) {
            binding.formClienteNombre.setText(clienteAEditar.getNombre());
            binding.formClienteCedula.setText(clienteAEditar.getCedula());
            binding.formClienteRuc.setText(clienteAEditar.getRuc());
            binding.formClienteCelular.setText(clienteAEditar.getCelular());
            binding.formClienteCorreo.setText(clienteAEditar.getCorreo());
            binding.formClienteDireccion.setText(clienteAEditar.getDireccion());
        }

        binding.formClienteBtnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancelado();
            dismiss();
        });

        binding.formClienteBtnSave.setOnClickListener(v -> {
            String nombre = binding.formClienteNombre.getText().toString().trim();
            String cedula = binding.formClienteCedula.getText().toString().trim();
            String ruc = binding.formClienteRuc.getText().toString().trim();
            String celular = binding.formClienteCelular.getText().toString().trim();
            String correo = binding.formClienteCorreo.getText().toString().trim();
            String direccion = binding.formClienteDireccion.getText().toString().trim();

            if (nombre.isEmpty() || cedula.isEmpty()) {
                Toast.makeText(getContext(), "Nombre y Cédula son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            Cliente nuevoCliente = new Cliente(nombre, cedula, ruc, celular, correo, direccion);
            
            // Si estábamos editando, hay que mantener el ID que ya tenía el cliente
            if (esEdicion && clienteAEditar != null) {
                nuevoCliente.setId(clienteAEditar.getId());
            }
            
            if (listener != null) listener.onGuardado(nuevoCliente);
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().setCanceledOnTouchOutside(false); // Obligamos a usar los botones para no perder datos
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
