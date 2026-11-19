package ru.mirea.filecryptor;

public interface EncryptionStrategy {
    byte[] encrypt(byte[] data, String password) throws Exception;
    byte[] decrypt(byte[] encryptedData, String password) throws Exception;
}

