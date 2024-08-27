package com.example.sistema_boha.entidades;

public class Comida {

    //atributos de la clase
    private int id;
    private String nombre;
    private String descripcion;
    private int precio;

    //constructores de la clase
    public Comida(int id, String nombre, String descripcion, int precio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }
    public Comida() {
        this.id = 0;
        this.nombre = "";
        this.descripcion = "";
        this.precio = 0;
    }

    //metodos para obtener o modificar los valores de los atributos
    public int getId() {
        return id;
    }
    public void setId(int id) {
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
    public int getPrecio() {
        return precio;
    }
    public void setPrecio(int precio) {
        this.precio = precio;
    }
}
