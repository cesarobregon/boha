package com.example.sistema_boha.entidades;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistema_boha.ListaProductos;
import com.example.sistema_boha.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {
    private JSONArray categorias;
    private Context context;

    public CategoriaAdapter(Context context, JSONArray categorias) {
        this.context = context;
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        try {
            // Obtener el producto en la posición actual
            JSONObject categoria = categorias.getJSONObject(position);

            // Asignar los datos a las vistas
            holder.id = categoria.getString("id_categoria"); // Asigna el id de la categoría a la variable en el ViewHolder
            holder.textViewNombre.setText(categoria.getString("nombre"));

            holder.btnVerProductos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crear un Intent para abrir el nuevo Activity
                    Intent intent = new Intent(context, ListaProductos.class);
                    // Pasar el id de la categoría al nuevo Activity
                    intent.putExtra("idCategoria", holder.id);
                    // Iniciar el nuevo Activity
                    context.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return categorias.length();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        String id;
        TextView textViewNombre;
        Button btnVerProductos;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            btnVerProductos = itemView.findViewById(R.id.btnVerProductos);
        }
    }
}