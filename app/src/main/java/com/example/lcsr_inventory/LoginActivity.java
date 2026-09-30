package com.example.lcsr_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lcsr_inventory.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 1. Verificación de Sesión Persistente (Como un Token JWT)
        // Si el usuario ya se logueó antes y no ha cerrado sesión, pasamos directo a la MainActivity
        if (mAuth.getCurrentUser() != null) {
            irAInventario();
        }

        // 2. Click para ir a Pantalla de Registro
        binding.loginTvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // 3. Click para Iniciar Sesión con Email
        binding.loginBtnLogin.setOnClickListener(v -> {
            String email = binding.loginInputEmail.getText().toString().trim();
            String password = binding.loginInputPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tus credenciales", Toast.LENGTH_SHORT).show();
                return;
            }

            mostrarCarga(true);
            
            // Magia de Firebase: Petición a la base de datos para autenticar
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        mostrarCarga(false);
                        if (task.isSuccessful()) {
                            // Ingreso Exitoso
                            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                            irAInventario();
                        } else {
                            // Ingreso Fallido (Contraseña mala, correo no existe, etc)
                            Toast.makeText(this, "Error de autenticación. Verifica tus datos.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

    private void irAInventario() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finalizamos el Login para que no puedan volver atrás con la flecha
    }

    private void mostrarCarga(boolean cargando) {
        if (cargando) {
            binding.loginProgress.setVisibility(View.VISIBLE);
            binding.loginBtnLogin.setEnabled(false);
        } else {
            binding.loginProgress.setVisibility(View.GONE);
            binding.loginBtnLogin.setEnabled(true);
        }
    }
}
