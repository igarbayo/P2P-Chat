// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Client.security;

import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

public class ChaChaEncryption {

    private static final int KEY_SIZE = 32; // 256 bits
    private static final int NONCE_SIZE = 8; // 96 bits
    private static final String BASE_PATH = "src/main/resources/com/Client/";

    // Función de cifrado estática
    public static String encryptMessage(String password, byte[] key, byte[] nonce) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 32 bytes (256 bits).");
        }
        if (nonce.length != NONCE_SIZE) {
            throw new IllegalArgumentException("Nonce must be 8 bytes.");
        }

        // Crear el motor ChaCha20 y el parámetro de clave
        ChaChaEngine engine = new ChaChaEngine(20); // 20 rounds for ChaCha20
        KeyParameter keyParameter = new KeyParameter(key);

        // Configurar el motor con la clave y el nonce (IV)
        ParametersWithIV params = new ParametersWithIV(keyParameter, nonce);
        engine.init(true, params); // true para cifrado

        // Convertir la contraseña a bytes y cifrarla
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = new byte[passwordBytes.length];
        engine.processBytes(passwordBytes, 0, passwordBytes.length, encrypted, 0);

        // Devolver los datos cifrados como una cadena hexadecimal
        return Hex.toHexString(encrypted);
    }

    // Generar una clave única para un par de usuarios
    public static byte[] generateKey(String user1, String user2) {
        try {
            // Asegurar que el orden sea consistente
            String combinedUsers = user1.compareTo(user2) < 0 ? user1 + user2 : user2 + user1;

            // Generar una clave basada en los usuarios combinados
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(combinedUsers.getBytes());

            byte[] key = new byte[KEY_SIZE]; // Clave de 256 bits
            secureRandom.nextBytes(key);
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Error generating key", e);
        }
    }

    // Generar un nonce único para cada sesión
    public static byte[] generateNonce() {
        byte[] nonce = new byte[NONCE_SIZE]; // ChaCha20 usa nonces de 96 bits (8 bytes)
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(nonce);
        return nonce;
    }

}

