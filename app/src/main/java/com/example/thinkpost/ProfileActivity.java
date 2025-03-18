package com.example.thinkpost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileactivity); // Asegúrate de que exista este layout

        // Obtener el botón del layout
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Configurar el evento de clic
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Cierra ProfileActivity para no volver atrás con el botón de retroceso
            }
        });
    }
}
