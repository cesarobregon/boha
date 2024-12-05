package com.example.sistema_boha;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReservarMesa extends AppCompatActivity {

    // Variables para los elementos de la interfaz
    String direccion = conexion.direccion;
    Button btnAgregar, btnHora;
    private Toolbar toolbar;
    CalendarView calendarView;
    EditText editTextCantidad, editTextMotivo;
    String fechaSeleccionada, horaSeleccionada;
    int id_cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obténemos el id_cliente desde SharedPreferences (donde se guardan datos locales del usuario logueado)
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        id_cliente = sharedPreferences.getInt("id_cliente", -1);

        // Inicializa los elementos visuales en activity_reservas.xml
        calendarView = findViewById(R.id.calendarioReserva);
        editTextCantidad = findViewById(R.id.txtCantidadSillas);
        editTextMotivo = findViewById(R.id.txtMotivoReserva);
        btnHora = findViewById(R.id.btnHoraReserva);
        btnAgregar = findViewById(R.id.btnAgregarReserva);

        // Configurar el TimePickerDialog para que el usuario pueda cambiar la hora
        btnHora.setOnClickListener(v -> {
            // Abrir el TimePickerDialog con la hora actual del botón
            @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog = new TimePickerDialog(ReservarMesa.this, (view, hourOfDay, minute) -> {
                // Almacenar la nueva hora seleccionada
                horaSeleccionada = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);

                // Actualizar el texto del botón con la nueva hora
                btnHora.setText("Hora: " + horaSeleccionada);

            }, 12, 0, true); // Pasa la hora y minuto actual al TimePicker
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

        // se intenta registrar la reserva
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarReserva();
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
            getSupportActionBar().setTitle("Reservar Mesa");
            // Cambiar el color del ícono de retroceso
            Drawable upArrow = getResources().getDrawable(com.google.android.material.R.drawable.abc_ic_ab_back_material);
            // Asignar el ícono personalizado al ActionBar
            upArrow.setColorFilter(getResources().getColor(R.color.appColor), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    @Override // metodo que devuelve a la activity anterior al presionar el boton de la barra de navegacion
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Función para registrar la reserva
    private void registrarReserva() {
        // URL del api php donde se realiza la solicitud POST
        String url = "http://" + direccion + "/conexionbd/Reservar.php";

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
                        // en caso de ser exitoso el registro, se finaliza el activity y se redirige al usuario al inicio
                        Toast.makeText(ReservarMesa.this, "Reserva registrada exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ReservarMesa.this, InicioActivity.class));
                    } else {
                        Toast.makeText(ReservarMesa.this, "Error al registrar la reserva", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // Si hay un error en la respuesta JSON, se muestra un mensaje de error
                    e.printStackTrace();
                    Toast.makeText(ReservarMesa.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar errores de la solicitud, como problemas de red
                Toast.makeText(ReservarMesa.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Mapa de parámetros que se enviarán al servidor
                Map<String, String> params = new HashMap<>();
                params.put("id_cliente", String.valueOf(id_cliente));
                params.put("cantidad_personas", cantidadPersonas);
                params.put("fecha", fechaSeleccionada);
                params.put("hora", horaSeleccionada);
                params.put("motivo", motivo);
                return params;
            }
        };
        // Añadimos la solicitud a la RequestQueue (se coloca la solicitud en la cola para que se envíe)
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}