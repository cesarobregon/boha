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

    // Define una clave fija en base64 (por ejemplo, una clave de 256 bits para AES)
    private static final String FIXED_KEY = "0123456789abcdef0123456789abcdef";

    // Método para obtener la clave fija
    public static SecretKey getFixedKey() {
        byte[] decodedKey = FIXED_KEY.getBytes();
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
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

