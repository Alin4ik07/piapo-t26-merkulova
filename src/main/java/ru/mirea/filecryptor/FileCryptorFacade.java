package ru.mirea.filecryptor;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileCryptorFacade {
    private EncryptionStrategy strategy;

    public FileCryptorFacade(EncryptionStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(EncryptionStrategy strategy) {
        this.strategy = strategy;
    }

    // Р В Р РѓР В РЎвЂР РЋРІР‚С›Р РЋР вЂљР В РЎвЂўР В Р вЂ Р В Р’В°Р В Р вЂ¦Р В РЎвЂР В Р’Вµ Р РЋРІР‚С›Р В Р’В°Р В РІвЂћвЂ“Р В Р’В»Р В Р’В°
    public void processEncryption(CryptoTask task) throws Exception {
        Path source = Path.of(task.getInputPath());
        Path target = Path.of(task.getOutputPath());

        byte[] fileBytes = Files.readAllBytes(source);
        byte[] encryptedBytes = strategy.encrypt(fileBytes, task.getPassword());
        Files.write(target, encryptedBytes);

        if (task.isDeleteOriginal()) {
            Files.deleteIfExists(source);
        }
    }

    // Р В РІР‚СњР В Р’ВµР РЋРІвЂљВ¬Р В РЎвЂР РЋРІР‚С›Р РЋР вЂљР В РЎвЂўР В Р вЂ Р В Р’В°Р В Р вЂ¦Р В РЎвЂР В Р’Вµ Р РЋРІР‚С›Р В Р’В°Р В РІвЂћвЂ“Р В Р’В»Р В Р’В°
    public void processDecryption(CryptoTask task) throws Exception {
        Path source = Path.of(task.getInputPath());
        Path target = Path.of(task.getOutputPath());

        byte[] encryptedBytes = Files.readAllBytes(source);
        byte[] decryptedBytes = strategy.decrypt(encryptedBytes, task.getPassword());
        Files.write(target, decryptedBytes);

        if (task.isDeleteOriginal()) {
            Files.deleteIfExists(source);
        }
    }
}

