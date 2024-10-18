package com.example.sistema_boha.entidades;

public class Reserva {
    int id_reserva;
    String id_cliente;
    String cantidad_personas;
    String fecha;
    String hora;
    String motivo;

    public Reserva(int id_reserva, String id_cliente, String cantidad_personas, String fecha, String hora, String motivo) {
        this.id_reserva = id_reserva;
        this.id_cliente = id_cliente;
        this.cantidad_personas = cantidad_personas;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
    }

    public Reserva() {
        this.id_reserva = 0;
        this.id_cliente = "";
        this.cantidad_personas = "";
        this.fecha = "";
        this.hora = "";
        this.motivo = "";
    }

    public int getId_reserva() {
        return id_reserva;
    }
    public void setId_reserva(int id_reserva) {
        this.id_reserva = id_reserva;
    }
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }
    public String getId_cliente() {
        return id_cliente;
    }
    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }
    public String getCantidad_personas() {
        return cantidad_personas;
    }
    public void setCantidad_personas(String cantidad_personas) {
        this.cantidad_personas = cantidad_personas;
    }
    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }


}
