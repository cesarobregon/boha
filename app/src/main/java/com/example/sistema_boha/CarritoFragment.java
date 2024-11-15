package com.example.sistema_boha;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistema_boha.conexion.conexion;
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
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarritoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarritoFragment extends Fragment {
    int id;
    String direccion = conexion.direccion;

    public List<Producto> productosEnCarrito;

    private Button btnVerCarro, btnConfirmar, btnCancelar;
    TextView infoPedido;

    public CarritoFragment() {
        // Required empty public constructorz
    }

    public static CarritoFragment newInstance(String param1, String param2) {
        CarritoFragment fragment = new CarritoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrito, container, false);

        // Obtener los productos en el carrito como lista de Producto
        productosEnCarrito = CarritoManager.getInstance().obtenerCarrito();


        // Inicializar vistas
        btnVerCarro = view.findViewById(R.id.btnVerCarro);
        btnConfirmar = view.findViewById(R.id.btnConfirmarPedido);
        btnCancelar = view.findViewById(R.id.btnCancelarPedido);
        infoPedido = view.findViewById(R.id.txtInfoPedido);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarPedido();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarritoManager.getInstance().vaciarCarrito();
                infoPedido.setText("");
                Toast.makeText(getActivity(), "Pedido Cancelado", Toast.LENGTH_SHORT).show();
            }
        });

        btnVerCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código para abrir el carrito, por ejemplo, iniciar una nueva actividad
                Intent intent = new Intent(getActivity(), CarroCompras.class);
                startActivity(intent);
            }
        });


        cargarDatosPedido();

        return view;
    }

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

    // Función para registrar el pedido
    private void registrarPedido() {
        // URL base del API PHP donde se realiza la solicitud GET
        String urlBase = "http://" + direccion + "/conexionbd/RegistrarPedido.php";

        // Preparar los valores necesarios para el pedido
        String uuidPedido = UUID.randomUUID().toString(); // Genera el UUID para el pedido
        String fecha = obtenerFechaActual(); // Obtener solo la fecha actual
        String hora = obtenerHoraActual(); // Obtener solo la hora actual
        String tipoEntrega = "delivery"; // Tipo de entrega
        String productos = convertirProductos(productosEnCarrito); // Convierte lista de productos a JSON
        String total = calcularTotalCarrito(); // Calcula el total del carrito como String

        // Verificamos que haya productos en el carrito y el total no sea vacío
        if (productos.isEmpty() || total.isEmpty()) {
            Toast.makeText(getActivity(), "El carrito está vacío o hay un problema con los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener SharedPreferences para id_cliente
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int idCliente = preferences.getInt("id_cliente", 0);

        // Construir la URL con los parámetros en la cadena de consulta
        String url = urlBase + "?uuid_pedido=" + uuidPedido
                + "&id_cliente=" + idCliente
                + "&fecha=" + fecha
                + "&hora=" + hora
                + "&tipo_entrega=" + tipoEntrega
                + "&productos=" + productos
                + "&total=" + total;

        // Crear un StringRequest para realizar una solicitud GET al servidor
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Convertir la respuesta en un objeto JSON
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Toast.makeText(getActivity(), "Pedido registrado exitosamente", Toast.LENGTH_SHORT).show();
                        // Opcionalmente, reiniciar o vaciar el carrito
                        CarritoManager.getInstance().vaciarCarrito();
                        infoPedido.setText("Aún no tienes pedidos. ¡Haz uno ahora!");
                    } else {
                        Toast.makeText(getActivity(), "Error al registrar el pedido", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        // Añadir la solicitud a la RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }




//    // Función para registrar el pedido
//    private void registrarPedido() {
//        // URL del api php donde se realiza la solicitud POST
//        String url = "http://" + direccion + "/conexionbd/RegistrarPedido.php";
//
//        // Preparar los valores necesarios para el pedido
//        String uuidPedido = UUID.randomUUID().toString(); // Genera el UUID para el pedido
//        String fecha = obtenerFechaActual(); // Asume que tienes una función para obtener solo la fecha actual
//        String hora = obtenerHoraActual(); // Asume que tienes una función para obtener solo la hora actual
//        String tipoEntrega = "delivery"; // Tipo de entrega
//        String productos = convertirProductos(productosEnCarrito); // Convierte lista de productos a JSON
//        String total = calcularTotalCarrito(); // Calcula el total del carrito como String
//
//        // Verificamos que haya productos en el carrito y el total no sea vacío
//        if (productos.isEmpty() || total.isEmpty()) {
//            Toast.makeText(getActivity(), "El carrito está vacío o hay un problema con los datos", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Crear un StringRequest para realizar una solicitud POST al servidor
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    // Convertir la respuesta en un objeto JSON
//                    JSONObject jsonObject = new JSONObject(response);
//                    boolean success = jsonObject.getBoolean("success");
//                    if (success) {
//                        Toast.makeText(getActivity(), "Pedido registrado exitosamente", Toast.LENGTH_SHORT).show();
//                        // Opcionalmente, reiniciar o vaciar el carrito
//                        //vaciarCarrito();
//                    } else {
//                        Toast.makeText(getActivity(), "Error al registrar el pedido", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                Toast.makeText(getActivity(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // Obtener SharedPreferences para id_cliente
//                SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
//                int idCliente = preferences.getInt("id_cliente", 0);
//
//                // Mapa de parámetros que se enviarán al servidor
//                Map<String, String> params = new HashMap<>();
//                params.put("uuid_pedido", uuidPedido);
//                params.put("id_cliente", String.valueOf(idCliente));
//                params.put("fecha", fecha);
//                params.put("hora", hora);
//                params.put("tipo_entrega", tipoEntrega);
//
//                Log.d("Params", "uuid_pedido: " + uuidPedido);
//                Log.d("Params", "id_cliente: " + String.valueOf(idCliente));
//                Log.d("Params", "fecha: " + fecha);
//                Log.d("Params", "hora: " + hora);
//                Log.d("Params", "tipo_entrega: " + tipoEntrega);
//
//                return params;
//            }
//        };
//
//        // Añadir la solicitud a la RequestQueue
//        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
//        requestQueue.add(stringRequest);
//    }

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
}