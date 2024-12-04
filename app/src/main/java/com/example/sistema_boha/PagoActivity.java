package com.example.sistema_boha;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistema_boha.conexion.conexion;
import com.example.sistema_boha.entidades.Pedido;
import com.example.sistema_boha.entidades.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PagoActivity extends AppCompatActivity {

    // barra de herramientas
    private Toolbar toolbar;
    String direccion = conexion.direccion;
    Intent intent;
    Pedido pedido;
    public List<Producto> productosEnCarrito;

    Button confirmarPedido;
    TextView txtInfoPedidos;
    String txtInfo = "";

    // Variables para almacenar los valores seleccionados en los RadioGroup
    private String metodoPagoSeleccionado = "";
    private String modoEntregaSeleccionado = "";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pago);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Obtener los productos en el carrito como lista de Producto
        productosEnCarrito = CarritoManager.getInstance().obtenerCarrito();
        txtInfoPedidos = findViewById(R.id.txtInfoPedido);
        confirmarPedido = findViewById(R.id.btnConfirmarPedido);
        txtproductos();

        // Inicializar barra de herramientas
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Métodos de Pago y Retiro del Pedido");
            // Cambiar el color del icono de retroceso
            @SuppressLint("UseCompatLoadingForDrawables") Drawable upArrow = getResources().getDrawable(com.google.android.material.R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.appColor), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        intent = getIntent();
        pedido = (Pedido) intent.getSerializableExtra("pedido");

        if (pedido == null) {
            Toast.makeText(this, "Error al Recibir los Datos del Pedido", Toast.LENGTH_SHORT).show();
        }

        // Configurar RadioGroups
        RadioGroup radioGroupMetodoPago = findViewById(R.id.radioGroupMetodoPago);
        RadioGroup radioGroupModoEntrega = findViewById(R.id.radioGroupModoEntrega);

        // Listener para el método de pago
        radioGroupMetodoPago.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            if (radioButton != null) {
                metodoPagoSeleccionado = radioButton.getText().toString();
            }
        });

        // Listener para el modo de entrega
        radioGroupModoEntrega.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            if (radioButton != null) {
                modoEntregaSeleccionado = radioButton.getText().toString();
            }
        });

        confirmarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarpedido();
            }
        });

    }
    private void txtproductos(){
        txtInfo = convertirProductos(productosEnCarrito) + "\nTotal: " + calcularTotalCarrito();
        txtInfoPedidos.setText(txtInfo);
    }
    // Método para convertir la lista de productos a un String JSON
    private String convertirProductos(List<Producto> productosEnCarrito) {
        String productos = " ";
        for (Producto producto : productosEnCarrito) {
            productos += producto.getNombre() + ", ";
        }
        return productos;
    }
    // Método para calcular el total del carrito
    private String calcularTotalCarrito() {
        double total = 0;
        for (Producto producto : productosEnCarrito) {
            total += Double.parseDouble(producto.getPrecio());
        }
        return String.valueOf(total);
    }

    private void confirmarpedido(){
        pedido.setTipoEntrega(modoEntregaSeleccionado);
        if (Objects.equals(metodoPagoSeleccionado, "Transferencia")){
            pedido.setId_metodo(3);
            pedido.setFechaPago(obtenerFechaActual());
            pedido.setEstadoPago("Pagado");
        }else {
            pedido.setId_metodo(4);
            pedido.setFechaPago(obtenerFechaActual());
            pedido.setEstadoPago("Pendiente");
        }
        pedido.setMontoTotal(calcularTotalCarrito());
        pedido.setProductos(productosEnCarrito);

        registrarPedido(pedido);

    }
    private void registrarPedido(Pedido pedido) {
        // URL base del API PHP donde se realiza la solicitud POST
        String urlBase = "http://" + direccion + "/conexionbd/RegistrarPedido.php";

        // Verificar que el objeto Pedido no sea nulo
        if (pedido == null) {
            Toast.makeText(this, "El pedido está vacío, no se puede registrar", Toast.LENGTH_SHORT).show();
            return;
        }

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
                                Toast.makeText(PagoActivity.this, "Pedido registrado exitosamente", Toast.LENGTH_SHORT).show();
                                CarritoManager.getInstance().vaciarCarrito();//vaciamos el carrito
                                finish();
                            } else {
                                Toast.makeText(PagoActivity.this, "Error al registrar el pedido", Toast.LENGTH_SHORT).show();
                                Log.e("Error al Registrar", jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PagoActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(PagoActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Crear el mapa de parámetros que se enviarán al servidor
                Map<String, String> params = new HashMap<>();
                params.put("uuid_pedido", pedido.getUuidPedido());
                params.put("id_cliente", String.valueOf(pedido.getId_cliente()));
                params.put("fecha", pedido.getFechaPedido());
                params.put("hora", pedido.getHora());
                params.put("tipo_entrega", pedido.getTipoEntrega());
                params.put("estado", "EN PROCESO");
                params.put("modificado", String.valueOf(pedido.getModificado()));
                params.put("id_pago", String.valueOf(pedido.getId_pago()));
                params.put("id_metodo", String.valueOf(pedido.getId_metodo()));
                params.put("monto", pedido.getMontoTotal());
                params.put("fecha_pago", pedido.getFechaPago());
                params.put("estado_pago", pedido.getEstadoPago());

                // Convertir productos a JSON
                JSONArray productosArray = new JSONArray();
                for (Producto producto : pedido.getProductos()) {
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

    @Override // metodo que devuelve a la activity anterior
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Método para obtener solo la fecha actual en el formato yyyy-MM-dd
    private String obtenerFechaActual() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdfFecha.format(new Date());
    }


}