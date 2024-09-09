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
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    EditText email, clave;
    Button registrar, ingresar, ingresar2, recuperarClave;

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
        ingresar2 = findViewById(R.id.btnIngreso2);
        registrar = findViewById(R.id.btn_Registrar);
        recuperarClave = findViewById(R.id.btnRecuperarClave);

        recuperarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, recuperarClave.class);
                startActivity(intent);
            }
        });

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camposVacios()){
                    Toast.makeText(Login.this, "Falta rellenar algunos campos", Toast.LENGTH_SHORT).show();
                }else {
                    String emailUsuario = email.getText().toString();
                    String claveUsuario = clave.getText().toString();
                    validarUsuario(emailUsuario, claveUsuario);
                }
            }
        });

        ingresar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, InicioActivity.class);
                startActivity(intent);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarse();
            }
        });

    }

    private void validarUsuario(String email, String clave){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.10.127/conexionbd/Validar.php?email=" + email + "&clave=" + clave;
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if(jsonObject.getString("email").equals(email) && jsonObject.getString("clave").equals(clave)){
                        Intent intent = new Intent(Login.this, InicioActivity.class);
                        guardarDatosUsuario(jsonObject);
                        startActivity(intent);
                        finish();
                    }
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

            // Guardar datos en SharedPreferences
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
            e.printStackTrace();
            Toast.makeText(Login.this, "Error al guardar los datos del usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void registrarse(){
        Intent intent = new Intent(Login.this, RegistrarUsuario.class);
        startActivity(intent);
    }

    private boolean camposVacios(){
        boolean isEmpty = false;
        if (email.getText().toString().isEmpty() | clave.getText().toString().isEmpty()){
            isEmpty = true;
        }
        return isEmpty;
    }
}