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
    private Cliente clienteSeleccionado = null;

    public interface OnCartActionListener {
        void onConfirmOrder(Cliente cliente);
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

        ProductoCarritoAdapter adapter = new ProductoCarritoAdapter(listaProductos, (position, producto) -> {
            // Lógica cuando se elimina un producto del carrito tocando la papelera
            totalVenta -= producto.getSubtotal();
            listaProductos.remove(position); // Borramos por posición exacta, no por referencia fantasma
            binding.popupCartTxtTotal.setText(String.format(Locale.US, "TOTAL: $ %.2f", totalVenta));
            
            // Actualizamos visualmente el adaptador
            if (binding.popupCartRvItems.getAdapter() != null) {
                binding.popupCartRvItems.getAdapter().notifyItemRemoved(position);
                binding.popupCartRvItems.getAdapter().notifyItemRangeChanged(position, listaProductos.size());
            }
            
            // Si el carrito se queda vacío, cerramos el popup
            if (listaProductos.isEmpty()) {
                dismiss();
            }
        });
        
        binding.popupCartRvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.popupCartRvItems.setAdapter(adapter);

        binding.popupCartBtnClose.setOnClickListener(v -> dismiss());
        
        binding.popupCartCbFactura.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && clienteSeleccionado == null) {
                // Abre el nuevo popup que pregunta "¿Existente o Nuevo?"
                PopupSeleccionCliente selClienteDialog = new PopupSeleccionCliente(new PopupSeleccionCliente.OnSeleccionListener() {
                    @Override
                    public void onOpcionSeleccionada(boolean esNuevo) {
                        if (esNuevo) {
                            // Abrir formulario para crear cliente nuevo
                            abrirFormularioNuevoCliente();
                        } else {
                            // Abrir buscador de clientes existentes
                            abrirBuscadorCliente();
                        }
                    }

                    @Override
                    public void onCancelado() {
                        binding.popupCartCbFactura.setChecked(false);
                    }
                });
                selClienteDialog.show(getParentFragmentManager(), "PopupSelCliente");
            } else if (!isChecked) {
                clienteSeleccionado = null;
            }
        });

        binding.popupCartBtnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmOrder(clienteSeleccionado);
            }
            dismiss();
        });
    }

    private void abrirFormularioNuevoCliente() {
        PopupFormCliente formCliente = new PopupFormCliente(new PopupFormCliente.OnClienteGuardadoListener() {
            @Override
            public void onGuardado(Cliente cliente) {
                clienteSeleccionado = cliente;
            }

            @Override
            public void onCancelado() {
                binding.popupCartCbFactura.setChecked(false);
            }
        });
        formCliente.show(getParentFragmentManager(), "PopupCliente");
    }

    private void abrirBuscadorCliente() {
        PopupBuscarCliente buscadorCliente = new PopupBuscarCliente(new PopupBuscarCliente.OnClienteGuardadoListener() {
            @Override
            public void onGuardado(Cliente cliente) {
                clienteSeleccionado = cliente;
            }

            @Override
            public void onCancelado() {
                binding.popupCartCbFactura.setChecked(false);
            }
        });
        buscadorCliente.show(getParentFragmentManager(), "PopupBuscarCliente");
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