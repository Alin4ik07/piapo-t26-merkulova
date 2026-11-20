package ru.mirea.filecryptor.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mirea.filecryptor.crypto.EncryptionStrategy;
import ru.mirea.filecryptor.model.CryptoTask;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileCryptorFacade {
    private static final Logger log = LoggerFactory.getLogger(FileCryptorFacade.class);
    private EncryptionStrategy strategy;

    public FileCryptorFacade(EncryptionStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(EncryptionStrategy strategy) {
        this.strategy = strategy;
    }

    public void processEncryption(CryptoTask task) throws Exception {
        Path source = Path.of(task.getInputPath());
        Path target = Path.of(task.getOutputPath());

        byte[] fileBytes = Files.readAllBytes(source);
        byte[] encryptedBytes = strategy.encrypt(fileBytes, task.getPassword());
        Files.write(target, encryptedBytes);
        log.info("Файл успешно зашифрован: {} -> {}", source, target);

        if (task.isDeleteOriginal()) {
            Files.deleteIfExists(source);
            log.info("Исходный файл удален: {}", source);
        }
    }

    public void processDecryption(CryptoTask task) throws Exception {
        Path source = Path.of(task.getInputPath());
        Path target = Path.of(task.getOutputPath());

        byte[] encryptedBytes = Files.readAllBytes(source);
        byte[] decryptedBytes = strategy.decrypt(encryptedBytes, task.getPassword());
        Files.write(target, decryptedBytes);
        log.info("Файл успешно расшифрован: {} -> {}", source, target);

        if (task.isDeleteOriginal()) {
            Files.deleteIfExists(source);
        }
    }
}
