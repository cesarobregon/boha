package com.example.sistema_boha.controladores;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Método para generar una clave secreta
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // Para AES-256
        return keyGen.generateKey();
    }

    // Método para encriptar el texto
    public static String encrypt(String value, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedValue = cipher.doFinal(value.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedValue, Base64.DEFAULT);
    }

    // Método para desencriptar el texto
    public static String decrypt(String value, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue = cipher.doFinal(Base64.decode(value, Base64.DEFAULT));
        return new String(decryptedValue, "UTF-8");
    }
}

