package com.example.lcsr_inventory;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lcsr_inventory.databinding.FragmentClientesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class clientes extends Fragment {

    private FragmentClientesBinding binding;
    private ClienteAdapter adapter;
    private List<Cliente> listaClientes;
    private DatabaseReference clientesRef;
    private InventoryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClientesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        binding.clientesRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        listaClientes = new ArrayList<>();

        adapter = new ClienteAdapter(listaClientes, new ClienteAdapter.OnClienteListener() {
            @Override
            public void onItemClick(Cliente cliente) {
                mostrarDatosCliente(cliente);
            }

            @Override
            public void onEditClick(Cliente cliente) {
                mostrarPopupEditarCliente(cliente);
            }

            @Override
            public void onDeleteLongClick(Cliente cliente) {
                eliminarCliente(cliente);
            }
        });
        binding.clientesRvList.setAdapter(adapter);

        clientesRef = FirebaseDatabase.getInstance().getReference("clientes");
        cargarClientesDesdeFirebase();

        binding.clientesInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filtrar(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarClientesDesdeFirebase() {
        clientesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Cliente> temporal = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Cliente cliente = item.getValue(Cliente.class);
                    if (cliente != null) {
                        cliente.setId(item.getKey());
                        temporal.add(cliente);
                    }
                }
                Collections.sort(temporal, (c1, c2) -> c1.getNombre().compareToIgnoreCase(c2.getNombre()));
                adapter.actualizarLista(temporal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void mostrarDatosCliente(Cliente cliente) {
        PopupVerCliente popupVerCliente = new PopupVerCliente(cliente);
        popupVerCliente.show(getParentFragmentManager(), "PopupVerCliente");
    }

    private void mostrarPopupEditarCliente(Cliente cliente) {
        PopupFormCliente formCliente = new PopupFormCliente(cliente, new PopupFormCliente.OnClienteGuardadoListener() {
            @Override
            public void onGuardado(Cliente clienteActualizado) {
                viewModel.editarCliente(clienteActualizado);
                android.widget.Toast.makeText(getContext(), "Cliente actualizado", android.widget.Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelado() {
                // No hacemos nada
            }
        });
        formCliente.show(getParentFragmentManager(), "PopupClienteEdit");
    }

    private void eliminarCliente(Cliente cliente) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Cliente")
                .setMessage("¿Estás seguro de que deseas eliminar a " + cliente.getNombre() + " de tu directorio?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (cliente.getId() != null) {
                        viewModel.eliminarCliente(cliente.getId());
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
