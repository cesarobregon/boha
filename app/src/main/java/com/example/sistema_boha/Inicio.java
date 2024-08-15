package com.example.sistema_boha;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Inicio extends AppCompatActivity {
    Button inicio, carrito, perfil;
    ImageButton comidas, viandas, promos, bebidas, reservas;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicio = findViewById(R.id.btnInicio_);
        carrito = findViewById(R.id.btnCarrito);
        perfil = findViewById(R.id.btnPerfil);
        comidas = findViewById(R.id.btnComidas);
        viandas = findViewById(R.id.btnViandas);
        promos = findViewById(R.id.btnPromos);
        bebidas = findViewById(R.id.btnBebidas);
        reservas = findViewById(R.id.btnReservas);

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicio();
            }
        });
        carrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carrito();
            }
        });
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perfil();
            }
        });
        comidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comidas();
            }
        });
        viandas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viandas();
            }
        });
        promos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promos();
            }
        });
        bebidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bebidas();
            }
        });
        reservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservas();
            }
        });

    }
    private void perfil(){
        Intent intent = new Intent(Inicio.this, perfilUsuario.class);
        startActivity(intent);
    }
    private void carrito(){
        Intent intent = new Intent(Inicio.this, Carrito.class);
        startActivity(intent);
    }
    private void inicio(){
        Intent intent = new Intent(Inicio.this, Inicio.class);
        startActivity(intent);
    }
    private void comidas(){
        Intent intent = new Intent(Inicio.this, Comidas.class);
        startActivity(intent);
    }
    private void viandas(){
        Intent intent = new Intent(Inicio.this, Viandas.class);
        startActivity(intent);
    }
    private void promos(){
        Intent intent = new Intent(Inicio.this, Promociones.class);
        startActivity(intent);
    }
    private void bebidas(){
        Intent intent = new Intent(Inicio.this, Bebidas.class);
        startActivity(intent);
    }
    private void reservas(){
        Intent intent = new Intent(Inicio.this, Reservas.class);
        startActivity(intent);
    }


}