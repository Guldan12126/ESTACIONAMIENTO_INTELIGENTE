package pe.utp.estacionamiento.model;

public class Vehiculo {
    private String placa;
    private String propietarioCodigo;

    public Vehiculo() {}

    public Vehiculo(String placa, String propietarioCodigo) {
        this.placa = placa;
        this.propietarioCodigo = propietarioCodigo;
    }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getPropietarioCodigo() { return propietarioCodigo; }
    public void setPropietarioCodigo(String propietarioCodigo) { this.propietarioCodigo = propietarioCodigo; }
}
