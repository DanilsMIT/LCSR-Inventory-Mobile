package com.example.lcsr_inventory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lcsr_inventory.databinding.FragmentInventoryBinding;

import android.net.Uri;
import android.provider.MediaStore;
import android.content.ContentValues;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends Fragment {

    private FragmentInventoryBinding binding;
    private ProductoAdapter adapter;
    private InventoryViewModel viewModel;

    // Variables del Carrito
    private final List<ProductoCarrito> carritoActual = new ArrayList<>();
    private double totalCarrito = 0.0;

    // Variables para la captura de fotos
    private Producto productoParaImagen = null;
    private Uri uriCamaraTemporal = null;

    // Lanzador para seleccionar foto de la Galería
    private final ActivityResultLauncher<String> galeriaLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && productoParaImagen != null) {
                    procesarYGuardarImagen(uri);
                }
            }
    );

    // Lanzador para la Cámara
    private final ActivityResultLauncher<Uri> camaraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            exito -> {
                if (exito && uriCamaraTemporal != null && productoParaImagen != null) {
                    procesarYGuardarImagen(uriCamaraTemporal);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // Configuración del Adaptador y Eventos de los Items
        adapter = new ProductoAdapter(new ArrayList<>(), new ProductoAdapter.OnProductListener() {
            @Override
            public void onEditClick(Producto producto) {
                mostrarPopupEditar(producto);
            }

            @Override
            public void onDeleteLongClick(Producto producto) {
                mostrarPopupEliminar(producto);
            }

            @Override
            public void onItemClick(Producto producto) {
                mostrarPopupCantidad(producto);
            }

            @Override
            public void onImageClick(Producto producto) {
                mostrarVisorImagen(producto);
            }

            @Override
            public void onImageLongClick(Producto producto) {
                mostrarOpcionesImagen(producto);
            }
        });

        // Configuración del RecyclerView
        binding.inventoryRvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.inventoryRvProducts.setAdapter(adapter);

        // Observador de Datos en Tiempo Real (GET)
        viewModel.getProductos().observe(getViewLifecycleOwner(), productos -> {
            adapter.actualizarLista(productos);
        });

        // Barra de Búsqueda (Filtro Local)
        binding.inventoryInputSearch.addTextChangedListener(new TextWatcher() {
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

        // Eventos de Botones Principales
        binding.inventoryBtnAdd.setOnClickListener(v -> mostrarPopupAgregar());
        binding.inventoryBtnTotal.setOnClickListener(v -> mostrarPopupCarrito());

        // Inicializar estado del botón del carrito
        actualizarBotonCarrito();
    }

    // Funciones de Lanzamiento de Popups (Modales)
    private void mostrarPopupAgregar() {
        PopupFormProduct formDialog = new PopupFormProduct((nombre, precio) -> {
            viewModel.agregarProducto(nombre, precio);
        });
        formDialog.show(getParentFragmentManager(), "PopupFormAdd");
    }

    private void mostrarPopupEditar(Producto producto) {
        PopupFormProduct formDialog = new PopupFormProduct(producto.getName(), producto.getPrice(), (nombre, precio) -> {
            viewModel.editarProducto(producto.getId(), nombre, precio);
            android.widget.Toast.makeText(getContext(), "Producto actualizado", android.widget.Toast.LENGTH_SHORT).show();
        });
        formDialog.show(getParentFragmentManager(), "PopupFormEdit");
    }

    // Funciones para manejar la imagen
    private void mostrarVisorImagen(Producto producto) {
        PopupImageViewer viewer = new PopupImageViewer(producto.getImagePath(), producto.getImage());
        viewer.show(getParentFragmentManager(), "PopupImgViewer");
    }

    private void mostrarOpcionesImagen(Producto producto) {
        productoParaImagen = producto;
        PopupImageOptions opcionesDialog = new PopupImageOptions(opcion -> {
            if (opcion == 0) {
                abrirCamara();
            } else if (opcion == 1) {
                galeriaLauncher.launch("image/*");
            }
        });
        opcionesDialog.show(getParentFragmentManager(), "PopupImgOpts");
    }

    private void abrirCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Foto_Producto_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DESCRIPTION, "Foto capturada para inventario LCSR");
        uriCamaraTemporal = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uriCamaraTemporal != null) {
            camaraLauncher.launch(uriCamaraTemporal);
        }
    }

    private void procesarYGuardarImagen(Uri uri) {
        // Mostramos un Toast para avisar que está trabajando
        android.widget.Toast.makeText(getContext(), "Subiendo imagen a la nube, por favor espera...", android.widget.Toast.LENGTH_LONG).show();
        
        com.example.lcsr_inventory.utils.ImageUtils.subirImagenACloudinary(getContext(), uri, new com.example.lcsr_inventory.utils.ImageUtils.OnImageUploadListener() {
            @Override
            public void onSuccess(String resultData) {
                // Parseamos el resultado que viene como "URL|||PublicID"
                String[] parts = resultData.split("\\|\\|\\|");
                String imageUrl = parts[0];
                String publicId = parts.length > 1 ? parts[1] : "";

                // BORRAMOS LA FOTO VIEJA DE CLOUDINARY
                if (productoParaImagen != null && productoParaImagen.getImagePublicId() != null && !productoParaImagen.getImagePublicId().isEmpty()) {
                    new Thread(() -> {
                        com.example.lcsr_inventory.utils.ImageUtils.borrarImagenDeCloudinary(getContext(), productoParaImagen.getImagePublicId());
                    }).start();
                }

                // Guardamos la nueva en Firebase
                viewModel.actualizarImagenProducto(productoParaImagen.getId(), imageUrl, publicId);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        android.widget.Toast.makeText(getContext(), "¡Imagen actualizada exitosamente!", android.widget.Toast.LENGTH_SHORT).show();
                        productoParaImagen = null;
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        android.widget.Toast.makeText(getContext(), "Error: " + error, android.widget.Toast.LENGTH_SHORT).show();
                        productoParaImagen = null;
                    });
                }
            }
        });
    }

    private void mostrarPopupEliminar(Producto producto) {
        PopupAlert alert = new PopupAlert();
        alert.setListener(() -> {
            viewModel.eliminarProducto(producto);
        });
        alert.show(getParentFragmentManager(), "PopupAlert");
    }

    private void mostrarPopupCantidad(Producto producto) {
        PopupCantidad cantidadDialog = new PopupCantidad(producto, (prod, cantidad, subtotal) -> {
            // Verificar si el producto ya está en el carrito
            boolean existe = false;
            for (ProductoCarrito pc : carritoActual) {
                if (pc.getName().equals(prod.getName())) {
                    pc.setCantidad(pc.getCantidad() + cantidad);
                    existe = true;
                    break;
                }
            }
            // Si no existe, lo agregamos como nuevo
            if (!existe) {
                carritoActual.add(new ProductoCarrito(prod.getName(), prod.getPrice(), cantidad));
            }
            
            calcularTotalCarrito();
        });
        cantidadDialog.show(getParentFragmentManager(), "PopupCantidad");
    }

    private void mostrarPopupCarrito() {
        PopupCarrito carritoDialog = new PopupCarrito(carritoActual, totalCarrito, () -> {
            // Lógica al confirmar la venta: enviar a Firebase
            viewModel.registrarVenta(new ArrayList<>(carritoActual), totalCarrito);
            
            // Vaciar carrito
            carritoActual.clear();
            calcularTotalCarrito();
            android.widget.Toast.makeText(getContext(), "¡Venta Confirmada!", android.widget.Toast.LENGTH_SHORT).show();
        });
        
        // Agregar un listener por si el usuario elimina cosas del carrito y lo cierra
        carritoDialog.getLifecycle().addObserver(new androidx.lifecycle.DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull androidx.lifecycle.LifecycleOwner owner) {
                calcularTotalCarrito(); // Recalcular al cerrar el carrito
            }
        });

        carritoDialog.show(getParentFragmentManager(), "PopupCarrito");
    }

    private void calcularTotalCarrito() {
        totalCarrito = 0.0;
        for (ProductoCarrito pc : carritoActual) {
            totalCarrito += pc.getSubtotal();
        }
        actualizarBotonCarrito();
    }

    private void actualizarBotonCarrito() {
        if (carritoActual.isEmpty()) {
            binding.inventoryBtnTotal.setEnabled(false);
            binding.inventoryBtnTotal.setAlpha(0.5f);
            binding.inventoryBtnTotal.setText("TOTAL: $ 0.00");
        } else {
            binding.inventoryBtnTotal.setEnabled(true);
            binding.inventoryBtnTotal.setAlpha(1.0f);
            binding.inventoryBtnTotal.setText(String.format(java.util.Locale.US, "TOTAL: $ %.2f", totalCarrito));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}