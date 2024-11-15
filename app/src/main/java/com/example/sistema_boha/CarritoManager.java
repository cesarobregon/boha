package com.example.sistema_boha;

import com.example.sistema_boha.entidades.Producto;

import java.util.ArrayList;
import java.util.List;

public class CarritoManager {
    private static CarritoManager instance;
    private List<Producto> productos;

    public CarritoManager() {
        productos = new ArrayList<>();
    }

    public static synchronized CarritoManager getInstance() {
        if (instance == null) {
            instance = new CarritoManager();
        }
        return instance;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public List<Producto> obtenerCarrito() {
        return productos;
    }

    public void vaciarCarrito() {
        productos.clear();
    }
}
