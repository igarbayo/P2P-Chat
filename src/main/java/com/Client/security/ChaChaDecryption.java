package com.Client.security;

import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ChaChaDecryption {

    private static final int KEY_SIZE = 32; // 256 bits
    private static final int NONCE_SIZE = 8; // 96 bits
    private static final String BASE_PATH = "src/main/resources/com/Client/";

    // Función de descifrado estática
    public static String decryptMessage(String encryptedPasswordHex, byte[] key, byte[] nonce) {
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

    // Leer la clave y el nonce de un archivo
    public static KeyNonce readKeyAndNonce(String user, String otherUser) throws Exception {
        String filePath = BASE_PATH + user + "/" + otherUser + ".key";
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }

        // Leer el contenido del archivo
        String content = Files.readString(Paths.get(filePath));
        String[] lines = content.split("\n");

        if (lines.length < 2) {
            throw new IllegalArgumentException("Invalid key file format");
        }

        // Extraer clave y nonce
        String keyBase64 = lines[0].split(": ")[1];
        String nonceBase64 = lines[1].split(": ")[1];

        byte[] key = Base64.getDecoder().decode(keyBase64);
        byte[] nonce = Base64.getDecoder().decode(nonceBase64);

        return new KeyNonce(key, nonce);
    }

    // Función para leer y desencriptar
    public static String decryptForUsers(String user, String otherUser, String encryptedMessage) {
        try {
            // Leer clave y nonce del archivo
            KeyNonce keyNonce = readKeyAndNonce(user, otherUser);

            // Desencriptar mensaje
            return decryptMessage(encryptedMessage, keyNonce.key, keyNonce.nonce);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error decrypting message: " + e.getMessage();
        }
    }

    // Clase auxiliar para clave y nonce
    public static class KeyNonce {
        byte[] key;
        byte[] nonce;

        public byte[] getKey() {
            return key;
        }

        public byte[] getNonce() {
            return nonce;
        }

        public KeyNonce(byte[] key, byte[] nonce) {
            this.key = key;
            this.nonce = nonce;
        }
    }

}
