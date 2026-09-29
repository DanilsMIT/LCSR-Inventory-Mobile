package com.example.lcsr_inventory;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.lcsr_inventory.databinding.ActivityMainBinding;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import android.Manifest;
import android.os.Build;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Pedir permiso para notificaciones en Android 13+ y suscribirse al tema
        pedirPermisosNotificacion();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content__fragment_container, new Inventory())
                    .commit();
        }

        binding.mainContentBtnMenu.setOnClickListener(v -> {
            binding.layoutDrawer.openDrawer(GravityCompat.START);
        });

        binding.navDrawerMenu.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu__item_inventory) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_content__fragment_container, new Inventory())
                        .commit();
            } else if (id == R.id.menu__item_sales) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_content__fragment_container, new ventas())
                        .commit();
            }

            binding.layoutDrawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void pedirPermisosNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                suscribirseAVentas();
            }
        } else {
            suscribirseAVentas();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                suscribirseAVentas();
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void suscribirseAVentas() {
        FirebaseMessaging.getInstance().subscribeToTopic("ventas_topic")
                .addOnCompleteListener(task -> {
                    // Si falla la suscripción lo mostramos en el log, pero no al usuario para no molestarlo
                    if (!task.isSuccessful()) {
                        android.util.Log.e("FCM", "Falló la suscripción al topic de ventas");
                    }
                });
    }
}