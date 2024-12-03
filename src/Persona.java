public class Persona {
    private String ID;
    private String nombre;
    private String correo;

    public Persona(String id, String nombre, String correo) {
        this.ID = ID;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getID() {
        return ID;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public String toString() {
        return ID + "," + nombre + "," + correo;
    }

    public static Persona fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            return new Persona(parts[0], parts[1], parts[2]);
        }
        return null;
    }
}
