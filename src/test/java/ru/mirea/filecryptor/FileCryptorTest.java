package ru.mirea.filecryptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.mirea.filecryptor.crypto.AesStrategy;
import ru.mirea.filecryptor.facade.FileCryptorFacade;
import ru.mirea.filecryptor.model.CryptoTask;

import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class FileCryptorTest {

    @TempDir
    Path tempDir;

    @Test
    void testEncryptAndDecrypt() throws Exception {
        Path original = tempDir.resolve("test.txt");
        Path encrypted = tempDir.resolve("test.txt.enc");
        Path decrypted = tempDir.resolve("restored.txt");

        String text = "Проверка шифрования для РТУ МИРЭА";
        Files.writeString(original, text);

        FileCryptorFacade cryptor = new FileCryptorFacade(new AesStrategy());

        CryptoTask encTask = new CryptoTask.Builder()
                .setInputPath(original.toString())
                .setOutputPath(encrypted.toString())
                .setPassword("Pass12345")
                .build();
        cryptor.processEncryption(encTask);

        CryptoTask decTask = new CryptoTask.Builder()
                .setInputPath(encrypted.toString())
                .setOutputPath(decrypted.toString())
                .setPassword("Pass12345")
                .build();
        cryptor.processDecryption(decTask);

        assertEquals(text, Files.readString(decrypted));
    }
}
