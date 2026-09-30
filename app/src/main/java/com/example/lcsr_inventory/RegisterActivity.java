package com.example.lcsr_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lcsr_inventory.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // 1. Click para regresar al Login
        binding.registerTvLogin.setOnClickListener(v -> {
            finish(); // Cierra esta pantalla y vuelve a la de Login
        });

        // 2. Click para Registrarse
        binding.registerBtnRegister.setOnClickListener(v -> {
            String email = binding.registerInputEmail.getText().toString().trim();
            String password = binding.registerInputPassword.getText().toString().trim();
            String confirmPassword = binding.registerInputPasswordConfirm.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            mostrarCarga(true);

            // Magia de Firebase: Petición para crear usuario
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        mostrarCarga(false);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show();
                            // Como la cuenta se creó, Firebase inicia sesión automáticamente.
                            // Redirigimos directo al inventario.
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Error al crear cuenta: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void mostrarCarga(boolean cargando) {
        if (cargando) {
            binding.registerProgress.setVisibility(View.VISIBLE);
            binding.registerBtnRegister.setEnabled(false);
        } else {
            binding.registerProgress.setVisibility(View.GONE);
            binding.registerBtnRegister.setEnabled(true);
        }
    }
}
