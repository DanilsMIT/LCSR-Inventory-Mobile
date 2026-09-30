package com.example.lcsr_inventory;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.lcsr_inventory.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
            } else if (id == R.id.menu__item_logout) {
                // Cerramos sesión en Firebase y volvemos a la pantalla de Login
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                android.widget.Toast.makeText(this, "Sesión cerrada", android.widget.Toast.LENGTH_SHORT).show();
                
                android.content.Intent intent = new android.content.Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            binding.layoutDrawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}