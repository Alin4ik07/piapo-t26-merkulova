package ru.mirea.filecryptor.crypto;

public interface EncryptionStrategy {
    byte[] encrypt(byte[] data, String password) throws Exception;
    byte[] decrypt(byte[] encryptedData, String password) throws Exception;
}
