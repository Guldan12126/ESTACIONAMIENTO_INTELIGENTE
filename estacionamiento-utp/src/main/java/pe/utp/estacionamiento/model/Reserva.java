package pe.utp.estacionamiento.model;

public class Reserva {
    private String id;
    private String vehiculoPlaca;
    private String espacioId;
    private boolean activa;
    private boolean vencida;

    public Reserva() {}

    public Reserva(String id, String vehiculoPlaca, String espacioId, boolean activa, boolean vencida) {
        this.id = id;
        this.vehiculoPlaca = vehiculoPlaca;
        this.espacioId = espacioId;
        this.activa = activa;
        this.vencida = vencida;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
    public String getEspacioId() { return espacioId; }
    public void setEspacioId(String espacioId) { this.espacioId = espacioId; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public boolean isVencida() { return vencida; }
    public void setVencida(boolean vencida) { this.vencida = vencida; }
}
