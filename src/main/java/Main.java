import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main {

    public static void main(String[] args) throws Exception {
        // Datos de conexión SSH
        String sshHost = "fi.jcaguilar.dev";
        String sshUser = "patito";
        String sshPass = "cuack";

        // Datos de conexión DB
        String dbHost = "localhost";
        String dbUser = "becario";
        String dbPass = "FdI-its-5a";

        // Establece la conexión
        JSch tunel = new JSch();
        Session sesion = tunel.getSession(sshUser, sshHost);
        sesion.setPassword(sshPass);
        sesion.setConfig("StrictHostKeyChecking", "no");

        // Obtiene un puerto redirigido
        sesion.connect();
        int puertoRedirigido =
                sesion.setPortForwardingL(0, dbHost, 3306);

        // Cadena de conexión
        String cadenaConexion = "jdbc:mariadb://" + dbHost
                + ":" + puertoRedirigido + "/its5a";
        // jdbc:mariadb://localhost:?????/its5a

        try (Connection con =
             DriverManager.getConnection(cadenaConexion, dbUser, dbPass)) {
            Statement sentencia = con.createStatement();
            String sql = "SELECT * FROM personas_escuela";
            ResultSet resultado = sentencia.executeQuery(sql);
            while (resultado.next()) {
                String nombre = resultado.getString(2);
                String apellido = resultado.getString(3);
                System.out.println("Persona: " + nombre + " " + apellido);
            }
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            System.out.println("Conectado a la BD");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
