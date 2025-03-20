// CreatePostActivity.java
package com.example.thinkpost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etPostContent;
    private Button btnPublicar, btnCancelar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_publicacion);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etPostContent = findViewById(R.id.etPost);
        btnPublicar = findViewById(R.id.btnPublicar);
        btnCancelar = findViewById(R.id.btnCancelar); // Referencia al botón Cancelar

        btnPublicar.setOnClickListener(v -> publicarContenido());

        // Configurar el botón Cancelar para redirigir a HomeActivity
        btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(CreatePostActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        });
    }

    private void publicarContenido() {
        String contenido = etPostContent.getText().toString().trim();

        if (contenido.isEmpty()) {
            Toast.makeText(this, "El contenido no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener datos del usuario desde Firestore
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");

                        // Crear objeto post
                        Map<String, Object> post = new HashMap<>();
                        post.put("content", contenido);
                        post.put("userId", mAuth.getCurrentUser().getUid());
                        post.put("username", username);
                        post.put("timestamp", FieldValue.serverTimestamp());

                        // Guardar en la colección posts
                        db.collection("posts")
                                .add(post)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(CreatePostActivity.this, "Publicación exitosa", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CreatePostActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error obteniendo datos de usuario", Toast.LENGTH_SHORT).show();
                });
    }
}
