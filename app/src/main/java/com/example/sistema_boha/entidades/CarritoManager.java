package com.example.sistema_boha.entidades;

import java.util.ArrayList;
import java.util.List;

public class CarritoManager {
    public CarritoManager instance;
    public List<Producto> productos;

    // Constructor privado para el patrón Singleton
    public CarritoManager() {
        productos = new ArrayList<>();
    }

    // Método para obtener la instancia única
    public synchronized CarritoManager getInstance() {
        if (instance == null) {
            instance = new CarritoManager();
        }
        return instance;
    }

    // Agregar un producto al carrito
    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    // Obtener todos los productos en el carrito
    public List<Producto> obtenerCarrito() {
        return productos;
    }

    // Vaciar el carrito
    public void vaciarCarrito() {
        productos.clear();
    }
}

