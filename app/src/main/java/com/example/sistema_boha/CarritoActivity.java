package com.example.sistema_boha;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.toolbox.Volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sistema_boha.conexion.conexion;
import com.example.sistema_boha.entidades.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarritoActivity extends AppCompatActivity {

    // Definir la barra de herramientas (toolbar)
    private Toolbar toolbar;
    String direccion = conexion.direccion;
    public List<Producto> productosEnCarrito;
    TextView txtAgregar;
    Button btncomidas, btnbebidas, btnConfirmarEditar;
    CarritoAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carrito);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        RecyclerView recyclerView = findViewById(R.id.recyclerViewCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtAgregar = findViewById(R.id.txtCarritoAgregarProducto);
        btncomidas = findViewById(R.id.btnCarritoComidas);
        btnbebidas = findViewById(R.id.btnCarritoBebidas);
        btnConfirmarEditar = findViewById(R.id.btnCarritoConfirmarEdit);

        // Obtener los productos en el carrito como lista de Producto
        productosEnCarrito = CarritoManager.getInstance().obtenerCarrito();

        if(!productosEnCarrito.isEmpty()){
            txtAgregar.setVisibility(View.VISIBLE);
            btncomidas.setVisibility(View.VISIBLE);
            btnbebidas.setVisibility(View.VISIBLE);
        }

        // Crear el adaptador con la lista de productos
        adapter = new CarritoAdapter(this, productosEnCarrito);
        recyclerView.setAdapter(adapter);

        btncomidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, ComidasCategorias.class);
                startActivity(intent);
            }
        });

        btnbebidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, BebidasCategorias.class);
                startActivity(intent);
            }
        });

        // Obtener el valor extra del Intent
        int modoEdicion = getIntent().getIntExtra("editar", 0);
        String uuidPedido = getIntent().getStringExtra("uuid_pedido");

        if (modoEdicion == 1) {
            // Lógica específica para el modo de edición
            Toast.makeText(this, "Modificar Pedido", Toast.LENGTH_SHORT).show();
            btnConfirmarEditar.setVisibility(View.VISIBLE);
        }

        btnConfirmarEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarPedidoEditado(uuidPedido, productosEnCarrito);
            }
        });

        // Inicializar la barra de herramientas y configurarla como ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Configurar el ActionBar con un botón de retroceso y el título
        if (getSupportActionBar() != null) {
            // Habilitar el botón de retroceso en el ActionBar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Establecer el título de la actividad
            getSupportActionBar().setTitle("Carrito");
            // Cambiar el color del ícono de retroceso
            @SuppressLint("UseCompatLoadingForDrawables") Drawable upArrow = getResources().getDrawable(com.google.android.material.R.drawable.abc_ic_ab_back_material);
            // Asignar el ícono personalizado al ActionBar
            upArrow.setColorFilter(getResources().getColor(R.color.appColor), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar el carrito cuando el Activity se vuelve visible
        actualizarCarrito();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void actualizarCarrito() {
        productosEnCarrito = CarritoManager.getInstance().obtenerCarrito();
        adapter.setProductos(productosEnCarrito);
        adapter.notifyDataSetChanged();
    }


    @Override // Vuelve a la activity anterior
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enviarPedidoEditado(String uuidPedido, List<Producto> productos) {
        // URL base del API PHP donde se realiza la solicitud POST
        String urlBase = "http://" + direccion + "/conexionbd/EditarPedido.php";

        // Crear un StringRequest para realizar una solicitud POST al servidor
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlBase,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Convertir la respuesta en un objeto JSON
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(CarritoActivity.this, "Pedido editado exitosamente", Toast.LENGTH_SHORT).show();
                                CarritoManager.getInstance().vaciarCarrito();
                                finish();
                            } else {
                                String message = jsonObject.has("message") ? jsonObject.getString("message") : "Error desconocido";
                                Toast.makeText(CarritoActivity.this, "Error al editar el pedido: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CarritoActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(CarritoActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Crear el mapa de parámetros que se enviarán al servidor
                Map<String, String> params = new HashMap<>();
                params.put("uuid_pedido", uuidPedido);

                // Convertir productos a JSON
                JSONArray productosArray = new JSONArray();
                for (Producto producto : productos) {
                    JSONObject productoObj = new JSONObject();
                    try {
                        productoObj.put("id_producto", producto.getId());
                        productoObj.put("cantidad", producto.getCantidad());
                        productoObj.put("precio", producto.getPrecio());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    productosArray.put(productoObj);
                }
                params.put("productos", productosArray.toString());
                return params;
            }
        };

        // Añadir la solicitud a la RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}