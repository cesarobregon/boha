package com.example.sistema_boha;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class recuperarClave extends AppCompatActivity {
    EditText correoCliente;
    Button btncodigo;

    Random num = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar_clave);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        correoCliente = findViewById(R.id.txtRecuperarCorreo);
        btncodigo = findViewById(R.id.btnEnviarCod);

        btncodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int codigo = num.nextInt(100000);
                Toast.makeText(recuperarClave.this, "Codigo: " + codigo, Toast.LENGTH_SHORT).show();
            }
        });

    }



}