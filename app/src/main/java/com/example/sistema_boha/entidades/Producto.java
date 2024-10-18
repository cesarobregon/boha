package com.example.sistema_boha.entidades;

public class Producto {

    //atributos de la clase
    private int id;
    private String nombre;
    private String descripcion;
    private String foto;
    private int precio;
    private String disponibilidad;
    private int id_categoria;

    //constructores de la clase

    public Producto(int id, String nombre, String descripcion, String foto, int precio,
                    String disponibilidad, int id_categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.foto = foto;
        this.precio = precio;
        this.disponibilidad = disponibilidad;
        this.id_categoria = id_categoria;
    }

    public Producto() {
        this.id = 0;
        this.nombre = "";
        this.descripcion = "";
        this.foto = "";
        this.precio = 0;
        this.disponibilidad = "";
        this.id_categoria = 0;
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
    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
    public int getPrecio() {
        return precio;
    }
    public void setPrecio(int precio) {
        this.precio = precio;
    }
    public String getDisponibilidad() {
        return disponibilidad;
    }
    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
    public int getId_categoria() {
        return id_categoria;
    }
    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }
}
