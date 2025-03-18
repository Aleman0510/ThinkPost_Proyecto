// CreatePostActivity.java
package com.example.thinkpost;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CreatePostActivity extends AppCompatActivity {

    private EditText etPostContent;
    private Button btnPublicar;
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

        btnPublicar.setOnClickListener(v -> publicarContenido());
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

        // Obtener datos del usuario desde Firestore (incluyendo password de práctica)
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
                        // post.put("timestamp", new Date());
                        post.put("timestamp", FieldValue.serverTimestamp());

                        // Guardar en colección posts
                        // Dentro del addOnSuccessListener al guardar el post
                        db.collection("posts")
                                .add(post)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(CreatePostActivity.this, "Publicación exitosa", Toast.LENGTH_SHORT).show(); // Cambiar this por CreatePostActivity.this
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CreatePostActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Aquí también
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error obteniendo datos de usuario", Toast.LENGTH_SHORT).show();
                });
    }
}