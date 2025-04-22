package com.firma.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Base64;

public class HashUtils {
    private static final String ALGORITHM = "AES";
    private static final String GRUPAL_KEY = "FirmaGrupalKey123";
    private static final String ADMIN_KEY = "FirmaAdminKey1234";

    public static byte[] encrypt(byte[] content, String certificateType) throws Exception {
        String key = certificateType.equals("Firma Grupal") ? GRUPAL_KEY : ADMIN_KEY;
        SecretKeySpec secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(content);
    }

    public static byte[] decrypt(byte[] encrypted, String certificateType) throws Exception {
        String key = certificateType.equals("Firma Grupal") ? GRUPAL_KEY : ADMIN_KEY;
        SecretKeySpec secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encrypted);
    }

    private static SecretKeySpec generateKey(String key) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(key.getBytes("UTF-8"));
        return new SecretKeySpec(hash, "AES");
    }

    public static String generateFileHash(byte[] content) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(content);
        return Base64.getEncoder().encodeToString(hash);
    }

    public static void processFile(Path source, Path target, String certificateType, boolean isEncrypting) throws Exception {
        byte[] content = Files.readAllBytes(source);
        byte[] processed;
        if (isEncrypting) {
            processed = encrypt(content, certificateType);
        } else {
            processed = decrypt(content, certificateType);
        }
        Files.write(target, processed);
    }
}