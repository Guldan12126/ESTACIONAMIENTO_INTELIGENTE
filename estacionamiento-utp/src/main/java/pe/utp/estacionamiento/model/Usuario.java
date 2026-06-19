package pe.utp.estacionamiento.model;

public class Usuario {
    private String codigo;
    private String password;
    private String rol;

    public Usuario() {}

    public Usuario(String codigo, String password, String rol) {
        this.codigo = codigo;
        this.password = password;
        this.rol = rol;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
