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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    EditText email, clave;

    Button registrar, ingresar;

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
        email = findViewById(R.id.txtEmail);
        clave = findViewById(R.id.txtClave);
        ingresar = findViewById(R.id.btnIngresar);
        registrar = findViewById(R.id.btn_Registrar);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUsuario = email.getText().toString();
                String claveUsuario = clave.getText().toString();
                buscarUsuario(emailUsuario, claveUsuario);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarse();
            }
        });

    }

    private void buscarUsuario(String email, String clave){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://cesarob.000webhostapp.com/conexionbd/Registro.php?email=" + email + "&clave=" + clave;
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Intent intent = new Intent(Login.this, inicio.class);
                    String id = jsonObject.getString("id_cliente");
                    intent.putExtra("id_cliente", id);
                    startActivity(intent);
                } catch (JSONException e) {
                    Toast.makeText(Login.this, "Email o Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Login.this, "Error al conectarse a la Base de Datos", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onErrorResponse: " + volleyError.getMessage());
            }
        }
        );
        requestQueue.add(jor);
    }

    private void registrarse(){
        finish();
        startActivity(new Intent(Login.this, RegistrarUsuario.class));
    }
}