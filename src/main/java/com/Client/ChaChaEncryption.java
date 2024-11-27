package com.Client;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class ChaChaEncryption {

    private static final int KEY_SIZE = 32; // 256 bits
    private static final int NONCE_SIZE = 8; // 96 bits

    // Función de cifrado estática
    public static String encryptPassword(String password, byte[] key, byte[] nonce) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 32 bytes (256 bits).");
        }
        if (nonce.length != NONCE_SIZE) {
            throw new IllegalArgumentException("Nonce must be 12 bytes (96 bits).");
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


}

