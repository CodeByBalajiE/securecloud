package com.example.securecloud.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESUtil {

    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_LENGTH = 12; // bytes

    private static final SecureRandom random = new SecureRandom();

    // 🔒 Encrypt: returns IV + ciphertext
    public static byte[] encrypt(byte[] data, String keyRaw, String uuid) throws Exception {
        byte[] keyBytes = keyRaw.getBytes(StandardCharsets.ISO_8859_1);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        byte[] encrypted = cipher.doFinal(data);

        // combine IV + ciphertext
        byte[] combined = new byte[IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
        System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

        return combined;
    }

    // 🔓 Decrypt: extracts IV + decrypts
    public static byte[] decrypt(byte[] cipherData, String keyRaw, String uuid) throws Exception {
        byte[] keyBytes = keyRaw.getBytes(StandardCharsets.ISO_8859_1);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = Arrays.copyOfRange(cipherData, 0, IV_LENGTH);
        byte[] encrypted = Arrays.copyOfRange(cipherData, IV_LENGTH, cipherData.length);

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        return cipher.doFinal(encrypted);
    }
}
