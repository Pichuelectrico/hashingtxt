package com.firma.model;

import java.time.LocalDate;

public class Firma {
    private String cedula;
    private String nombre;
    private String institucion;
    private String cargo;
    private LocalDate fecha;
    private String archivoOriginal;
    private String archivoFirmado;
    private String certificadoUsado;
    private String archivoHash;

    public Firma() {
        this.fecha = LocalDate.now();
    }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getArchivoOriginal() { return archivoOriginal; }
    public void setArchivoOriginal(String archivoOriginal) { this.archivoOriginal = archivoOriginal; }

    public String getArchivoFirmado() { return archivoFirmado; }
    public void setArchivoFirmado(String archivoFirmado) { this.archivoFirmado = archivoFirmado; }

    public String getCertificadoUsado() { return certificadoUsado; }
    public void setCertificadoUsado(String certificadoUsado) { this.certificadoUsado = certificadoUsado; }

    public String getArchivoHash() { return archivoHash; }
    public void setArchivoHash(String archivoHash) { this.archivoHash = archivoHash; }
}