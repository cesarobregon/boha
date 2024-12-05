package com.example.sistema_boha;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sistema_boha.conexion.conexion;
import com.example.sistema_boha.entidades.Producto;


import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    String direccion = conexion.direccion;
    private List<Producto> productos;
    private Context context;

    public CarritoAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false);
        return new CarritoViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        Producto producto = productos.get(position);

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
                .into(holder.imgProductoCarrito);

        holder.textViewNombre.setText(producto.getNombre());
        holder.textViewPrecio.setText("Precio: $" + producto.getPrecio());

        // Eliminar el producto
        holder.buttonEliminar.setOnClickListener(v -> {
            productos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, productos.size());
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewPrecio;
        ImageView imgProductoCarrito;
        ImageButton buttonEliminar;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
            imgProductoCarrito = itemView.findViewById(R.id.imageViewProducto);
            buttonEliminar = itemView.findViewById(R.id.buttonEliminar);
        }
    }

    public void setProductos(List<Producto> carrito){
        if (carrito == null){
            Producto producto = new Producto();
            carrito.add(producto);
        }else {
            productos = carrito;
        }

    }
}
