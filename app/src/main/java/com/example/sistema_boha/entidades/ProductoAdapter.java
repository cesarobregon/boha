package com.example.sistema_boha.entidades;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Necesario si decides usar Glide para cargar imágenes
import com.example.sistema_boha.CarritoManager;
import com.example.sistema_boha.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    public CarritoManager carritoManager;
    private JSONArray productos;
    private Context context;

    public ProductoAdapter(Context context, JSONArray productos, CarritoManager carritoManager) {
        this.context = context;
        this.productos = productos;
        this.carritoManager = carritoManager;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        try {
            // Obtener el producto en la posición actual
            JSONObject productoJson = productos.getJSONObject(position);

            // Crear una instancia de Producto a partir del JSON
            Producto producto = new Producto(productoJson);

            // Asignar los datos a las vistas
            holder.textViewNombre.setText(producto.getNombre());
            holder.textViewPrecio.setText("Precio: $" + producto.getPrecio());

            // Cargar la imagen desde la URL si existe
            if (!producto.getFoto().isEmpty()) {
                Glide.with(context)
                        .load(producto.getFoto())
                        .placeholder(R.drawable.boha_inicio) // Usa una imagen por defecto en caso de error
                        .into(holder.imageViewProducto);
            }

            // Configurar el botón "Añadir al Carrito"
            holder.buttonAnadirCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carritoManager.agregarProducto(producto); // Llama al método de instancia
                    Toast.makeText(context, producto.getNombre() + "Añadido al Carrito", Toast.LENGTH_SHORT).show();
                }
            });

            // Configurar el botón "Ver Descripción"
            holder.buttonVerDescripcion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lógica para ver la descripción
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productos.length();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewPrecio;
        ImageView imageViewProducto;
        Button buttonAnadirCarrito, buttonVerDescripcion;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
            imageViewProducto = itemView.findViewById(R.id.imageViewProducto);
            buttonAnadirCarrito = itemView.findViewById(R.id.btnAnadirAlCarrito);
            buttonVerDescripcion = itemView.findViewById(R.id.btnVerDescripcion);
        }
    }
}

