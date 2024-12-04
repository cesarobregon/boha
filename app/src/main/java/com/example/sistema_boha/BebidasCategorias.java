package com.example.sistema_boha;

// se importa las librerías necesarias
import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistema_boha.conexion.conexion;
import com.example.sistema_boha.entidades.CategoriaAdapter;

import org.json.JSONArray;

public class BebidasCategorias extends AppCompatActivity {

    //barra de herramientas
    private Toolbar toolbar;
    String direccion = conexion.direccion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bebidas_categorias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //se inicializa la barra de herramientas del activity
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Categorias de Bebidas");
            // Cambiar el color del icono de retroceso
            @SuppressLint("UseCompatLoadingForDrawables") Drawable upArrow = getResources().getDrawable(com.google.android.material.R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.appColor), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        // Referenciar el RecyclerView desde el layout(listado de las bebidas)
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBebidas);

        // Configurar el RecyclerView con un LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Llamar a la función para listar productos
        listarCategorias();

    }
    @Override // metodo que devuelve a la activity anterior
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //metodo para listar las categorias de bebidas registradas en la base de datos
    public void listarCategorias() {
        // el RequestQueue es la cola dónde vamos a añadir nuestras conexiones
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // direccion del api php
        String url = "http://"+ direccion +"/conexionbd/ListarCategoriasBebidas.php";
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Inicializar el RecyclerView
                RecyclerView recyclerView = findViewById(R.id.recyclerViewBebidas);
                recyclerView.setLayoutManager(new LinearLayoutManager(BebidasCategorias.this));
                // Crear el adaptador y asignarlo al RecyclerView
                CategoriaAdapter adapter = new CategoriaAdapter(BebidasCategorias.this, response);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar error
                error.printStackTrace();
            }
        });
        requestQueue.add(jar);
    }


}