package com.example.sistema_boha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistema_boha.entidades.Producto;


import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {
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

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        Producto producto = productos.get(position);

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
        Button buttonEliminar;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
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
