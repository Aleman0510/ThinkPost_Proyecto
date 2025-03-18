package com.example.thinkpost;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etUsername, etDescription;
    private Button btnActualizar, btnCancelar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        etUsername = findViewById(R.id.etUsername);
        etDescription = findViewById(R.id.etDescription);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Cargar los datos actuales del usuario
        loadUserData();

        // Botón para actualizar perfil
        btnActualizar.setOnClickListener(v -> updateUserProfile());

        // Botón para cancelar y volver a ProfileActivity
        btnCancelar.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    String descripcion = documentSnapshot.getString("descripcion"); // Cambio aquí

                    etUsername.setText(username);
                    etDescription.setText(descripcion);
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(EditProfileActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void updateUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String newUsername = etUsername.getText().toString().trim();
            String newDescripcion = etDescription.getText().toString().trim(); // Cambio aquí

            if (newUsername.isEmpty() || newDescripcion.isEmpty()) {
                Toast.makeText(this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Usar los nombres de los campos correctamente
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", newUsername);
            userData.put("descripcion", newDescripcion); // Cambio aquí

            // Usar set() con merge para evitar problemas
            db.collection("users").document(userId)
                    .set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(EditProfileActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    );
        }
    }
}
