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

import com.example.lcsr_inventory.databinding.PopupBuscarClienteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PopupBuscarCliente extends DialogFragment {

    private PopupBuscarClienteBinding binding;
    private Cliente clienteEncontrado = null;
    private final OnClienteGuardadoListener listener;

    public interface OnClienteGuardadoListener {
        void onGuardado(Cliente cliente);
        void onCancelado();
    }

    public PopupBuscarCliente(OnClienteGuardadoListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupBuscarClienteBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.popupBuscarClienteBtnClose.setOnClickListener(v -> {
            if (listener != null) listener.onCancelado();
            dismiss();
        });

        binding.popupBuscarClienteBtnSearch.setOnClickListener(v -> {
            String busqueda = binding.popupBuscarClienteInputRuc.getText().toString().trim();
            if (busqueda.isEmpty()) {
                Toast.makeText(getContext(), "Ingrese Cédula o RUC", Toast.LENGTH_SHORT).show();
                return;
            }

            buscarCliente(busqueda);
        });

        binding.popupBuscarClienteBtnSelect.setOnClickListener(v -> {
            if (clienteEncontrado != null && listener != null) {
                listener.onGuardado(clienteEncontrado);
                dismiss();
            }
        });
    }

    private void buscarCliente(String busqueda) {
        binding.popupBuscarClienteBtnSearch.setEnabled(false);
        binding.popupBuscarClienteBtnSearch.setText("...");
        binding.popupBuscarClienteCardResult.setVisibility(View.GONE);
        binding.popupBuscarClienteBtnSelect.setVisibility(View.GONE);
        binding.popupBuscarClienteTvError.setVisibility(View.GONE);

        // Firebase no permite OR en consultas complejas, buscamos por cédula y luego por ruc si falla.
        Query queryRuc = FirebaseDatabase.getInstance().getReference("clientes").orderByChild("ruc").equalTo(busqueda);
        queryRuc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    procesarResultado(snapshot);
                } else {
                    buscarPorCedula(busqueda);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarError();
            }
        });
    }

    private void buscarPorCedula(String busqueda) {
        Query queryCedula = FirebaseDatabase.getInstance().getReference("clientes").orderByChild("cedula").equalTo(busqueda);
        queryCedula.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    procesarResultado(snapshot);
                } else {
                    mostrarError();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarError();
            }
        });
    }

    private void procesarResultado(DataSnapshot snapshot) {
        binding.popupBuscarClienteBtnSearch.setEnabled(true);
        binding.popupBuscarClienteBtnSearch.setText("Buscar");

        for (DataSnapshot item : snapshot.getChildren()) {
            clienteEncontrado = item.getValue(Cliente.class);
            if (clienteEncontrado != null) {
                clienteEncontrado.setId(item.getKey());
                binding.popupBuscarClienteTvNombre.setText(clienteEncontrado.getNombre());
                binding.popupBuscarClienteTvCedula.setText("C.I/RUC: " + (clienteEncontrado.getRuc().isEmpty() ? clienteEncontrado.getCedula() : clienteEncontrado.getRuc()));
                
                binding.popupBuscarClienteCardResult.setVisibility(View.VISIBLE);
                binding.popupBuscarClienteBtnSelect.setVisibility(View.VISIBLE);
                return; // Solo tomamos el primero
            }
        }
    }

    private void mostrarError() {
        binding.popupBuscarClienteBtnSearch.setEnabled(true);
        binding.popupBuscarClienteBtnSearch.setText("Buscar");
        binding.popupBuscarClienteTvError.setVisibility(View.VISIBLE);
        clienteEncontrado = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
