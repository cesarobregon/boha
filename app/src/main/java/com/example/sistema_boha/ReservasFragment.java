package com.example.sistema_boha;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistema_boha.conexion.conexion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservasFragment extends Fragment {

    int id;
    Intent intent;

    String direccion = conexion.direccion;

    Button btn_editarReserva, btn_eliminarReserva;
    ImageButton reservas;
    TextView txtInfoReserva, txtTituloInfoReserva, txtNuevaReserva;
    String id_reserva = "", id_cliente, fecha, estado, hora, cantidad, motivo;

    JSONObject reserva;

    public ReservasFragment() {
        // Required empty public constructor
    }

    public static ReservasFragment newInstance(String param1, String param2) {
        return new ReservasFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservas, container, false);

        reservas = view.findViewById(R.id.btn_Reserva);
        txtInfoReserva = view.findViewById(R.id.txtInfoReserva);
        txtTituloInfoReserva = view.findViewById(R.id.txtTituloInfoReserva);
        txtNuevaReserva = view.findViewById(R.id.txtAgregarReserva);
        btn_editarReserva = view.findViewById(R.id.btnEditarReserva);
        btn_eliminarReserva = view.findViewById(R.id.btnCancelarReserva);

        reservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), ReservarMesa.class);
                startActivity(intent);
            }
        });

        btn_editarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editar(reserva);
            }
        });

        btn_eliminarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmacionCancelacion(id_reserva);
            }
        });

        cargarDatosReserva();
        return view;

    }
    private void cargarDatosReserva() {
        //Obtener SharedPreferences utilizando el contexto del Fragment
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        //obtener el id almacenado en el dispositivo
        id = preferences.getInt("id_cliente", 0);
        obtenerDatosPorId(id);
    }

    private void obtenerDatosPorId(int id) {
        // Crear una nueva cola de solicitudes para gestionar peticiones de red usando Volley
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        // URL donde se realiza la petición GET para obtener datos por ID
        String url = "http://" + direccion + "/conexionbd/buscarReserva.php?id=" + id;

        // Crear una solicitud para obtener los datos en formato JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() { // Listener que maneja la respuesta exitosa
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        reserva = response;
                        try {
                            // Manejar el objeto JSON recibido desde el API PHP
                            // Aquí puedes procesar el objeto como lo necesites
                            Log.d("Response", "Datos recibidos: " + response.toString());

                            // obtener valores del JSON
                            id_reserva = response.getString("id_reserva");
                            id_cliente = response.getString("id_cliente");
                            cantidad = response.getString("cantidad_personas");
                            estado = response.getString("estado");
                            fecha = response.getString("fecha");
                            hora = response.getString("hora");
                            motivo = response.getString("motivo");

                            txtInfoReserva.setText("Estado de su Reserva: " + estado +
                                    "\nFecha: " + fecha +
                                    "\nHora: " + hora +
                                    "\nCantidad de Personas: " + cantidad +
                                    "\nMotivo de la Reserva: " + motivo);

                            reservas.setVisibility(View.INVISIBLE);
                            txtNuevaReserva.setVisibility(View.INVISIBLE);
                            txtTituloInfoReserva.setVisibility(View.VISIBLE);
                            btn_editarReserva.setVisibility(View.VISIBLE);
                            btn_eliminarReserva.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            Log.e("No se Recibieron Reservas", "Sin Reservas");
                            // si se produce un JSONException significa que el cliente no tiene reservas
                        }
                    }
                }, new Response.ErrorListener() { // Listener que maneja errores en la petición
            @Override
            public void onErrorResponse(VolleyError error) {
                // Mostrar un mensaje si hay error al conectarse a la base de datos
                Toast.makeText(requireContext(), "Error al conectarse a la Base de Datos", Toast.LENGTH_SHORT).show();
                // Registrar el error en los logs
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        // Añadir la solicitud a la cola de solicitudes (RequestQueue) para que se ejecute
        requestQueue.add(jsonObjectRequest);
    }

    private void editar(JSONObject reserva){

        String primeraClave = "";

        // Obtener la primera clave
        Iterator<String> clave = reserva.keys();
        if (clave.hasNext()) {
            primeraClave = clave.next();
        }


        if(primeraClave.equals("mensaje")){
            Toast.makeText(requireContext(), "Aún no has Reservado una Mesa", Toast.LENGTH_SHORT).show();
        }else {
            // Redirigir a editarReserva
            Intent intent = new Intent(requireActivity(), editarReserva.class);

            // Convertir el JSONObject a String
            String reservaString = reserva.toString();
            // Poner el String del JSON en el Intent
            intent.putExtra("jsonObject", reservaString);

            startActivity(intent);
        }
    }

    private void ConfirmacionCancelacion(final String id_reserva) {
        String primeraClave = "";

        // Obtener la primera clave
        Iterator<String> clave = reserva.keys();
        if (clave.hasNext()) {
            primeraClave = clave.next();
        }
        if(primeraClave.equals("mensaje")){
            Toast.makeText(requireContext(), "Aún no has Reservado una Mesa", Toast.LENGTH_SHORT).show();
        }else {
            // Crear un AlertDialog para mostrar la confirmación de la cancelación
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancelar Reserva")
                    .setMessage("¿Estás seguro de que deseas cancelar la reserva?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Llamar al método que enviará la cancelación al servidor
                            cancelarReserva(id_reserva);
                        }
                    })
                    .setNegativeButton("No", null) // Si elige "No", simplemente cierra el diálogo
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void cancelarReserva(String id_reserva) {
        // Crear una nueva cola de solicitudes para gestionar peticiones de red usando Volley
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        // URL donde se realiza la petición GET para cancelar la reserva
        String url = "http://" + direccion + "/conexionbd/CancelarReserva.php?id_reserva=" + id_reserva;

        // Crear una solicitud para enviar la cancelación de la reserva
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() { // Listener que maneja la respuesta exitosa
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // Manejar la respuesta del servidor (por ejemplo, confirmar la cancelación)
                        Toast.makeText(requireContext(), "Reserva cancelada exitosamente", Toast.LENGTH_SHORT).show();
                        txtInfoReserva.setText("Aún no has Reservado una Mesa");
                        reservas.setVisibility(View.VISIBLE);
                        txtNuevaReserva.setVisibility(View.VISIBLE);
                        txtTituloInfoReserva.setVisibility(View.INVISIBLE);
                        btn_editarReserva.setVisibility(View.INVISIBLE);
                        btn_eliminarReserva.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() { // Listener que maneja errores en la petición
            @Override
            public void onErrorResponse(VolleyError error) {
                // Mostrar un mensaje si hay error al conectarse a la base de datos
                Toast.makeText(requireContext(), "Error al cancelar la reserva", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error en cancelarReserva: " + error.getMessage());
            }
        });

        // Añadir la solicitud a la cola de solicitudes (RequestQueue) para que se ejecute
        requestQueue.add(stringRequest);
    }

}