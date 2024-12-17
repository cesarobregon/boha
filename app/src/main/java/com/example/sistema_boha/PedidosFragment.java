package com.example.sistema_boha;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistema_boha.conexion.conexion;
import com.example.sistema_boha.entidades.Pedido;
import com.example.sistema_boha.entidades.PedidoAdapter;
import com.example.sistema_boha.entidades.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PedidosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PedidosFragment extends Fragment {

    //id del cliente
    int id;

    //direccion ip
    String direccion = conexion.direccion;
    private Handler handler = new Handler();
    private Runnable runnable;

    //lista de productos en el carrito
    public List<Producto> productosEnCarrito;
    PedidoAdapter pedidoAdapter;

    //se crea una instancia de pedido con los atributos vacios para ir asignandole valores
    Pedido pedido = new Pedido();
    List<Pedido> pedidos = new ArrayList<>();
    String estadoRecibido = "PENDIENTE";

    //Botones y texto de la vista del pedido
    private Button btnVerCarro, btnConfirmar, btnCancelar;
    TextView infoPedido, txtUltimosPedidos;

    public PedidosFragment() {
        // Required empty public constructorz
    }

    public static PedidosFragment newInstance(String param1, String param2) {
        PedidosFragment fragment = new PedidosFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!pedidos.isEmpty()){
            iniciarRecorridoEstados(pedidos);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Detener el Runnable
    }
    // Detener el ciclo en onPause o onDestroyView
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable); // Detener el Runnable al destruir la vista
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);

        // Obtener los productos en el carrito como lista de Producto
        productosEnCarrito = CarritoManager.getInstance().obtenerCarrito();


        // Inicializar vistas
        btnVerCarro = view.findViewById(R.id.btnVerCarro);
        btnConfirmar = view.findViewById(R.id.btnConfirmarPedido);
        btnCancelar = view.findViewById(R.id.btnCancelarPedido);
        infoPedido = view.findViewById(R.id.txtInfoPedido);
        txtUltimosPedidos = view.findViewById(R.id.txtUltimosPedidos);


        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarPedido();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                CarritoManager.getInstance().vaciarCarrito();
                infoPedido.setText("Aún no tienes pedidos. ¡Haz uno ahora!");
                Toast.makeText(getActivity(), "Pedido Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        btnVerCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código para abrir el carrito, por ejemplo, iniciar una nueva actividad
                Intent intent = new Intent(getActivity(), CarritoActivity.class);
                startActivity(intent);
            }
        });

        cargarDatosPedido();//cargar datos del pedido actual
        cargarDatosPedidos();//buscar pedidos realizados en la base de datos

        return view;
    }

    public void mostrarNotificacion(String estado) {
        String channelId = "mi_canal_id";
        String mensaje = "";

        // Crear el canal de notificación (solo necesario en Android 8.0 y superior)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence channelName = "Mi Canal";
            String channelDescription = "Descripción de mi canal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (Objects.equals(estado, "EN PROCESO")){
            mensaje = "Su Pedido ya esta en proceso";
        }else if (Objects.equals(estado, "LISTO")){
            mensaje = "Su Pedido ya esta listo";
        } else if (Objects.equals(estado, "EN CAMINO")) {
            mensaje = "Su Pedido ya esta en camino";
        }else {
            mensaje = "Estado de su Pedido: " + estado;
        }

        // Crear y mostrar la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.boha_inicio) // Reemplaza con tu ícono
                .setContentTitle("Estado de su Pedido")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build()); // ID único para la notificación
        }
    }

    @SuppressLint("SetTextI18n")
    private void cargarDatosPedido() {
        if (productosEnCarrito.isEmpty()) {
            infoPedido.setText("Aún no tienes pedidos. ¡Haz uno ahora!");
        } else {
            // Contar la cantidad de productos y calcular el total de precios
            int cantidadProductos = productosEnCarrito.size();
            double totalPrecio = 0;

            for (Producto producto : productosEnCarrito) {
                // Convertir el precio de String a double
                try {
                    double precio = Double.parseDouble(producto.getPrecio()); // Asumiendo que getPrecio() devuelve un String
                    totalPrecio += precio;
                } catch (NumberFormatException e) {
                    // Manejo del error si el formato del precio no es válido
                    e.printStackTrace();
                }
            }

            // Formatear el texto con la cantidad de productos y el precio total
            String mensaje = "Tienes " + cantidadProductos + " producto(s) en tu carrito. \nTotal: $" + String.format("%.2f", totalPrecio);
            infoPedido.setText(mensaje);
        }
    }

    private void cargarDatosPedidos() {
        // Obtener SharedPreferences utilizando el contexto del Fragment
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);

        // Obtener el ID almacenado en el dispositivo
        int idCliente = preferences.getInt("id_cliente", 0);

        // Obtener los datos de los pedidos para ese cliente
        obtenerPedidosPorId(idCliente);
    }

    private void obtenerPedidosPorId(int idCliente) {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        String url = "http://" + direccion + "/conexionbd/buscarPedidosCliente.php?id_cliente=" + idCliente;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject pedidoJson = response.getJSONObject(i);

                                // Datos del pedido
                                String uuidPedido = pedidoJson.getString("uuid_pedido");

                                String fecha = pedidoJson.getString("fecha");
                                String estado = pedidoJson.getString("estado");
                                String hora = pedidoJson.getString("hora");
                                String tipoEntrega = pedidoJson.getString("tipo_entrega");
                                String montoTotal = pedidoJson.getString("monto_total");

                                // Lista de productos del pedido
                                JSONArray productosJson = pedidoJson.getJSONArray("productos");
                                List<Producto> productos = new ArrayList<>();

                                for (int j = 0; j < productosJson.length(); j++) {
                                    JSONObject productoJson = productosJson.getJSONObject(j);

                                    // Crear el objeto Producto utilizando su constructor basado en JSONObject
                                    Producto producto = new Producto(productoJson);
                                    producto.setCantidad(productoJson.optInt("cantidad", 1)); // Asegurar que se establezca la cantidad
                                    productos.add(producto);
                                }
                                // Crear el objeto Pedido con su lista de productos
                                Pedido pedido = new Pedido();
                                pedido.setUuidPedido(uuidPedido);
                                pedido.setFechaPedido(fecha);
                                pedido.setEstadoPedido(estado);
                                pedido.setHora(hora);
                                pedido.setTipoEntrega(tipoEntrega);
                                pedido.setMontoTotal(montoTotal);
                                pedido.setProductos(productos);
                                pedidos.add(pedido);
                            }
                            if (!pedidos.isEmpty()){
                                txtUltimosPedidos.setVisibility(View.VISIBLE);
                            }
                            // Actualizar el RecyclerView con los pedidos obtenidos
                            configurarRecyclerView(pedidos);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error al procesar la respuesta JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error al conectarse a la Base de Datos", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onErrorResponse: " + error.getMessage());
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void confirmarPedido() {
        // Preparar los valores necesarios para el pedido
        String uuidPedido = UUID.randomUUID().toString();
        String fechaPedido = obtenerFechaActual();
        String horaPedido = obtenerHoraActual();
        String monto = calcularTotalCarrito();
        String estado = "PENDIENTE";
        int modificado = 0;

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int idCliente = preferences.getInt("id_cliente", 0);

        pedido = new Pedido();
        pedido.setUuidPedido(uuidPedido);
        pedido.setId_cliente(idCliente);
        pedido.setFechaPedido(fechaPedido);
        pedido.setHora(horaPedido);
        pedido.setMontoTotal(monto);
        pedido.setEstadoPedido(estado);
        pedido.setModificado(modificado);

        // Iniciar el nuevo Activity enviando el objeto Pedido
        Intent intent = new Intent(getActivity(), PagoActivity.class);
        intent.putExtra("pedido", pedido); // Enviar el objeto Pedido
        startActivity(intent);
    }

    private void configurarRecyclerView(List<Pedido> pedidos) {
        pedidoAdapter = new PedidoAdapter(pedidos);
        RecyclerView recyclerPedidos = requireView().findViewById(R.id.recyclerPedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerPedidos.setAdapter(pedidoAdapter);
    }


    // Método para calcular el monto total del carrito
    private String calcularTotalCarrito() {
        double total = 0;
        for (Producto producto : productosEnCarrito) {
            total += Double.parseDouble(producto.getPrecio());
        }
        return String.valueOf(total);
    }

    // Método para obtener solo la fecha actual en el formato yyyy-MM-dd
    private String obtenerFechaActual() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdfFecha.format(new Date());
    }

    // Método para obtener solo la hora actual en el formato HH:mm:ss
    private String obtenerHoraActual() {
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdfHora.format(new Date());
    }

    public void verificarEstadoPedido(String uuidPedido , String estadoActual) {
        // URL de tu API

        String url = "http://" + direccion + "/conexionbd/obtenerEstadoPedido.php?uuid_pedido=" + uuidPedido;

        // Crear el Response Listener para manejar la respuesta exitosa
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Convertir la respuesta a un objeto JSON
                    JSONObject jsonObject = new JSONObject(response);

                    // Verificar si tiene el campo "estado"
                    if (jsonObject.has("estado")) {
                        estadoRecibido = jsonObject.getString("estado");
                        if (!Objects.equals(estadoActual, estadoRecibido)){
                            mostrarNotificacion(estadoRecibido);
                            pedidoAdapter.clearItems();
                            cargarDatosPedidos();

                        }
                    } else if (jsonObject.has("mensaje")) {
                        Log.e("Error al Obtener el Estado Del Pedido", jsonObject.getString("mensaje"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        // Crear una solicitud de tipo StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);

        // Agregar la solicitud a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    // Método para iniciar el ciclo de recorrido del listado de pedidos
    private void iniciarRecorridoEstados(List<Pedido> pedidos) {
        runnable = new Runnable() {
            @Override
            public void run() {
                // Verificar si el fragmento está asociado
                if (!isAdded()) {
                    handler.removeCallbacks(runnable); // Detener el ciclo si el fragmento no está asociado
                    return;
                }
                // Recorrer los pedidos
                for (Pedido pedido : pedidos) {
                    String estadoActual = pedido.getEstadoPedido();
                    verificarEstadoPedido(pedido.getUuidPedido(), estadoActual);
                }
                // Programar el siguiente recorrido en 10 segundos
                handler.postDelayed(this, 10000);
            }
        };
        // Iniciar el ciclo
        handler.post(runnable);
    }
}