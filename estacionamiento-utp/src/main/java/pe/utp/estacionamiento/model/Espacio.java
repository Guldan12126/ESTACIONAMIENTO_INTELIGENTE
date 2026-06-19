package pe.utp.estacionamiento.model;

public class Espacio {
    private String id;
    private boolean ocupado;

    public Espacio() {}

    public Espacio(String id, boolean ocupado) {
        this.id = id;
        this.ocupado = ocupado;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public boolean isOcupado() { return ocupado; }
    public void setOcupado(boolean ocupado) { this.ocupado = ocupado; }
}
