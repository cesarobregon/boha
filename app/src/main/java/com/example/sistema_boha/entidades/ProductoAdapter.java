package com.example.sistema_boha.entidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sistema_boha.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {
    private JSONArray productos;
    private Context context;

    public ProductoAdapter(Context context, JSONArray productos) {
        this.context = context;
        this.productos = productos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        try {
            // Obtener el producto en la posición actual
            JSONObject producto = productos.getJSONObject(position);

            // Asignar los datos a las vistas
            holder.textViewNombre.setText(producto.getString("nombre"));
            holder.textViewPrecio.setText("Precio: $" + producto.getString("precio"));

            // La imagen es una por defecto por ahora, puedes cambiarla si recibes una URL desde el JSON

            // Los botones por ahora no tienen funciones específicas
            holder.buttonAnadirCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lógica para añadir al carrito (sin implementar por ahora)
                }
            });

            holder.buttonVerDescripcion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lógica para ver la descripción (sin implementar por ahora)
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
            buttonAnadirCarrito = itemView.findViewById(R.id.btnDescripcion);
            buttonVerDescripcion = itemView.findViewById(R.id.btnAnadirAlCarrito);
        }
    }
}