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


    //metodos para validar cada atributo
    public boolean nombreValido() {
        return nombre != null && nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+") && nombre.length() >= 2 && nombre.length() <= 40;
    }
    public boolean apellidoValido() {
        return apellido != null && apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+") && apellido.length() >= 2 && apellido.length() <= 40;
    }
    public boolean correoValido() {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.length() <= 60;
    }
    public boolean claveValida() {
        return clave != null && clave.length() >= 8 && clave.length() <= 30
                && clave.matches(".*[A-Z].*") && clave.matches(".*[a-z].*")
                && clave.matches(".*[0-9].*");
    }
    public boolean direccionValida() {
        return direccion != null && !direccion.trim().isEmpty() && direccion.length() >= 5 && direccion.length() <= 100;
    }
    public boolean telefonoValido() {
        return telefono != null && telefono.matches("[0-9+]{7,30}");
    }

    // Método general para validar todos los campos
    public boolean clienteValido() {
        return nombreValido() && apellidoValido() && correoValido() && claveValida()
                && direccionValida() && telefonoValido();
    }
}
