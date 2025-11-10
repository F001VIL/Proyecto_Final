import dao.UsuarioDAO;

public class CrearAdmin {
    public static void main(String[] args) {
        System.out.println(">> Creando usuario administrador...");

        UsuarioDAO dao = new UsuarioDAO();

        boolean exito = dao.crearUsuarioConPersona(
                "ADM001",
                "Admin",
                "Principal",
                "admin@utp.edu",
                "admin",
                "admin123",
                "Administrador"
        );

        if (exito)
            System.out.println("Usuario administrador creado correctamente.");
        else
            System.out.println("Error al crear el usuario administrador.");
    }
}
