package com.example.sistema_boha;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.example.sistema_boha.controladores.EncriptarDesencriptar;
import com.example.sistema_boha.entidades.Cliente;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class editarDatosUsuario extends AppCompatActivity {

    Button btn_guardar;
    private Toolbar toolbar;
    String direccion = conexion.direccion;
    int id;
    private SecretKey secretKey;

    Cliente cliente = new Cliente();
    EditText txtNombre, txtApellido, txtEmail, txtDireccion, txtTelefono, txtClave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_datos_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Editar Datos del Usuario");
            @SuppressLint("UseCompatLoadingForDrawables") Drawable upArrow = getResources().getDrawable(com.google.android.material.R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.appColor), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }


        txtNombre = findViewById(R.id.txtEditarNombre);
        txtApellido = findViewById(R.id.txtEditarApellido);
        txtEmail = findViewById(R.id.txtEditarCorreo);
        txtDireccion = findViewById(R.id.txtEditarDireccion);
        txtTelefono = findViewById(R.id.txtEditarTelefono);
        txtClave = findViewById(R.id.txtEditarClave);
        btn_guardar = findViewById(R.id.btnEditarGuardar);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!controlCampos()){  //si el control de campos devuelve false se muestra el siguiente mensaje
                    Toast.makeText(editarDatosUsuario.this, "Error Al Registrar, Campos Incorrectos", Toast.LENGTH_SHORT).show();
                }else {  //si el control devuelve true, se ejecuta el metodo buscarEmail()
                    editarDatosCliente();
                }
            }
        });

        // Obtener y mostrar los datos del usuario
        cargarDatosUsuario();
    }
    @Override // Vuelve a la activity anterior
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void cargarDatosUsuario() {

        // Obtener SharedPreferences utilizando el contexto del Fragment
        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);

        // Obtener los datos guardados en SharedPreferences
        id = preferences.getInt("id_cliente", 0);
        String nombre = preferences.getString("nombre", "Nombre no disponible");
        String apellido = preferences.getString("apellido", "Apellido no disponible");
        String email = preferences.getString("email", "Email no disponible");
        String direccion = preferences.getString("direccion", "Dirección no disponible");
        String telefono = preferences.getString("telefono", "telefono no disponible");
        String clave = preferences.getString("clave", "Clave no disponible");

        String ClaveDesencriptada;
        //desencriptar la clave
        try {
            // Obtener la clave fija
            SecretKey secretKey = EncriptarDesencriptar.getFixedKey();
            // Desencriptar el valor
            String decryptedText = EncriptarDesencriptar.desencriptar(clave, secretKey);
            Log.e("Clave Desencriptada", "Texto Desencriptado: " + decryptedText);
            ClaveDesencriptada = decryptedText;
        } catch (Exception e) {
            e.printStackTrace();
            ClaveDesencriptada = "";
        }

        // Mostrar los datos en los TextViews
        txtNombre.setText(nombre);
        txtApellido.setText(apellido);
        txtEmail.setText(email);
        txtDireccion.setText(direccion);
        txtTelefono.setText(telefono);
        txtClave.setText(ClaveDesencriptada);
    }
    private void editarDatosCliente(){
        String url = "http://"+direccion+"/conexionbd/Editar.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(editarDatosUsuario.this, "Usuario Modificado Exitosamente", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(editarDatosUsuario.this, InicioActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(editarDatosUsuario.this, "Error al Editar", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(editarDatosUsuario.this, InicioActivity.class));
                Log.e(TAG, "onErrorResponse: " + volleyError.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_cliente", String.valueOf(id));
                parametros.put("nombre", txtNombre.getText().toString());
                parametros.put("apellido", txtApellido.getText().toString());
                parametros.put("direccion", txtDireccion.getText().toString());
                parametros.put("email", txtEmail.getText().toString());
                parametros.put("telefono", txtTelefono.getText().toString());
                parametros.put("clave", txtClave.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(sr);
    }

    private boolean controlCampos(){
        boolean valido = false;
        cliente.setNombre(txtNombre.getText().toString());
        cliente.setApellido(txtApellido.getText().toString());
        cliente.setDireccion(txtDireccion.getText().toString());
        cliente.setEmail(txtEmail.getText().toString());
        cliente.setTelefono(txtTelefono.getText().toString());
        cliente.setClave(txtClave.getText().toString());
        if(cliente.clienteValido()){
            valido = true; // Los datos son válidos, proceder con la lógica de registro
            //encriptar la clave
            try {
                // Obtener la clave fija
                SecretKey secretKey = EncriptarDesencriptar.getFixedKey();
                // Encriptar un valor
                String originalText = txtClave.getText().toString();
                String encryptedText = EncriptarDesencriptar.encriptar(originalText, secretKey);
                Log.e("Clave Encriptada", "Texto Encriptado: " + encryptedText);
                cliente.setClave(encryptedText);
            } catch (Exception e) {
                e.printStackTrace();
                cliente.setClave(txtClave.getText().toString());
            }
        }else {
            // Mostrar mensaje de error específico
            if (!cliente.nombreValido()) {
                txtNombre.setError("Nombre inválido. Se permiten solamente letras mayusculas y minusculas");
            }
            if (!cliente.apellidoValido()) {
                txtApellido.setError("Apellido inválido. Se permiten solamente letras mayusculas y minusculas");
            }
            if (!cliente.correoValido()) {
                txtEmail.setError("Email inválido");
            }
            if (!cliente.claveValida()) {
                txtClave.setError("Contraseña inválida, Se permiten solamente Mayusculas, Minusculas y Números");
            }
            if (!cliente.direccionValida()) {
                txtDireccion.setError("Dirección inválida");
            }
            if (!cliente.telefonoValido()) {
                txtTelefono.setError("Número inválido");
            }
        }
        return valido;
    }
}