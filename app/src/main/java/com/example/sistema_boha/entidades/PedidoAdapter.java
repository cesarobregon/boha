package com.example.sistema_boha.entidades;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import com.example.sistema_boha.CarritoManager;
import com.example.sistema_boha.CarritoActivity;
import com.example.sistema_boha.R;
import com.example.sistema_boha.conexion.conexion;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Pedido> pedidos;
    public List<Producto> productosEnCarrito;
    String estadoPedido = "PENDIENTE";

    String direccion = conexion.direccion;
    Intent intent;
    Context context;


    // Constructor del adaptador
    public PedidoAdapter(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        // Obtener los productos en el carrito como lista de Producto
        productosEnCarrito = CarritoManager.getInstance().obtenerCarrito();

        // Configurar datos del pedido
        holder.txtFechaPedido.setText("Fecha: " + pedido.getFechaPedido());
        holder.txtMontoTotal.setText("Monto: $" + pedido.getMontoTotal());

        //si el tipo de entrega es distinto a "consumir en el local", se cambia el texto y color del boton
        if(!Objects.equals(pedido.getTipoEntrega(), "Consumir en el Local")){
            holder.btnAccionPedido.setText("Cancelar");
            // Cambiar el color de fondo del botón directamente
            holder.btnAccionPedido.setBackgroundColor(Color.parseColor("#9F0707"));
        }

        // Manejar clic en "Ver productos"
        holder.btnVerCarrito.setOnClickListener(v -> {
            showProductsDialog(holder.itemView.getContext(), pedido);
        });

        // Manejar clic en "Editar o Cancelar pedido"
        holder.btnAccionPedido.setOnClickListener(v -> {
            if ("Consumir en el Local".equals(pedido.getTipoEntrega())) { //si el tipo de entrega es "consumir en el local", se permite editar
                if(!Objects.equals(pedido.getEstadoPedido(), "ENTREGADO")){  //si el estado del pedido es distinto que "Entregado", se permite editar
                    //primero se vacia el carrito actual
                    CarritoManager.getInstance().vaciarCarrito();

                    //almacenamos los productos del carrito que queremos modificar
                    productosEnCarrito = pedido.getProductos();

                    // Actualizar el carrito global con los productos del pedido
                    CarritoManager.getInstance().setCarrito(productosEnCarrito);

                    // Crear un Intent para abrir la actividad del carrito
                    Intent intent = new Intent(holder.itemView.getContext(), CarritoActivity.class);
                    intent.putExtra("editar", 1);
                    intent.putExtra("uuid_pedido", pedido.getUuidPedido());
                    holder.itemView.getContext().startActivity(intent);
                }else {
                    Toast.makeText(holder.itemView.getContext(), "El pedido ya no se puede Modificar", Toast.LENGTH_SHORT).show();
                }
            } else {
                verificarCancelarPedido(holder.itemView.getContext(), pedido.getFechaPedido(), pedido.getHora(), pedido);

            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView txtFechaPedido, txtMontoTotal;
        Button btnVerCarrito, btnAccionPedido;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFechaPedido = itemView.findViewById(R.id.txtFechaPedido);
            txtMontoTotal = itemView.findViewById(R.id.txtMontoTotal);
            btnVerCarrito = itemView.findViewById(R.id.btnVerCarrito);
            btnAccionPedido = itemView.findViewById(R.id.btnAccionPedido);


        }
    }

    private void showProductsDialog(Context context, Pedido pedido) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Productos del Pedido");

        // Crear un contenedor de texto para los productos
        StringBuilder productosTexto = new StringBuilder();

        // Iterar sobre los productos para construir el texto
        for (Producto producto : pedido.getProductos()) {
            productosTexto.append("Nombre: ").append(producto.getNombre())
                    .append("\nPrecio: $").append(producto.getPrecio())
                    .append("\n\n"); // Doble salto de línea entre productos
        }

        // Crear un TextView para mostrar el contenido
        TextView textView = new TextView(context);
        textView.setText(productosTexto.toString());
        textView.setPadding(32, 32, 32, 32); // Ajustar el padding para mejor visualización
        textView.setTextSize(16); // Ajustar el tamaño de texto si es necesario

        // Configurar el diálogo
        builder.setView(textView);
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void verificarCancelarPedido(Context context, String fechaPedido, String horaPedido, Pedido pedido) {
        // Formato de la fecha y hora del pedido
        String formatoFechaHora = "yyyy-MM-dd HH:mm:ss"; // Ajusta este formato según sea necesario

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(formatoFechaHora);
        try {
            // Combinar fecha y hora en un solo string
            String fechaHoraPedido = fechaPedido + " " + horaPedido;
            Date fechaPedidoDate = dateFormat.parse(fechaHoraPedido);
            Date fechaActual = new Date();

            // Calcular la diferencia en milisegundos
            long diferencia = fechaActual.getTime() - fechaPedidoDate.getTime();

            // Convertir la diferencia a minutos
            long diferenciaMinutos = diferencia / (60 * 1000);

            if (diferenciaMinutos > 5) {
                Toast.makeText(context, "El pedido ya no se puede cancelar.", Toast.LENGTH_SHORT).show();
            } else {
                // Crear un AlertDialog para mostrar la confirmación de la cancelación
                new AlertDialog.Builder(context)
                        .setTitle("Cancelar Pedido")
                        .setMessage("¿Estás seguro de que deseas cancelar el Pedido?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Llamar al método que enviará la cancelación al servidor
                                cancelarPedido(context, pedido.getUuidPedido());//se envia el uuid del pedido para cancelar
                            }
                        })
                        .setNegativeButton("No", null) // Si elige "No", simplemente cierra el diálogo
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al analizar la fecha y hora del pedido.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelarPedido(Context context, String uuidPedido) {
        // URL base del API PHP donde se realiza la solicitud POST
        String urlBase = "http://" + direccion + "/conexionbd/CancelarPedido.php";

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
                                Toast.makeText(context, "Pedido cancelado exitosamente", Toast.LENGTH_SHORT).show();
                                Log.e("Mensaje del Servidor", jsonObject.getString("message"));
                            } else {
                                Toast.makeText(context, "Error al cancelar el pedido: ", Toast.LENGTH_SHORT).show();
                                Log.e("Mensaje del Servidor", jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Crear el mapa de parámetros que se enviarán al servidor
                Map<String, String> params = new HashMap<>();
                params.put("uuid_pedido", uuidPedido);
                return params;
            }
        };
        // Añadir la solicitud a la RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}

