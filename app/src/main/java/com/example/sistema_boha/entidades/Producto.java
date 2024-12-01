package com.example.sistema_boha.entidades;

import org.json.JSONException;
import org.json.JSONObject;

public class Producto {

    //atributos de la clase
    private String id;
    private String nombre;
    private String descripcion;
    private String foto;
    private String precio;
    private String disponibilidad;
    private String id_categoria;
    private int cantidad;

    //constructores de la clase

    public Producto(String id, String nombre, String descripcion, String foto, String precio,
                    String disponibilidad, String id_categoria, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.foto = foto;
        this.precio = precio;
        this.disponibilidad = disponibilidad;
        this.id_categoria = id_categoria;
        this.cantidad = 1;
    }

    public Producto() {
        this.id = "";
        this.nombre = "";
        this.descripcion = "";
        this.foto = "";
        this.precio = "";
        this.disponibilidad = "";
        this.id_categoria = "";
        this.cantidad = 1;
    }

    // Constructor que toma un JSONObject
    public Producto(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id_producto"); // Asegúrate de que los nombres coincidan con los del JSON
        this.nombre = jsonObject.getString("nombre");
        this.descripcion = jsonObject.getString("descripcion");
        this.foto = jsonObject.optString("foto", ""); // Si "foto" no existe en el JSON, se usa una cadena vacía
        this.precio = jsonObject.getString("precio");
        this.disponibilidad = jsonObject.getString("disponibilidad");
        this.id_categoria = jsonObject.getString("id_categoria");
        this.cantidad = 1;
    }

    public Producto(String productoA, int i, double v) {
    }

    //metodos para obtener o modificar los valores de los atributos

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getFoto() {
        return foto;
    }
    public void setFoto(String foto) {
        this.foto = foto;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public String getPrecio() {
        return precio;
    }
    public void setPrecio(String precio) {
        this.precio = precio;
    }
    public String getDisponibilidad() {
        return disponibilidad;
    }
    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
    public String getId_categoria() {
        return id_categoria;
    }
    public void setId_categoria(String id_categoria) {
        this.id_categoria = id_categoria;
    }
}
