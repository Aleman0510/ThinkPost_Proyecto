package com.example.thinkpost;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth; // Añade esta línea

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); // Inicializa Firebase Auth

        // Elimina las llamadas antiguas a crearUsuarioDePrueba() y crearPostDePrueba()
        // Y reemplaza con este nuevo método mejorado
        crearUsuarioYPostDePrueba();
    }

    private void crearUsuarioYPostDePrueba() {
        String email = "test@example.com";
        String password = "password123";

        // Primero crea el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();

                            // Ahora crea el documento en Firestore usando el UID de Auth
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", "usuario_test");
                            userData.put("email", email);

                            db.collection("users").document(uid)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Usuario en Firestore creado");

                                        // Ahora crea el post relacionado usando el UID real
                                        crearPostDePrueba(uid);
                                    })
                                    .addOnFailureListener(e -> Log.w(TAG, "Error en Firestore", e));
                        }
                    } else {
                        Log.w(TAG, "Error en Auth", task.getException());
                    }
                });
    }

    // Modifica este método para recibir el UID real
    private void crearPostDePrueba(String userId) {
        Map<String, Object> post = new HashMap<>();
        post.put("content", "Este es un post de prueba");
        post.put("userId", userId); // Usamos el UID real de Auth
        post.put("username", "usuario_test");
        post.put("timestamp", FieldValue.serverTimestamp()); // Añade esto también

        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Post creado. ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error creando post", e);
                });
    }
}