package com.example.sistema_boha;


import static android.content.ContentValues.TAG;
import android.content.Intent;
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
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistema_boha.conexion.conexion;
import com.example.sistema_boha.controladores.CryptoUtils;
import com.example.sistema_boha.entidades.Cliente;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class RegistrarUsuario extends AppCompatActivity {

    // Variables para los elementos de la interfaz
    EditText nombre, apellido, direccion, correo, numero, clave;
    Button botonRegistrar;
    String direccionip = conexion.direccion;
    Cliente cliente = new Cliente();
    private SecretKey secretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa los elementos visuales en activity_registrar_usuario.xml
        nombre = findViewById(R.id.txtNombre);
        apellido = findViewById(R.id.txtApellido);
        direccion = findViewById(R.id.txtDireccion);
        correo = findViewById(R.id.txtEmail);
        numero = findViewById(R.id.txtNumCelular);
        clave = findViewById(R.id.txtClave);
        botonRegistrar = findViewById(R.id.btn_Registrar);

        //se intenta registrar la reserva
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!controlCampos()){  //si el control de campos devuelve false se muestra el siguiente mensaje
                    Toast.makeText(RegistrarUsuario.this, "Error Al Registrar, Campos Incorrectos", Toast.LENGTH_SHORT).show();
                }else {  //si el control devuelve true, se ejecuta el metodo buscarEmail()
                    buscarEmail();
                }
            }
        });
    }

    //metodo para registrar un usuario Cliente con los datos ingresados por el usuario
    private void registrarUsuario(){

        // URL del api php donde se realiza la solicitud POST
        String url = "http://"+direccionip+"/conexionbd/Registrar.php";

        // Crear un StringRequest para realizar una solicitud POST al servidor
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() { // Listener que maneja la respuesta exitosa
            @Override
            public void onResponse(String s) {
                Toast.makeText(RegistrarUsuario.this, "Usuario Registrado Exitosamente", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(RegistrarUsuario.this, Login.class));
            }
        }, new Response.ErrorListener() { // Listener que maneja errores en la petición
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(RegistrarUsuario.this, "Error al Registrar", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onErrorResponse: " + volleyError.getMessage());
            }
        }){

            // Mapa de parámetros que se enviarán al servidor
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("nombre", cliente.getNombre());
                parametros.put("apellido", cliente.getApellido());
                parametros.put("direccion", cliente.getDireccion());
                parametros.put("email", cliente.getEmail());
                parametros.put("telefono", cliente.getTelefono());
                parametros.put("clave", cliente.getClave());
                return parametros;
            }
        };

        // Añadir la solicitud a la cola de solicitudes (RequestQueue) para que se ejecute
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(sr);
    }

    //metodo que verifica que los campos tengan un formato y una cantidad valida de caracteres
    private boolean controlCampos(){
        boolean valido = false;
        cliente.setNombre(nombre.getText().toString());
        cliente.setApellido(apellido.getText().toString());
        cliente.setDireccion(direccion.getText().toString());
        cliente.setEmail(correo.getText().toString());
        cliente.setTelefono(numero.getText().toString());
        cliente.setClave(clave.getText().toString());

        if(cliente.clienteValido()){
            valido = true; // Los datos son válidos, proceder con la lógica de registro

            //encriptar la clave
            try {
                // Obtener la clave fija
                SecretKey secretKey = CryptoUtils.getFixedKey();

                // Encriptar un valor
                String originalText = clave.getText().toString();
                String encryptedText = CryptoUtils.encrypt(originalText, secretKey);
                Log.e("Clave Encriptada", "Texto Encriptado: " + encryptedText);
                cliente.setClave(encryptedText);
            } catch (Exception e) {
                e.printStackTrace();
                cliente.setClave(clave.getText().toString());
            }
        }else {
            // Mostrar mensaje de error específico
            if (!cliente.nombreValido()) {
                nombre.setError("Nombre inválido. Se permiten solamente letras mayusculas y minusculas");
            }
            if (!cliente.apellidoValido()) {
                apellido.setError("Apellido inválido. Se permiten solamente letras mayusculas y minusculas");
            }
            if (!cliente.correoValido()) {
                correo.setError("Email inválido");
            }
            if (!cliente.claveValida()) {
                clave.setError("Contraseña inválida, Se permiten solamente Mayusculas, Minusculas y Números");
            }
            if (!cliente.direccionValida()) {
                direccion.setError("Dirección inválida");
            }
            if (!cliente.telefonoValido()) {
                numero.setError("Número inválido");
            }
        }
        return valido;
    }

    //metodo que verifica en la base de datos si el correo ingresado por el usuario
    // ya esta registrado, en caso de que no este, se ejecuta el metodo registrarUsuario()
    private void buscarEmail() {
        String url = "http://"+direccionip+"/conexionbd/BuscarCorreo.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("exists")) { // Correo ya registrado
                                Toast.makeText(getApplicationContext(), "El correo ya está registrado", Toast.LENGTH_SHORT).show();
                            } else { // Correo no registrado, proceder con el registro
                                registrarUsuario();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegistrarUsuario.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores
                        Toast.makeText(getApplicationContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Enviar parámetros POST
                Map<String, String> params = new HashMap<>();
                params.put("email", cliente.getEmail()); // Enviar el correo a PHP
                return params;
            }
        };
        // Agregar la solicitud a la cola
        requestQueue.add(stringRequest);
    }
}