package com.example.sistema_boha;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistema_boha.conexion.conexion;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    // se definen los elementos de la interfaz
    EditText email, clave;
    Button registrar, ingresar, recuperarClave;

    // Dirección IP del servidor
    String direccion = conexion.direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // se vincula los elementos de la interfaz con sus id en el layout
        email = findViewById(R.id.txtEmail);
        clave = findViewById(R.id.txtClave);
        ingresar = findViewById(R.id.btnIngresar);
        registrar = findViewById(R.id.btn_Registrar);
        recuperarClave = findViewById(R.id.btnRecuperarClave);

        // Configurar el botón "Recuperar Clave" para abrir la actividad de recuperación de contraseña
        recuperarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Login.this, recuperarClave.class);
//                startActivity(intent);
                Toast.makeText(Login.this, "Funcion en Desarrollo", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón "Ingresar" para validar el usuario
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar si hay campos vacíos
                if (camposVacios()){
                    Toast.makeText(Login.this, "Falta rellenar algunos campos", Toast.LENGTH_SHORT).show();
                }else {
                    // Si los campos están llenos, obtener los valores ingresados y
                    //llamar a la funcion para validar
                    String emailUsuario = email.getText().toString();
                    String claveUsuario = clave.getText().toString();
                    validarUsuario(emailUsuario, claveUsuario);
                }
            }
        });

        // botón "Registrar" para abrir la actividad de registro de usuario
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarse();
            }
        });
    }

    // Método para validar las credenciales del usuario en el servidor
    private void validarUsuario(String email, String clave){
        // Crear una nueva cola de solicitudes para gestionar peticiones de red usando Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // URL donde se realiza la petición GET para validar el usuario
        String url = "http://"+direccion+"/conexionbd/Validar.php?email=" + email + "&clave=" + clave;

        // Crear una solicitud para obtener los datos del usuario en formato JSON
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() { // Listener que maneja la respuesta exitosa
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    // Si las credenciales coinciden, navegar a la actividad principal
                    if(jsonObject.getString("email").equals(email) && jsonObject.getString("clave").equals(clave)){
                        Intent intent = new Intent(Login.this, InicioActivity.class);
                        guardarDatosUsuario(jsonObject); // Guardar los datos del usuario en SharedPreferences
                        startActivity(intent);
                        finish();  // Cerrar la actividad de login para no volver al hacer back
                    }
                } catch (JSONException e) {
                    // Mostrar mensaje si las credenciales son incorrectas
                    Toast.makeText(Login.this, "Email o Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() { // Listener que maneja errores en la petición
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Mostrar un mensaje si hay error al conectarse a la base de datos
                Toast.makeText(Login.this, "Error al conectarse a la Base de Datos", Toast.LENGTH_SHORT).show();
                // Registrar el error en los logs
                Log.e(TAG, "onErrorResponse: " + volleyError.getMessage());
            }
        });
        // Añadir la solicitud a la cola de solicitudes (RequestQueue) para que se ejecute
        requestQueue.add(jor);
    }

    // Método para guardar los datos del usuario en SharedPreferences
    private void guardarDatosUsuario(JSONObject JsonUsuario){
        try {
            // Obtener datos del JSON
            int id = JsonUsuario.getInt("id_cliente");
            String nombre = JsonUsuario.getString("nombre");
            String apellido = JsonUsuario.getString("apellido");
            String email = JsonUsuario.getString("email");
            String direccion = JsonUsuario.getString("direccion");
            String clave = JsonUsuario.getString("clave");
            String telefono = JsonUsuario.getString("telefono");

            // Guardar los datos en SharedPreferences para mantener la sesión iniciada
            SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("id_cliente", id);
            editor.putString("nombre", nombre);
            editor.putString("apellido", apellido);
            editor.putString("email", email);
            editor.putString("direccion", direccion);
            editor.putString("clave", clave);
            editor.putString("telefono", telefono);
            editor.apply();

            Log.d("Login", "Datos de usuario guardados en SharedPreferences");

        } catch (JSONException e) {
            // Manejar errores de JSON al obtener los datos
            e.printStackTrace();
            Toast.makeText(Login.this, "Error al guardar los datos del usuario", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para abrir la actividad de registro de usuario
    private void registrarse(){
        Intent intent = new Intent(Login.this, RegistrarUsuario.class);
        startActivity(intent);
    }

    // Método para verificar si hay campos vacíos en los campos de texto
    private boolean camposVacios(){
        boolean isEmpty = false;
        // Verificar si el campo de email o clave está vacío
        if (email.getText().toString().isEmpty() | clave.getText().toString().isEmpty()){
            isEmpty = true;
        }
        return isEmpty; // Retornar true si hay campos vacíos, de lo contrario false
    }
}