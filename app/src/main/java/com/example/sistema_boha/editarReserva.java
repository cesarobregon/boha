package com.example.sistema_boha;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistema_boha.conexion.conexion;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class editarReserva extends AppCompatActivity {

    private Toolbar toolbar;
    String direccion = conexion.direccion;
    Button btnGuardar , btnCambiarHora;
    CalendarView calendarView;
    EditText editTextCantidad, editTextMotivo;
    String fechaSeleccionada, horaSeleccionada;
    String id_reserva;
    JSONObject reserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_reserva);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //recibir los datos de la reserva desde el carrito

        Intent intent = getIntent(); // Obtener el Intent que inició este activity
        String reservaString = intent.getStringExtra("jsonObject"); // Obtener el String del JSONObject del Intent
        // Convertir el String nuevamente a un JSONObject
        try {
            reserva = new JSONObject(reservaString);
            Log.d("editarReserva", "JSON recibido: " + reserva.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Inicializa los elementos visuales en activity_reservas.xml
        calendarView = findViewById(R.id.calendarioReservaEditar);
        editTextCantidad = findViewById(R.id.txtCantidadSillasEditar);
        editTextMotivo = findViewById(R.id.txtMotivoReservaEditar);
        btnCambiarHora = findViewById(R.id.btnHoraReservaEditar);
        btnGuardar = findViewById(R.id.btnGuardarCambiosReserva);

        // Configurar el TimePickerDialog para que el usuario pueda cambiar la hora
        btnCambiarHora.setOnClickListener(v -> {
            // Dividir la hora en horas y minutos
            String[] horaArray = new String[0];
            try {
                horaArray = reserva.getString("hora").split(":");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            int horaActual = Integer.parseInt(horaArray[0]);
            int minutoActual = Integer.parseInt(horaArray[1]);

            // Abrir el TimePickerDialog con la hora actual del botón
            @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog = new TimePickerDialog(editarReserva.this, (view, hourOfDay, minute) -> {
                // Almacenar la nueva hora seleccionada
                horaSeleccionada = hourOfDay + ":" + minute;

                // Actualizar el texto del botón con la nueva hora
                btnCambiarHora.setText("Hora: " + horaSeleccionada);

            }, horaActual, minutoActual, true); // Pasa la hora y minuto actual al TimePicker
            timePickerDialog.show();
        });

        // Obtener la fecha seleccionada desde el CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Almacena la fecha seleccionada en formato yyyy-MM-dd
                fechaSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth; // Formato yyyy-MM-dd
            }
        });

        //se intenta registrar la reserva
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarReserva();
            }
        });

        // Inicializar la barra de herramientas (toolbar) y configurarla como ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Configurar el ActionBar con un botón de retroceso y el título
        if (getSupportActionBar() != null) {
            // Habilitar el botón de retroceso en el ActionBar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Establecer el título de la actividad
            getSupportActionBar().setTitle("Modificar Reserva");
            // Cambiar el color del ícono de retroceso
            Drawable upArrow = getResources().getDrawable(com.google.android.material.R.drawable.abc_ic_ab_back_material);
            // Asignar el ícono personalizado al ActionBar
            upArrow.setColorFilter(getResources().getColor(R.color.appColor), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        cargarDatosReserva();

    }

    @Override // metodo que devuelve a la activity anterior al presionar el boton de la barra de navegacion
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void cargarDatosReserva(){
        try {
            // Obtener la fecha del objeto JSON
            String fecha = reserva.getString("fecha");
            // Definir el formato de la fecha (año-mes-día)
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            // Parsear el String de la fecha a un objeto Date
            Date fechaDate = formatoFecha.parse(fecha);

            // Si la fecha es válida, asignarla al CalendarView
            if (fechaDate != null) {
                // Obtener el tiempo en milisegundos de la fecha
                long milisegundos = fechaDate.getTime();

                // Asignar la fecha al CalendarView
                calendarView.setDate(milisegundos, true, true); // Los últimos dos parámetros son para animación
            }

            fechaSeleccionada = reserva.getString("fecha");
            // Obtener la hora del objeto JSON
            horaSeleccionada = reserva.getString("hora");
            // Asignar la hora recibida como texto al botón
            btnCambiarHora.setText("Hora: " + horaSeleccionada);

            editTextMotivo.setText(reserva.getString("motivo"));
            editTextCantidad.setText(reserva.getString("cantidad_personas"));

            id_reserva = reserva.getString("id_reserva");

        } catch (JSONException | ParseException e) {
            Log.e("editarReserva", Objects.requireNonNull(e.getMessage()));
        }
    }

    // Función para registrar la reserva
    private void editarReserva() {
        // URL del api php donde se realiza la solicitud POST
        String url = "http://"+direccion+"/conexionbd/editarReserva.php";

        // Obténemos los valores de los EditText
        String cantidadPersonas = editTextCantidad.getText().toString();
        String motivo = editTextMotivo.getText().toString();

        //  Controles de los Campos

        // Verificamos que no haya campos vacíos
        if (cantidadPersonas.isEmpty() || motivo.isEmpty() || fechaSeleccionada == null || horaSeleccionada == null) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        // Verificación de cantidad de personas
        if (Integer.parseInt(cantidadPersonas) > 15) {
            Toast.makeText(this, "Se pueden Reservar como máximo 15 Sillas", Toast.LENGTH_SHORT).show();
            return;
        }
        // Verificación de fecha
        Calendar fechaActual = Calendar.getInstance();
        fechaActual.add(Calendar.DAY_OF_YEAR, 1); // Añadir un día para obtener el día siguiente
        Calendar fechaSeleccionadaCal = Calendar.getInstance();
        String[] partesFecha = fechaSeleccionada.split("-");
        fechaSeleccionadaCal.set(Integer.parseInt(partesFecha[0]), Integer.parseInt(partesFecha[1]) - 1, Integer.parseInt(partesFecha[2]));
        if (fechaSeleccionadaCal.before(fechaActual)) {
            Toast.makeText(this, "Fecha Incorrecta", Toast.LENGTH_SHORT).show();
            return;
        }
        // Verificación de hora
        String[] partesHora = horaSeleccionada.split(":");
        int hora = Integer.parseInt(partesHora[0]);
        int minuto = Integer.parseInt(partesHora[1]);
        if (hora < 8 || (hora >= 23 && minuto > 0)) {
            Toast.makeText(this, "La hora debe estar entre las 08:00 y las 23:00", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un StringRequest para realizar una solicitud POST al servidor
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // se recibe un objeto json desde el servidor con la informacion del registro en la base de datos
                try {
                    // Convertir la respuesta en un objeto JSON
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        //en caso de ser exitoso el registro, se finaliza el activity y se redirige al usuario al inicio
                        Toast.makeText(editarReserva.this, "Reserva editada exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(editarReserva.this, InicioActivity.class));
                    } else {
                        Toast.makeText(editarReserva.this, "Error al editar la reserva", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // Si hay un error en la respuesta JSON, se muestra un mensaje de error
                    e.printStackTrace();
                    Toast.makeText(editarReserva.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar errores de la solicitud, como problemas de red
                Toast.makeText(editarReserva.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Mapa de parámetros que se enviarán al servidor
                Map<String, String> params = new HashMap<>();
                params.put("id_reserva", id_reserva);
                params.put("cantidad_personas", cantidadPersonas);
                params.put("fecha", fechaSeleccionada);
                params.put("hora", horaSeleccionada);
                params.put("motivo", motivo);
                return params;
            }
        };
        // Añadimo la solicitud a la RequestQueue (se coloca la solicitud en la cola para que se envíe)
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}