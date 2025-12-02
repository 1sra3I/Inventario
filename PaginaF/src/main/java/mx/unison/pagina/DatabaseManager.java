/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.unison.pagina;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author rober
 */
public class DatabaseManager {
    private static final String DB_NAME = "InventarioBD_2.db";
    private static String dbFilePath = null;
    
    public static void inicializar() {
        try {
            // Intentar ruta de desarrollo
            File devDB = new File("src/main/resources/" + DB_NAME);
            
            if (devDB.exists() && devDB.canRead()) {
                // Modo desarrollo
                dbFilePath = "jdbc:sqlite:src/main/resources/" + DB_NAME;
                System.out.println(" Modo DESARROLLO");
                System.out.println(" Usando: " + devDB.getAbsolutePath());
                return;
            }
            
            // Modo producción - Buscar en resources del JAR
            System.out.println(" Modo PRODUCCIÓN (JAR)");
            
            // Crear directorio en carpeta de usuario
            File userDir = new File(System.getProperty("user.home"), ".pagina_unison");
            if (!userDir.exists()) {
                userDir.mkdirs();
                System.out.println(" Creada carpeta: " + userDir.getAbsolutePath());
            }
            
            File dbFile = new File(userDir, DB_NAME);
            
            // Si no existe, intentar copiar desde JAR
            if (!dbFile.exists()) {
                System.out.println(" Copiando BD desde JAR...");
                boolean copiado = copiarDesdeJAR(dbFile);
                
                if (!copiado) {
                    // Si falla, intentar con ruta alternativa
                    System.out.println("⚠️ Intento 1 falló. Probando ruta alternativa...");
                    copiado = copiarConRutaAlternativa(dbFile);
                }
                
                if (!copiado) {
                    throw new IOException("No se pudo copiar la base de datos desde el JAR");
                }
                
                System.out.println(" BD copiada a: " + dbFile.getAbsolutePath());
            } else {
                System.out.println(" Usando BD existente: " + dbFile.getAbsolutePath());
            }
            
            dbFilePath = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            
        } catch (Exception e) {
            System.err.println(" ERROR CRÍTICO: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null,
                "Error crítico al inicializar la base de datos:\n\n" + 
                e.getMessage() + "\n\n" +
                "Asegúrate de que inventarioBD_2.db esté en src/main/resources/\n" +
                "y recompila el proyecto con 'Clean and Build'",
                "Error Fatal",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private static boolean copiarDesdeJAR(File destino) {
        try {
            
            InputStream input = DatabaseManager.class.getResourceAsStream("/" + DB_NAME);
            
            if (input == null) {
                System.out.println(" No encontrado con ruta: /" + DB_NAME);
                return false;
            }
            
            copiarStream(input, destino);
            return true;
            
        } catch (IOException e) {
            System.err.println(" Error en copia: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean copiarConRutaAlternativa(File destino) {
        try {
            InputStream input = DatabaseManager.class.getResourceAsStream(DB_NAME);
            
            if (input == null) {
                System.out.println(" No encontrado con ruta: " + DB_NAME);
                
                // Intentar desde ClassLoader
                input = DatabaseManager.class.getClassLoader().getResourceAsStream(DB_NAME);
                
                if (input == null) {
                    System.out.println(" No encontrado con ClassLoader");
                    return false;
                }
            }
            
            copiarStream(input, destino);
            return true;
            
        } catch (IOException e) {
            System.err.println(" Error en copia alternativa: " + e.getMessage());
            return false;
        }
    }
    
    private static void copiarStream(InputStream input, File destino) throws IOException {
        try (FileOutputStream output = new FileOutputStream(destino)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytes = 0;
            
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            
            System.out.println(" Copiados " + totalBytes + " bytes");
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (dbFilePath == null) {
            throw new SQLException("La base de datos no ha sido inicializada. Llama a DatabaseManager.inicializar() primero.");
        }
        
        Connection conn = DriverManager.getConnection(dbFilePath);
        
        // Configurar SQLite
        java.sql.Statement stmt = conn.createStatement();
        stmt.execute("PRAGMA journal_mode=WAL;");
        stmt.execute("PRAGMA busy_timeout=5000;");
        stmt.close();
        
        return conn;
    }
    
    public static String getDBPath() {
        return dbFilePath;
    }
}

