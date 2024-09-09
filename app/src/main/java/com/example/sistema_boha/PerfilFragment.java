package com.example.sistema_boha;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    Button btn_editar, btn_logout;

    TextView txtNombre, txtApellido, txtEmail, txtDireccion, txtTelefono;

    public PerfilFragment() {
        // Required empty public constructor
    }

    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar los TextViews
        txtNombre = view.findViewById(R.id.txtPerfilNombre);
        txtApellido = view.findViewById(R.id.txtPerfilApellido);
        txtEmail = view.findViewById(R.id.txtPerfilEmail);
        txtDireccion = view.findViewById(R.id.txtPerfilDireccion);
        txtTelefono = view.findViewById(R.id.txtPerfilTelefono);

        // Obtener y mostrar los datos del usuario
        cargarDatosUsuario();

        btn_editar = view.findViewById(R.id.btnPerfilEditar);
        btn_logout = view.findViewById(R.id.btnPerfilLogout);

        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editar();
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return view;
    }
    private void cargarDatosUsuario() {

        // Obtener SharedPreferences utilizando el contexto del Fragment
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);

        // Obtener los datos guardados en SharedPreferences
        String nombre = preferences.getString("nombre", "Nombre no disponible");
        String apellido = preferences.getString("apellido", "Nombre no disponible");
        String email = preferences.getString("email", "Email no disponible");
        String direccion = preferences.getString("direccion", "Dirección no disponible");
        String telefono = preferences.getString("telefono", "Nombre no disponible");

        // Mostrar los datos en los TextViews
        txtNombre.setText(nombre);
        txtApellido.setText(apellido);
        txtEmail.setText(email);
        txtDireccion.setText(direccion);
        txtTelefono.setText(telefono);

        //solucionar error
        //java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object reference

    }

    private void logout(){
        //Limpiar los datos de SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        //mensaje de confirmación
        Toast.makeText(requireActivity(), "Sesión cerrada", Toast.LENGTH_SHORT).show();

        // Redirigir al Login
        Intent intent = new Intent(requireActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar la pila de actividades
        startActivity(intent);
        requireActivity().finish(); // Cierra el Activity actual para evitar volver atrás
    }
    private void editar(){
        // Redirigir al Login
        Intent intent = new Intent(requireActivity(), editarDatosUsuario.class);
        startActivity(intent);
    }
}