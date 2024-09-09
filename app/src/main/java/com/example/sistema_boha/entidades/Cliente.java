package com.example.sistema_boha.entidades;

public class Cliente {

    //atributos de la clase
    private int id;
    private String nombre, apellido, email, clave, direccion, telefono;

    //constructores de la clase
    public Cliente(int id, String nombre, String apellido, String email, String clave, String direccion, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.clave = clave;
        this.direccion = direccion;
        this.telefono = telefono;
    }
    public Cliente() {
        this.id = 0;
        this.nombre = "";
        this.apellido = "";
        this.email = "";
        this.clave = "";
        this.direccion = "";
        this.telefono = "";
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
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getClave() {
        return clave;
    }
    public void setClave(String clave) {
        this.clave = clave;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
