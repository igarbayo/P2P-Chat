// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Client.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Bcrypt {
    public static String hashPassword(String password) {
        // Generar el hash usando bcrypt
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        // Verificar la contraseña con el hash
        return BCrypt.checkpw(password, hashedPassword);
    }
}
