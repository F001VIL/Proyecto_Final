import dao.UsuarioDAO;
import modelo.Usuario;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UsuarioDAO dao = new UsuarioDAO();

        System.out.println("===== SISTEMA DE BIBLIOTECA UNIVERSITARIA =====");

        int intentos = 0;
        Usuario u = null;

        // Intentos máximos
        while (intentos < 3 && u == null) {
            System.out.print("Usuario: ");
            String user = sc.nextLine();
            System.out.print("Contraseña: ");
            String pass = sc.nextLine();

            u = dao.validarLogin(user, pass);

            if (u == null) {
                intentos++;
                System.out.println("❌ Credenciales inválidas o usuario inactivo. Intento " + intentos + " de 3.");
                if (intentos == 3) {
                    System.out.println("🚫 Demasiados intentos fallidos. Cerrando el sistema...");
                    return;
                }
            }
        }

        if (u == null) return;

        // Primer inicio de sesión
        if (u.isPrimerInicio()) {
            System.out.println("\n⚠️ Es su primer inicio de sesión. Debe cambiar su contraseña.");
            System.out.print("Ingrese nueva contraseña: ");
            String nuevaPass = sc.nextLine();
            if (dao.cambiarPassword(u.getId(), nuevaPass)) {
                System.out.println("✅ Contraseña cambiada correctamente. Vuelva a iniciar sesión.");
                return;
            } else {
                System.out.println("❌ Error al cambiar la contraseña. Contacte al administrador.");
                return;
            }
        }

        System.out.println("\n👋 Bienvenido " + u.getUsername() + " (" + u.getRol() + ")");

        if (u.getRol().equalsIgnoreCase("Administrador")) {
            menuAdministrador(sc, dao);
        } else {
            System.out.println("🔒 Acceso restringido. Solo los administradores pueden gestionar usuarios.");
        }
    }

    // ==============================
    // MENÚ ADMINISTRADOR
    // ==============================
    private static void menuAdministrador(Scanner sc, UsuarioDAO dao) {
        while (true) {
            System.out.println("\n--- MENÚ ADMINISTRADOR ---");
            System.out.println("1. Crear nuevo usuario");
            System.out.println("2. Listar usuarios");
            System.out.println("3. Cambiar contraseña de usuario");
            System.out.println("4. Eliminar usuario");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion;
            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida, intente de nuevo.");
                continue;
            }

            switch (opcion) {
                case 1 -> crearUsuario(sc, dao);
                case 2 -> listarUsuarios(dao);
                case 3 -> cambiarPasswordUsuario(sc, dao);
                case 4 -> eliminarUsuario(sc, dao);
                case 5 -> {
                    System.out.println("👋 Cerrando sesión...");
                    return;
                }
                default -> System.out.println("⚠️ Opción no válida.");
            }
        }
    }

    // ==============================
    // CREAR NUEVO USUARIO
    // ==============================
    private static void crearUsuario(Scanner sc, UsuarioDAO dao) {
        try {
            System.out.print("Código Universitario: ");
            String codigo = sc.nextLine();
            System.out.print("Nombres: ");
            String nombre = sc.nextLine();
            System.out.print("Apellidos: ");
            String apellido = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();

            System.out.print("Nuevo nombre de usuario: ");
            String username = sc.nextLine();
            System.out.print("Contraseña inicial: ");
            String password = sc.nextLine();
            System.out.print("Rol (Alumno/Profesor/Bibliotecario/Administrador): ");
            String rol = sc.nextLine();

            boolean exito = dao.crearUsuarioConPersona(codigo, nombre, apellido, email, username, password, rol);
            if (exito)
                System.out.println("Usuario y persona creados correctamente. Deberá cambiar su contraseña al primer inicio.");
            else
                System.out.println("Error al crear usuario/persona (verifique duplicados o datos inválidos).");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==============================
    // LISTAR USUARIOS
    // ==============================
    private static void listarUsuarios(UsuarioDAO dao) {
        List<Usuario> usuarios = dao.listarUsuarios();
        System.out.println("\n--- LISTA DE USUARIOS ---");
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        for (Usuario usr : usuarios) {
            System.out.printf("ID: %d | PersonaID: %d | Usuario: %s | Rol: %s | Activo: %s | Primer inicio: %s%n",
                    usr.getId(),
                    usr.getPersonaId(),
                    usr.getUsername(),
                    usr.getRol(),
                    usr.isActivo() ? "Sí" : "No",
                    usr.isPrimerInicio() ? "Sí" : "No");
        }
    }

    // ==============================
    // CAMBIAR CONTRASEÑA (ADMIN)
    // ==============================
    private static void cambiarPasswordUsuario(Scanner sc, UsuarioDAO dao) {
        try {
            System.out.print("Ingrese ID del usuario: ");
            int id = Integer.parseInt(sc.nextLine());
            System.out.print("Nueva contraseña: ");
            String nueva = sc.nextLine();

            if (dao.cambiarPassword(id, nueva))
                System.out.println("Contraseña actualizada correctamente.");
            else
                System.out.println("No se pudo actualizar la contraseña.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==============================
    // ELIMINAR USUARIO (ADMIN)
    // ==============================
    private static void eliminarUsuario(Scanner sc, UsuarioDAO dao) {
        try {
            System.out.print("Ingrese ID del usuario a eliminar: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("¿Está seguro que desea eliminar este usuario? (s/n): ");
            String confirm = sc.nextLine().trim().toLowerCase();

            if (!confirm.equals("s")) {
                System.out.println("Operación cancelada.");
                return;
            }

            if (dao.eliminarUsuario(id))
                System.out.println("Usuario eliminado correctamente.");
            else
                System.out.println("No se pudo eliminar (ID inexistente o error).");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
