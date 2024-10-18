package com.example.sistema_boha;

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
import com.example.sistema_boha.entidades.ProductoAdapter;
import org.json.JSONArray;

public class Comidas extends AppCompatActivity {
    // Definir la barra de herramientas (toolbar)
    private Toolbar toolbar;

    // Dirección IP del servidor
    String direccion = conexion.direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comidas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar la barra de herramientas (toolbar) y configurarla como ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Configurar el ActionBar con un botón de retroceso y el título
        if (getSupportActionBar() != null) {
            // Habilitar el botón de retroceso en el ActionBar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Establecer el título de la actividad
            getSupportActionBar().setTitle("Comidas");
            // Cambiar el color del ícono de retroceso
            Drawable upArrow = getResources().getDrawable(com.google.android.material.R.drawable.abc_ic_ab_back_material);
            // Asignar el ícono personalizado al ActionBar
            upArrow.setColorFilter(getResources().getColor(R.color.appColor), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        // Referenciar el RecyclerView desde el layout para mostrar la lista de comidas
        RecyclerView recyclerView = findViewById(R.id.recyclerViewComidas);

        // Configurar el RecyclerView para usar un LinearLayoutManager,
        // que organiza los elementos en una lista vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Llamar a la función para listar los productos (comidas) y actualizar el RecyclerView
        listarProductos();
    }


    @Override // Vuelve a la activity anterior
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Método para listar los productos (comidas) desde el servidor
    public void listarProductos() {
        // Crear una nueva cola de solicitudes para gestionar peticiones de red usando Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // URL donde se realiza la petición GET para obtener la lista de comidas
        String url = "http://"+direccion+"/conexionbd/ListarComidas.php";

        // Crear una solicitud para obtener un array de datos en formato JSON desde el servidor
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() { // Listener que maneja la respuesta exitosa
            @Override
            public void onResponse(JSONArray response) {
                // Inicializar el RecyclerView para mostrar la lista de productos
                RecyclerView recyclerView = findViewById(R.id.recyclerViewComidas);
                // Configurar el RecyclerView con un LayoutManager (en este caso, LinearLayoutManager)
                recyclerView.setLayoutManager(new LinearLayoutManager(Comidas.this));
                // Crear un adaptador (ProductoAdapter) que tomará los datos de respuesta
                // (JSON) y los mostrará en el RecyclerView
                ProductoAdapter adapter = new ProductoAdapter(Comidas.this, response);
                // Asignar el adaptador al RecyclerView para que se muestren los productos en la interfaz
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() { // Listener que maneja errores en la petición
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar error
                error.printStackTrace();
            }
        });
        // Añadir la solicitud a la cola de solicitudes (RequestQueue) para que se ejecute
        requestQueue.add(jar);
    }
}