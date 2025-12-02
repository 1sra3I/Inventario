/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.unison.pagina;
import org.mindrot.jbcrypt.BCrypt;
/**
 *
 * @author rober
 */
public class Seguridad {
    // Genera el hash seguro
    public static String hashPassword(String contraseña) {
        return BCrypt.hashpw(contraseña, BCrypt.gensalt(12)); 
    }
    // Verifica la contraseña contra el hash guardado
    public static boolean verificarPassword(String contraseña, String hash) {
        return BCrypt.checkpw(contraseña, hash);
    }
}

