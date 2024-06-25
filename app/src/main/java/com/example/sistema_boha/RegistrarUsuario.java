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

import java.util.HashMap;
import java.util.Map;

public class RegistrarUsuario extends AppCompatActivity {

    EditText nombre, apellido, direccion, correo, numero, clave;
    Button botonRegistrar;

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
        nombre = findViewById(R.id.txtNombre);
        apellido = findViewById(R.id.txtApellido);
        direccion = findViewById(R.id.txtDireccion);
        correo = findViewById(R.id.txtEmail);
        numero = findViewById(R.id.txtNumCelular);
        clave = findViewById(R.id.txtClave);
        botonRegistrar = findViewById(R.id.btn_Registrar);

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });
    }

    //metodo para registrar un usuario Cliente
    private void registrar(){
        String url = "https://cesarob.000webhostapp.com/conexionbd/Insertar.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(RegistrarUsuario.this, "Usuario Registrado Exitosamente", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(RegistrarUsuario.this, Login.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(RegistrarUsuario.this, "Error al Registrar", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Ocurrió una excepción: " + volleyError.getMessage(), volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("nombre", nombre.getText().toString());
                parametros.put("apellido", apellido.getText().toString());
                parametros.put("direccion", direccion.getText().toString());
                parametros.put("email", correo.getText().toString());
                parametros.put("telefono", numero.getText().toString());
                parametros.put("clave", clave.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(sr);
    }


}