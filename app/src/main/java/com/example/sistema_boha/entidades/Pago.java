package com.example.sistema_boha.entidades;

public class Pago {
    private int id_pago;
    private String uuid_pedido;
    private int id_metodo;
    private int monto;
    private String fechapago;
    private String estado;

    public Pago(int id_pago, String uuid_pedido, int id_metodo, int monto, String fechapago, String estado) {
        this.id_pago = id_pago;
        this.uuid_pedido = uuid_pedido;
        this.id_metodo = id_metodo;
        this.monto = monto;
        this.fechapago = fechapago;
        this.estado = estado;
    }

    public  Pago(){
        this.id_pago = 0;
        this.uuid_pedido = "";
        this.id_metodo = 0;
        this.monto = 0;
        this.fechapago = "";
        this.estado = "";
    }

    public int getId_pago() {
        return id_pago;
    }

    public void setId_pago(int id_pago) {
        this.id_pago = id_pago;
    }

    public String getUuid_pedido() {
        return uuid_pedido;
    }

    public void setUuid_pedido(String uuid_pedido) {
        this.uuid_pedido = uuid_pedido;
    }

    public int getId_metodo() {
        return id_metodo;
    }

    public void setId_metodo(int id_metodo) {
        this.id_metodo = id_metodo;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public String getFechapago() {
        return fechapago;
    }

    public void setFechapago(String fechapago) {
        this.fechapago = fechapago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
