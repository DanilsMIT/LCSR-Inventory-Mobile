package com.example.lcsr_inventory;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
public class InventoryViewModel extends ViewModel {

    private final MutableLiveData<List<Producto>> mProductos = new MutableLiveData<>();
    private final DatabaseReference productosRef;

    public InventoryViewModel() {
        productosRef = FirebaseDatabase.getInstance().getReference("productos");
        escucharProductosEnTiempoReal();
    }

    public LiveData<List<Producto>> getProductos() {
        return mProductos;
    }

    // Petición GET
    private void escucharProductosEnTiempoReal() {
        productosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Producto> listaTemporal = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Producto producto = item.getValue(Producto.class);
                    if (producto != null) {
                        producto.setId(item.getKey());
                        listaTemporal.add(producto);
                    }
                }

                Collections.sort(listaTemporal, (p1, p2) ->
                        p1.getName().compareToIgnoreCase(p2.getName())
                );

                mProductos.setValue(listaTemporal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // POST: Agregar un nuevo producto
    public void agregarProducto(String nombre, double precio) {
        String nuevoId = productosRef.push().getKey();
        if (nuevoId != null) {
            Producto nuevoProducto = new Producto(nuevoId, nombre, precio);
            productosRef.child(nuevoId).setValue(nuevoProducto);
        }
    }

    // Patch
    public void editarProducto(String id, String nuevoNombre, double nuevoPrecio) {
        Map<String, Object> actualizaciones = new HashMap<>();
        actualizaciones.put("name", nuevoNombre);
        actualizaciones.put("price", nuevoPrecio);

        productosRef.child(id).updateChildren(actualizaciones);
    }
    // DELETE: Borrar producto de la base de datos
    public void eliminarProducto(String id) {
        productosRef.child(id).removeValue();
    }
}