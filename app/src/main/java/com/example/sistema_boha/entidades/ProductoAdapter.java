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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.example.sistema_boha.CarritoManager;
import com.example.sistema_boha.R;
import com.example.sistema_boha.conexion.conexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    public CarritoManager carritoManager;
    private JSONArray productos;
    String direccion = conexion.direccion;
    private Context context;

    public ProductoAdapter(Context context, JSONArray productos, CarritoManager carritoManager) {
        this.context = context;
        this.productos = productos;
        this.carritoManager = carritoManager;
    }

    public ProductoAdapter(List<Producto> productos) {
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


            // URL de la imagen que deseas cargar
            String imageUrl = "http://"+direccion+"/BOHAFINAL/" + producto.getFoto();

            // Cargar la imagen desde la URL si existe
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_notfound) // Imagen mientras se carga
                    .error(R.drawable.img_notfound) // Imagen de error en caso de fallo
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Log del error
                            if (e != null) {
                                e.logRootCauses("GlideError");
                            }
                            return false; // Devuelve false para permitir que Glide maneje el error
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Imagen cargada correctamente
                            return false;
                        }
                    })
                    .into(holder.imageViewProducto);


            // Configurar el botón "Añadir al Carrito"
            holder.buttonAnadirCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carritoManager.agregarProducto(producto); // Llama al método de instancia
                    Toast.makeText(context, producto.getNombre() + " añadido al Carrito", Toast.LENGTH_SHORT).show();
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
        return productos != null ? productos.length() : 0;
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewPrecio;
        ImageView imageViewProducto;
        // Dirección IP del servidor
        String direccion = conexion.direccion;
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

