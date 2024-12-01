package com.example.sistema_boha.entidades;

import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {

    //atributos de la tabla pedidos
    private String uuidPedido;
    private int id_cliente;
    private String fechaPedido;
    private String hora;
    private String tipoEntrega;
    private String estadoPedido;
    private int modificado;

    //atributos de la tabla pagos

    private int id_pago;
    private int id_metodo;
    private String fechaPago;
    private String montoTotal;
    private String estadoPago;

    //atributos de la tabla detalle
    private List<Producto> productos;

    public Pedido(String uuidPedido, int id_cliente, String fechaPedido, String hora, String tipoEntrega,
                  String estadoPedido, int modificado, int id_pago, int id_metodo, String fechaPago,
                  String montoTotal, String estadoPago, List<Producto> productos) {
        this.uuidPedido = uuidPedido;
        this.id_cliente = id_cliente;
        this.fechaPedido = fechaPedido;
        this.hora = hora;
        this.tipoEntrega = tipoEntrega;
        this.estadoPedido = estadoPedido;
        this.modificado = modificado;
        this.id_pago = id_pago;
        this.id_metodo = id_metodo;
        this.fechaPago = fechaPago;
        this.montoTotal = montoTotal;
        this.estadoPago = estadoPago;
        this.productos = productos;
    }

    public  Pedido(){
        this.uuidPedido = "";
        this.id_cliente = 0;
        this.fechaPedido = "";
        this.hora = "";
        this.tipoEntrega = "";
        this.estadoPedido = "";
        this.modificado = 0;
        this.id_pago = 0;
        this.id_metodo = 0;
        this.fechaPago = "";
        this.montoTotal = "";
        this.estadoPago = "";
        this.productos = getProductos();
    }

    public String getUuidPedido() {
        return uuidPedido;
    }

    public void setUuidPedido(String uuidPedido) {
        this.uuidPedido = uuidPedido;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public int getModificado() {
        return modificado;
    }

    public void setModificado(int modificado) {
        this.modificado = modificado;
    }

    public int getId_pago() {
        return id_pago;
    }

    public void setId_pago(int id_pago) {
        this.id_pago = id_pago;
    }

    public int getId_metodo() {
        return id_metodo;
    }

    public void setId_metodo(int id_metodo) {
        this.id_metodo = id_metodo;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(String montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}


