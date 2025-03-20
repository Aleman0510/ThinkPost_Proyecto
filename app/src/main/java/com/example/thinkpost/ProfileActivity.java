package com.example.thinkpost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvDescription;
    private Button btnRegresar, btnCerrarSesion, btnEditarPerfil;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileactivity); // Asegúrate de que el XML tiene este nombre

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionBottom);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil); // Nuevo botón

        // Cargar datos del usuario
        loadUserData();

        // Botón para regresar
        btnRegresar.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish(); // Cierra ProfileActivity
        });

        // Botón para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        // Botón para editar perfil
        btnEditarPerfil.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Obtener datos de Firestore
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    String descripcion = documentSnapshot.getString("descripcion");

                    tvUsername.setText(username != null ? username : "Usuario desconocido");
                    tvDescription.setText(descripcion != null ? descripcion : "Sin descripción");
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(ProfileActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
