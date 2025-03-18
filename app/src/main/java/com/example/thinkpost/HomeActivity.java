package com.example.thinkpost;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fabPublicar;
    private RecyclerView recyclerPosts;
    private PostsAdapter adapter;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        fabPublicar = findViewById(R.id.fabPublicar);
        recyclerPosts = findViewById(R.id.recyclerPosts);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        db = FirebaseFirestore.getInstance();

        // Configurar RecyclerView
        recyclerPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostsAdapter(new ArrayList<>());
        recyclerPosts.setAdapter(adapter);

        loadPosts();

        fabPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreatePostActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            }
        });

        // Configurar navegación inferior
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_profile) {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadPosts() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> posts = task.getResult().getDocuments();
                        adapter.updateData(posts);
                    } else {
                        Log.w("Firestore", "Error getting posts", task.getException());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts(); // Recargar posts cuando la actividad se reanuda
    }
}