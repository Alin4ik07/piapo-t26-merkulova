package ru.mirea.filecryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AesStrategy implements EncryptionStrategy {

    private SecretKeySpec getKey(String password) throws Exception {
        byte[] key = MessageDigest.getInstance("SHA-256")
                .digest(password.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
    }

    @Override
    public byte[] encrypt(byte[] data, String password) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getKey(password));
        return cipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, String password) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, getKey(password));
        return cipher.doFinal(encryptedData);
    }
}

