package com.example.sistema_boha;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment {
    ImageButton comidas, viandas, promos, bebidas, reservas;
    Intent intent;

    public InicioFragment() {
    }
    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        comidas = view.findViewById(R.id.btn_Comidas);
        viandas = view.findViewById(R.id.btn_Viandas);
        promos = view.findViewById(R.id.btn_Promos);
        bebidas = view.findViewById(R.id.btn_Bebidas);
        reservas = view.findViewById(R.id.btn_Reservas);

        comidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), Comidas.class);
                startActivity(intent);
            }
        });
        viandas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), Viandas.class);
                startActivity(intent);
            }
        });
        promos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), Promociones.class);
                startActivity(intent);
            }
        });
        bebidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), Bebidas.class);
                startActivity(intent);
            }
        });
        reservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), Reservas.class);
                startActivity(intent);
            }
        });
        return view;
    }
}