package com.Client;

import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.nio.charset.StandardCharsets;

public class ChaChaDecryption {

    private static final int KEY_SIZE = 32; // 256 bits
    private static final int NONCE_SIZE = 8; // 96 bits

    // Función de descifrado estática
    public static String decryptPassword(String encryptedPasswordHex, byte[] key, byte[] nonce) {
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
        engine.init(false, params); // false para descifrado

        // Convertir la cadena hexadecimal a bytes y descifrarla
        byte[] encryptedBytes = Hex.decode(encryptedPasswordHex);
        byte[] decrypted = new byte[encryptedBytes.length];
        engine.processBytes(encryptedBytes, 0, encryptedBytes.length, decrypted, 0);

        // Devolver los datos descifrados como una cadena
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
