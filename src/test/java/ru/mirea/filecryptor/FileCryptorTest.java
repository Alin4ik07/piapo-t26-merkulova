package ru.mirea.filecryptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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

        String text = "Р В РЎСџР РЋР вЂљР В РЎвЂўР В Р вЂ Р В Р’ВµР РЋР вЂљР В РЎвЂќР В Р’В° Р РЋРІвЂљВ¬Р В РЎвЂР РЋРІР‚С›Р РЋР вЂљР В РЎвЂўР В Р вЂ Р В Р’В°Р В Р вЂ¦Р В РЎвЂР РЋР РЏ Р В РўвЂР В Р’В»Р РЋР РЏ Р В Р’В Р В РЎС›Р В Р в‚¬ Р В РЎС™Р В Р’ВР В Р’В Р В Р’В­Р В РЎвЂ™";
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
